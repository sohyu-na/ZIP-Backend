package com.capstone.bszip.Bookstore.repository;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookstoreRepository extends JpaRepository<Bookstore,Long> {
    List<Bookstore> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

}
