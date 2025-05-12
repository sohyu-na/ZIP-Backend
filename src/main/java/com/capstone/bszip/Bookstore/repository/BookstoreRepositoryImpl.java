package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class BookstoreRepositoryImpl implements BookstoreRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Bookstore> findWithFiltersOrderByDistance(Specification<Bookstore> spec,
                                                          double userLat,
                                                          double userLng){
        // spec 조건으로 1차 필터링 
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Bookstore> root = query.from(Bookstore.class);

        if(spec != null) {
            query.where(spec.toPredicate(root,query,builder));
        }

        query.select(root.get("bookstoreId"));
        List<Long> filteredIds = em.createQuery(query).getResultList();

        // native query로 2차 거리순 필터링
        if(filteredIds.isEmpty()) {
            return Collections.emptyList();
        }
        String nativeQuery = "SELECT b.*, " +
                "(6371 * acos(cos(radians(:userLat)) * cos(radians(b.latitude)) " +
                "* cos(radians(b.longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(b.latitude)))) " +
                "AS distance " +
                "FROM bookstores b " +
                "WHERE b.bookstore_id IN (:filteredIds) " +
                "ORDER BY distance ASC";

        org.hibernate.query.NativeQuery<?> hibernateQuery =
                em.createNativeQuery(nativeQuery, Bookstore.class)
                        .unwrap(org.hibernate.query.NativeQuery.class);

        hibernateQuery.setParameter("userLat", userLat);
        hibernateQuery.setParameter("userLng", userLng);
        hibernateQuery.setParameterList("filteredIds", filteredIds);

        return (List<Bookstore>) hibernateQuery.getResultList();
    }
}
