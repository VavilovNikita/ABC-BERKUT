package com.vizdizbot.message;

import com.vizdizbot.entyty.Filters;
import com.vizdizbot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
@Component
public class CheckMessage {
    @Autowired
    private BotService botService;


    public CheckMessage(BotService botService) {
        this.botService = botService;
    }

    public static boolean wordsContains(List<Filters> words, String messageText) {
        for (Filters word : words) {
            if (messageText.contains(word.getText())) {
                return true;
            }
        }
        return false;
    }

    public boolean isHomeChatAndNotBot(Message message) {
    return !message.getFrom().getIsBot() && message.getChat().getId().equals(botService.get().getHomeChatId());
    }
}
