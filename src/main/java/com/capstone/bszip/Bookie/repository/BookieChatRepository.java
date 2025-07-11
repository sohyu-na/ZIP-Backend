package com.capstone.bszip.Bookie.repository;

import com.capstone.bszip.Bookie.domain.BookieChat;
import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookieChatRepository extends JpaRepository<BookieChat, Long> {
    List<BookieChat> findByMemberAndCreatedDateAfterOrderByCreatedDate(Member member, LocalDateTime from);
}
