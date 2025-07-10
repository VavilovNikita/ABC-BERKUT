package com.vizdizbot.service;

import com.vizdizbot.entity.Message;
import com.vizdizbot.entity.Users;
import com.vizdizbot.repository.MessageRepository;
import com.vizdizbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final VizDizBot telegramBotService;

    public ResponseEntity<String> sendMessage(String username, String text) {
        Users user = userRepository.findByUsername(username)
            .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Пользователь не найден");
        }

        Message message = Message.builder()
            .user(user)
            .message(text)
            .createdAt(LocalDateTime.now())
            .build();

        messageRepository.save(message);

        String formatted = String.format("%s, я получил от тебя сообщение:\n%s", user.getName(), text);
        telegramBotService.sendMessage(user.getTelegramChatId(), formatted);

        return ResponseEntity.ok("Сообщение успешно отправлено");
    }

    public ResponseEntity<List<Message>> getMessages(String username) {
        Users user = userRepository.findByUsername(username)
            .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Message> messages = messageRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(messages);
    }
}

