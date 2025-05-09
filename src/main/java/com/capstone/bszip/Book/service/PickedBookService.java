package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.PickedBook;
import com.capstone.bszip.Book.repository.PickedBookRepository;
import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickedBookService {

    private final PickedBookRepository pickedBookRepository;

    public void savePickedBook(Book book, Member member) {
        PickedBook pickedBook = PickedBook.builder()
                .book(book)
                .member(member)
                .build();
        pickedBookRepository.save(pickedBook);
    }

    public void deletePickedBook(Book book, Member member) {
        PickedBook pickedBook = pickedBookRepository.findByBookAndMember(book, member).orElseThrow(()-> new EntityNotFoundException("Picked Book not found"));
        pickedBookRepository.delete(pickedBook);
    }

    public Boolean existsPickedBook(Book book, Member member) {
        return pickedBookRepository.existsByBookAndMember(book, member);
    }
}
