package com.vizdizbot.service;

import com.vizdizbot.config.BotProperties;
import com.vizdizbot.entity.MessageStatus;
import com.vizdizbot.model.Command;
import com.vizdizbot.entity.Filters;
import jakarta.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.vizdizbot.utils.MessageUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VizDizBot extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(VizDizBot.class);
    private final BotProperties botProperties;
    private final FiltersService filtersService;
    private MessageStatus messageStatus = MessageStatus.DEFAULT;


    @Autowired
    public VizDizBot(BotProperties botProperties, FiltersService filtersService) {
        this.botProperties = botProperties;
        this.filtersService = filtersService;
    }


    @PostConstruct
    public void createMenu() {
        final List<BotCommand> botCommands = Arrays.stream(MessageUtil.Menu.values())
                .map(menu -> new BotCommand(menu.menuItem, menu.menuDescription))
                .collect(Collectors.toList());
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            logger.error("Execute bot failed", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            receiveMessage(update.getMessage());
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

    private void receiveMessage(Message message) {
        if (!messageStatus.equals(MessageStatus.DEFAULT) && isHomeChatAndNotBot(message)) {
            switch (messageStatus) {
                case WAIT_ADD -> {
                    if (filtersService.getByText(message.getText()).isEmpty()) {
                        filtersService.save(new Filters(message.getText().toLowerCase()));
                        sendMessage(String.format(MessageUtil.ADD_SUCCESS, message.getText()));
                    } else {
                        sendMessage(String.format(MessageUtil.ADD_FAILED, message.getText()));
                    }
                    messageStatus = MessageStatus.DEFAULT;

                }
                case WAIT_DELETE -> {
                    Optional<Filters> filters = filtersService.getByText(message.getText().toLowerCase());
                    if (filters.isPresent()) {
                        filtersService.delete(filters.get());
                        sendMessage(String.format(MessageUtil.DELETE_SUCCESS, message.getText()));
                    } else {
                        sendMessage(String.format(MessageUtil.DELETE_FAILED, message.getText()));
                    }
                    messageStatus = MessageStatus.DEFAULT;
                }
            }

        } else {
            if (isHomeChatAndNotBot(message)) {
                Command command = new Command(botProperties.getUserName(), message.getText());
                if (command.getBotName().equals(botProperties.getUserName())) {
                    executeCommand(command.getCommand());
                }
            } else {
                String messageText = message.getText().toLowerCase();
                if (MessageUtil.wordsContains(filtersService.findAll(), messageText.toLowerCase())) {
                    sendMessage(String.format(MessageUtil.COINCIDENCE, message.getFrom().getUserName(), message.getChat().getTitle(), message.getText()));
                }
            }
        }
    }


    public void executeCommand(String command) {

        switch (command) {
            case MessageUtil.ADD -> {
                sendMessage(MessageUtil.Menu.ADD.getMenuMessage());
                messageStatus = MessageStatus.WAIT_ADD;
            }
            case MessageUtil.DELETE -> {
                sendMessage(MessageUtil.Menu.DELETE.getMenuMessage());
                messageStatus = MessageStatus.WAIT_DELETE;
            }
            case MessageUtil.SHOW -> {
                List<Filters> show = filtersService.findAll();
                sendMessage(show.isEmpty() ? MessageUtil.Menu.SHOW.getMenuMessage() : show.toString());
            }
            case MessageUtil.HELP -> sendMessage(MessageUtil.Menu.HELP.getMenuMessage());
            default -> sendMessage(MessageUtil.UNKNOWN_COMMAND);
        }
    }


    private void sendMessage(String messageForSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(botProperties.getHomeChatId()));
        message.setText(messageForSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Execute send message failed", e);
        }

    }

    private boolean isHomeChatAndNotBot(Message message) {
        return !message.getFrom().getIsBot() && message.getChat().getId().equals(botProperties.getHomeChatId());
    }
}
