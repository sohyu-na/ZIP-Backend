package com.capstone.bszip.Member.repository;

import com.capstone.bszip.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    //이메일로 사용자 찾기
    Optional<Member> findByEmail(String email);
}
