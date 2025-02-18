package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookstoreRepository extends JpaRepository<Bookstore,Long> {
    List<Bookstore> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

    List<Bookstore> findByBookstoreCategory(BookstoreCategory category);}
