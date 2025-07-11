package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookstoreRepository extends JpaRepository<Bookstore,Long>, JpaSpecificationExecutor<Bookstore>,BookstoreRepositoryCustom{
    @Query(value = "SELECT *, (6371 * acos(cos(radians(:userLat)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:userLng)) + sin(radians(:userLat)) * sin(radians(latitude)))) " +
            "AS distance FROM bookstores WHERE bookstore_id IN :bookstoreIds ORDER BY distance ASC", nativeQuery = true)
    List<Bookstore> findAllByIdOrderByDistance(@Param("bookstoreIds") List<Long> bookstoreIds,
                                               @Param("userLat") double userLat,
                                               @Param("userLng") double userLng);

    List<Bookstore> findAllByNameContaining(String name);

    List<Bookstore> findTop10ByOrderByBookstoreIdDesc();

    @Query("SELECT b.bookstoreId, b.name FROM Bookstore b WHERE b.bookstoreId IN :ids")
    List<Object[]> findIdAndNameByIds(@Param("ids") List<Long> ids);
}
