package com.vizdizbot.service;

import com.vizdizbot.entity.Message;
import com.vizdizbot.entity.Users;
import com.vizdizbot.repository.MessageRepository;
import com.vizdizbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final VizDizBot telegramBotService;

    public void sendMessage(String username, String text) {
        Users user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Message message = Message.builder()
            .user(user)
            .message(text)
            .createdAt(LocalDateTime.now())
            .build();

        messageRepository.save(message);

        String formatted = String.format("%s, я получил от тебя сообщение:\n%s", user.getName(), text);

        telegramBotService.sendMessage(user.getTelegramChatId(), formatted);
    }

    public List<Message> getMessages(String username) {
        Users user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return messageRepository.findByUserOrderByCreatedAtDesc(user);
    }
}

