package com.capstone.bszip.Bookie.controller;

import com.capstone.bszip.Bookie.dto.request.ChatRequest;
import com.capstone.bszip.Bookie.service.BookieChatService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookie")
@RequiredArgsConstructor
public class BookieChatController {

    private final BookieChatService bookieChatService;

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@AuthenticationPrincipal Member member) {
        try{
            return ResponseEntity.ok(bookieChatService.getChatHistory(member));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<?> getChat(@AuthenticationPrincipal Member member, ChatRequest chatRequest) throws JsonProcessingException {
        try{
            return ResponseEntity.ok(bookieChatService.getChat(member, chatRequest));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
