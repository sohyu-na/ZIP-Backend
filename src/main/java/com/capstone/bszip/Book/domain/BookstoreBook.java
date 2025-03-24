package com.capstone.bszip.Book.domain;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

// 📖책과 🏠서점을 연결하는 테이블
@Entity
@Table(name="bookstore_books")
@Getter
@NoArgsConstructor
public class BookstoreBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boostore_id", nullable = false)
    private Bookstore bookstore;
}
