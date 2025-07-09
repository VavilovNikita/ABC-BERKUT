package com.vizdizbot.service;

import com.vizdizbot.config.BotProperties;
import com.vizdizbot.entity.Users;
import com.vizdizbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class VizDizBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    @Autowired
    private UserRepository userRepository;


    @Autowired
    public VizDizBot(BotProperties botProperties) {
        this.botProperties = botProperties;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            Optional<Users> userOpt = userRepository.findByTelegramToken(text);
            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                user.setTelegramChatId(chatId);
                userRepository.save(user);

                sendMessage(chatId, "Токен принят! Теперь бот связан с вашим аккаунтом.");
            } else {
                sendMessage(chatId, "Токен не найден. Проверьте правильность введённых данных.");
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUserName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}
