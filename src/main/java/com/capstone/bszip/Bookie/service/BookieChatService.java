package com.capstone.bszip.Bookie.service;

import com.capstone.bszip.Book.dto.ProfileUpdateRequest;
import com.capstone.bszip.Bookie.domain.BookieChat;
import com.capstone.bszip.Bookie.dto.request.APIChatRequest;
import com.capstone.bszip.Bookie.dto.request.ChatRequest;
import com.capstone.bszip.Bookie.dto.response.MemberChatHistoryResponse;
import com.capstone.bszip.Bookie.dto.response.MemberChatResponses;
import com.capstone.bszip.Bookie.repository.BookieChatRepository;
import com.capstone.bszip.Member.domain.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookieChatService {

    private final BookieChatRepository bookieChatRepository;

    @Value("${ai.base-uri}")
    String embeddingURI;

    @Transactional(readOnly = true)
    public MemberChatResponses getChatHistory(Member member){
        List<MemberChatHistoryResponse> history = bookieChatRepository.findByMemberOrderByCreatedDateDesc(member)
                .stream().map(MemberChatHistoryResponse::fromEntity).toList();
        return MemberChatResponses.fromChat(history);
    }

    @Transactional
    public String getChat(Member member, ChatRequest chatRequest) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<APIChatRequest> httpEntity = new HttpEntity<>(APIChatRequest.fromEntity(member, chatRequest), httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        String chatJson = restTemplate.postForEntity(embeddingURI+"/chat", httpEntity, String.class).getBody();
        log.info("✅ 응답 : {}", chatJson);
        saveChatToDB(chatJson, member, chatRequest);
        return chatJson;
    }

    @Async
    @Transactional
    public void saveChatToDB(String chatJson, Member member, ChatRequest chatRequest){
        BookieChat bookieChat = BookieChat.builder()
                .question(chatRequest.getMessage())
                .answer(chatJson)
                .member(member)
                .build();
        log.info("{}에 대한 응답 저장", bookieChat.getQuestion());
        bookieChatRepository.save(bookieChat);
    }

}
