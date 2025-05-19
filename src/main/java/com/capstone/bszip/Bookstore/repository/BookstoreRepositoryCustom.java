package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BookstoreRepositoryCustom {
    List<Bookstore> findWithFiltersOrderByDistance(Specification<Bookstore> spec,
                                                   double userLat,
                                                   double userLng);
    List<Bookstore> findWithFiltersOrderByRating(Specification<Bookstore> spec);

    List<Bookstore> findWithFiltersOrderByLikes(Specification<Bookstore> spec);
}
