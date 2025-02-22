package com.capstone.bszip.Bookstore.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="bookstores")
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

    @Column(name = "description")
    private String description;

    @Column(name="rating")
    private Double rating;

}
