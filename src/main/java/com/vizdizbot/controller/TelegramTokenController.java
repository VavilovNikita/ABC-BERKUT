package com.vizdizbot.controller;

import com.vizdizbot.entity.Users;
import com.vizdizbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class TelegramTokenController {
    private final UserRepository userRepository;

    @PostMapping("telegram/token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> generateTelegramToken(Authentication authentication) {
        System.out.println(authentication.getName());
        Users user = userRepository.findByUsername(authentication.getName()).get();
        String token = UUID.randomUUID().toString();
        user.setTelegramToken(token);
        userRepository.save(user);
        return ResponseEntity.ok(token);
    }
}
