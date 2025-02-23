package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.capstone.bszip.Bookstore.domain.BookstoreCategory.CHILD;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    @Transactional
    public List<BookstoreResponse> searchBookstores(String keyword) {
        List<Bookstore> bookstores = bookstoreRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword);

        return bookstores.stream()
                .map(this::convertToBookstoreResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookstoreResponse> getBookstoresByCategory(BookstoreCategory category){
        if(category == null){
            List <Bookstore> bookstores = bookstoreRepository.findAll();
            return bookstores.stream()
                    .map(this::convertToBookstoreResponse)
                    .collect(Collectors.toList());
        }
        List <Bookstore> bookstores =bookstoreRepository.findByBookstoreCategory(category);
        return bookstores.stream()
                .map(this::convertToBookstoreResponse)
                .collect(Collectors.toList());
    }

    private BookstoreResponse convertToBookstoreResponse (Bookstore bookstore){
        String addressExceptCode = bookstore.getAddress();
        if(bookstore.getBookstoreCategory()!=CHILD) {
            addressExceptCode =addressExceptCode.substring(8);
        }
        return new BookstoreResponse(
                bookstore.getName(),
                bookstore.getRating(),
                bookstore.getBookstoreCategory(),
                addressExceptCode
        );
    }

}
