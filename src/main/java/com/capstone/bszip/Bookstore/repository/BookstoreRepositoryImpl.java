package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BookstoreRepositoryImpl implements BookstoreRepositoryCustom {

    private final EntityManager em;
    private final RedisTemplate<String,String> redisTemplate;

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
    @Override
    public List<Bookstore> findWithFiltersOrderByRating(Specification<Bookstore> spec) {
        // spec 조건으로 1차 필터링
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Bookstore> root = query.from(Bookstore.class);

        if(spec != null) {
            query.where(spec.toPredicate(root,query,builder));
        }

        query.select(root.get("bookstoreId"));
        List<Long> filteredIds = em.createQuery(query).getResultList();

        // native query로 별점순 정렬
        if(filteredIds.isEmpty()) {
            return Collections.emptyList();
        }

        String nativeQuery = "SELECT b.* FROM bookstores b " +
                "WHERE b.bookstore_id IN (:filteredIds) " +
                "ORDER BY b.rating DESC";

        org.hibernate.query.NativeQuery<?> hibernateQuery =
                em.createNativeQuery(nativeQuery, Bookstore.class)
                        .unwrap(org.hibernate.query.NativeQuery.class);

        hibernateQuery.setParameterList("filteredIds", filteredIds);

        return (List<Bookstore>) hibernateQuery.getResultList();
    }

    @Override
    public List<Bookstore> findWithFiltersOrderByLikes(Specification<Bookstore> spec) {
        // spec 조건으로 1차 필터링
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Bookstore> root = query.from(Bookstore.class);

        if (spec != null) {
            query.where(spec.toPredicate(root, query, builder));
        }

        query.select(root.get("bookstoreId"));
        List<Long> filteredIds = em.createQuery(query).getResultList();

        if(filteredIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 필터링된 서점들의 좋아요 수 조회
        Map<Long, Integer> likeCountMap = new HashMap<>();
        for(Long bookstoreId : filteredIds) {
            // 각 서점의 좋아요 수 조회 (bookstore:{id}:likes 형태로 저장된다고 가정)
            String bookstoreKey = "bookstore:" + bookstoreId + ":likes";
            String likeCount = redisTemplate.opsForValue().get(bookstoreKey);
            if(likeCount != null) {
                likeCountMap.put(bookstoreId, Integer.parseInt(likeCount));
            } else {
                likeCountMap.put(bookstoreId, 0); // 좋아요 없는 경우
            }
        }

        // DB에서 서점 정보 조회
        List<Bookstore> bookstores = em.createQuery(
                        "SELECT b FROM Bookstore b WHERE b.bookstoreId IN :ids",
                        Bookstore.class)
                .setParameter("ids", filteredIds)
                .getResultList();

        // 좋아요 수 기준으로 정렬
        bookstores.sort((b1, b2) ->
                Integer.compare(
                        likeCountMap.getOrDefault(b2.getBookstoreId(), 0),
                        likeCountMap.getOrDefault(b1.getBookstoreId(), 0)
                )
        );

        return bookstores;    }
}
