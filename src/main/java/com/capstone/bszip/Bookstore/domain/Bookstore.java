package com.capstone.bszip.Bookstore.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder(toBuilder = true) // toBuilder = true 옵션 추가
@Table(name="bookstores")
@NoArgsConstructor
@AllArgsConstructor
public class Bookstore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookstore_id")
    private Long bookstoreId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category",nullable = false)
    private BookstoreCategory bookstoreCategory;

    @Column(name = "phone")
    private String phone;

    @Column(name = "hours")
    private String hours;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "description")
    private String description;

    @Column(name="rating")
    private double rating;

    @Column(name="ratingCount")
    private int ratingCount;

    @Column(name="keyword")
    private String keyword;

    // 리뷰 작성 -> 별점 업데이트
    public void updateRating(double newRating) {
        double totalRating = this.rating * this.ratingCount;
        this.ratingCount++;
        this.rating = (totalRating + newRating) / this.ratingCount;
    }

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookstoreReview> bookstoreReviews;
}