package com.vizdizbot.service;

import com.vizdizbot.entyty.Filters;
import com.vizdizbot.message.CheckMessage;
import com.vizdizbot.message.MessageStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class VizDizBot extends TelegramLongPollingBot {
    private final BotService botService;
    private final FiltersService filtersService;
    private MessageStatus messageStatus = MessageStatus.DEFAULT;

    private final CheckMessage checkMessage;

    private final String helpMessage = "/add Добавить текст для поиска\n" + "/delete Удалить текст для поиска\n" + "/show Отобразить все критерии поиска\n" + "/help повторить это сообщение\n";

    @Autowired
    public VizDizBot(BotService botService, FiltersService filtersService, CheckMessage checkMessage) {
        this.botService = botService;
        this.filtersService = filtersService;
        this.checkMessage = checkMessage;
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/add","Добавить текст для поиска"));
        botCommands.add(new BotCommand("/delete","Удалить текст для поиска"));
        botCommands.add(new BotCommand("/show","Отобразить все критерии поиска"));
        botCommands.add(new BotCommand("/help","Отобразить информацию о командах"));

        try {
            this.execute(new SetMyCommands(botCommands,new BotCommandScopeDefault(),null));
        }catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            if (!messageStatus.equals(MessageStatus.DEFAULT) && checkMessage.isHomeChatAndNotBot(message)) {
                switch (messageStatus) {
                    case WAIT_ADD -> {
                        if (filtersService.getByText(message.getText()).isEmpty()) {
                            filtersService.save(new Filters(message.getText().toLowerCase()));
                            sendMessage(botService.get().getHomeChatId(), "Текст \"" + message.getText() + "\" успешно добавлен");
                        } else {
                            sendMessage(botService.get().getHomeChatId(), "Текст \"" + message.getText() + "\" уже существует");
                        }
                        messageStatus = MessageStatus.DEFAULT;

                    }
                    case WAIT_DELETE -> {
                        Optional<Filters> filters = filtersService.getByText(message.getText().toLowerCase());
                        if (filters.isPresent()) {
                            filtersService.delete(filters.get());
                            sendMessage(botService.get().getHomeChatId(), "Текст \"" + message.getText() + "\" успешно удален");
                        } else {
                            sendMessage(botService.get().getHomeChatId(), "Текст \"" + message.getText() + "\" не найден");
                        }
                        messageStatus = MessageStatus.DEFAULT;
                    }
                }

            } else {
                if (checkMessage.isHomeChatAndNotBot(message)) {
                    String[] msg = message.getText().split("@");
                    if(msg[1].equals(botService.get().getName())){
                        switch (msg[0].toLowerCase()) {
                            case "/add" -> {
                                sendMessage(botService.get().getHomeChatId(), "Введите текст для добавления");
                                messageStatus = MessageStatus.WAIT_ADD;
                            }
                            case "/delete" -> {
                                sendMessage(botService.get().getHomeChatId(), "Введите текст для Удаления");
                                messageStatus = MessageStatus.WAIT_DELETE;
                            }
                            case "/show" -> {
                                List<Filters> show = filtersService.findAll();
                                        sendMessage(botService.get().getHomeChatId(), show.isEmpty()?"Список пуст":show.toString());
                            }
                            case "/help" ->
                                    sendMessage(botService.get().getHomeChatId(), helpMessage);
                            default -> sendMessage(botService.get().getHomeChatId(), "Неизвестная команда попробуйте /help");
                        }
                    }
                } else {
                    String messageText = message.getText().toLowerCase();
                    if (CheckMessage.wordsContains(filtersService.findAll(), messageText.toLowerCase())) {
                        sendMessage(botService.get().getHomeChatId(), "@" +
                                message.getFrom().getUserName() +
                                " Отправил сообщение в группу " +
                                message.getChat().getTitle() +
                                " с сообщением \"" + message.getText() +
                                "\"");
                    }
                }
            }


        }
    }


    @Override
    public String getBotUsername() {
        return botService.get().getName();
    }

    @Override
    public String getBotToken() {
        return botService.get().getToken();
    }

    private void sendMessage(Long chatId, String messageForSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageForSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
