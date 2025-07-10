package com.vizdizbot.controller;

import com.vizdizbot.entity.Message;
import com.vizdizbot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendMessage(@RequestBody Message request, Principal principal) {
        String username = principal.getName();
        return messageService.sendMessage(username, request.getMessage());
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMessages(Principal principal) {
        String username = principal.getName();
        return messageService.getMessages(username);
    }
}

