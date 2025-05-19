package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag,Long> {
    boolean existsByTag(String tag);

    Optional<Hashtag> findByTag(String tag);

    @Query(value = "SELECT * FROM hashtag ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Hashtag> findRandomHashtags(@Param("limit") int limit);
}
