package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.PickedBook;
import com.capstone.bszip.Book.dto.MyPickBookResponse;
import com.capstone.bszip.Book.dto.MyPickBooksResponse;
import com.capstone.bszip.Book.repository.PickedBookRepository;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.exception.MemberNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public MyPickBooksResponse getMyPickedBooks(Member member) {
        if(member == null) {
            throw new MemberNotFoundException("Member not found");
        }

        List<Book> pickedBooks = pickedBookRepository.findBooksByMember(member);

        List<MyPickBookResponse> myPickBookResponses = pickedBooks.stream().map(MyPickBookResponse::from).toList();
        return MyPickBooksResponse.toMyPickBooksResponse(myPickBookResponses);
    }
}
