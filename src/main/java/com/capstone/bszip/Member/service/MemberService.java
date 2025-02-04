package com.capstone.bszip.Member.service;

import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.Member.repository.MemberRepository;
import com.capstone.bszip.Member.security.JwtUtil;
import com.capstone.bszip.Member.service.dto.LoginRequest;
import com.capstone.bszip.Member.service.dto.SignupRequest;
import com.capstone.bszip.Member.service.dto.SignupAddRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.capstone.bszip.Member.domain.MemberJoinType.DEFAULT;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> temporaryStorage = new HashMap<>(); // 임시 데이터 저장소

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerMember(SignupRequest signupRequest){
        //이메일 중복 확인
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        temporaryStorage.put(signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword())); // 이메일-비밀번호 임시 저장
    }
    @Transactional
    public void registerMemberNickname(String email,String nickname){
        // 이메일이 임시 저장소에 없으면 에러 처리
        if (!temporaryStorage.containsKey(email)) {
            throw new IllegalArgumentException("임시 저장소에 해당 이메일 정보가 없습니다. 다시 진행해주세요.");
        }
        // 임시 저장소에서 비밀번호 가져오기
        String password = temporaryStorage.get(email);

        //닉네임 중복 확인
        if (memberRepository.existsByNickname(nickname)){
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        Member member = new Member();
        //사용자 정보 설정
        member.setEmail(email);
        member.setPassword(password);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        member.setNickname(nickname);
        member.setMemberJoinType(DEFAULT);
        //권한 추가
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        member.setRoles(roles);

        memberRepository.save(member);

        // 임시 저장소에서 데이터 삭제
        temporaryStorage.remove(email);
    }

    @Transactional
    public String loginUser(LoginRequest loginRequest){
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
        if(!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new RuntimeException("일치하지 않는 비밀번호입니다.");
        }
        return JwtUtil.generateToken(member.getEmail());
    }
}

