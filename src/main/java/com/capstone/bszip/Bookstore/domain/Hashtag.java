package com.capstone.bszip.Bookstore.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hashtag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "bookstore_id", unique = true) // 외래 키 + 유니크 제약
    private Bookstore bookstore;

    @Column(unique = true, nullable = false)
    private String tag;
}
