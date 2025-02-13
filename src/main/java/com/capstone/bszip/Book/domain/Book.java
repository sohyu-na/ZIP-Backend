package com.capstone.bszip.Book.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name="Books")
public class Book {
    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "book_image_url", nullable = true)
    private String bookImageUrl;
}
