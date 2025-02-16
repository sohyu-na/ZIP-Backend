package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    @Transactional
    public List<Bookstore> searchBookstores(String keyword) {
        return bookstoreRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword,keyword);
    }

}
