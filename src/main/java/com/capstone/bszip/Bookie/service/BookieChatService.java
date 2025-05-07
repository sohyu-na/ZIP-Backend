package com.capstone.bszip.Bookie.service;

import com.capstone.bszip.Bookstore.repository.BookstoreRepository;
import com.capstone.bszip.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookieChatService {

    private final BookstoreRepository bookstoreRepository;
    private final MemberRepository memberRepository;

}
