package com.capstone.bszip.Bookie.domain;

import com.capstone.bszip.Member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookieChat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 1024)
    String question;

    @Column(length = 1024)
    String answer;

    @CreatedDate
    LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    Member member;

    @Builder
    public BookieChat(String question, String answer, Member member) {
        this.question = question;
        this.answer = answer;
        this.member = member;
    }
}
