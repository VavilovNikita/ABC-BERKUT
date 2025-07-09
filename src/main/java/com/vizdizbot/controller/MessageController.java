package com.vizdizbot.controller;

import com.vizdizbot.entity.Message;
import com.vizdizbot.entity.Users;
import com.vizdizbot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message request, Principal principal) {
        String username = principal.getName();
        messageService.sendMessage(username, request.getMessage());
        return ResponseEntity.ok("Сообщение отправлено в Telegram");
    }


    @GetMapping
    public ResponseEntity<List<Message>> getMessages(@AuthenticationPrincipal Users userDetails) {
        return ResponseEntity.ok(messageService.getMessages(userDetails.getUsername()));
    }
}

