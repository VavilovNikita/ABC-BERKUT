package com.vizdizbot.service;

import com.vizdizbot.config.BotProperties;
import com.vizdizbot.entity.Filters;
import com.vizdizbot.entity.MessageStatus;
import com.vizdizbot.entity.Questions;
import com.vizdizbot.model.Command;
import com.vizdizbot.utils.MessageUtil;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VizDizBot extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(VizDizBot.class);
    private final BotProperties botProperties;
    private final FiltersService filtersService;
    private final QuestionsService questionsService;
    private MessageStatus messageStatus = MessageStatus.DEFAULT;


    @Autowired
    public VizDizBot(BotProperties botProperties, FiltersService filtersService, QuestionsService questionsService) {
        this.botProperties = botProperties;
        this.filtersService = filtersService;
        this.questionsService = questionsService;
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
                    executeCommand(command.getCommand(),message);
                }
            } else {
                Message replyToMessage = message.getReplyToMessage();
                if (replyToMessage != null) {
                    Questions questions = questionsService.findByMessageId(replyToMessage.getMessageId());
                    if (questions.getId() != null) {
                        questions.setAnswer(questions.getAnswer() == null ? message.getText() : questions.getAnswer() + " | " + message.getText());
                        questionsService.save(questions);
                    }
                }
            }
            String messageText = message.getText().toLowerCase();
            if (message.getReplyToMessage() == null && MessageUtil.wordsContains(filtersService.findAll(), messageText.toLowerCase())) {
                sendMessage(String.format(MessageUtil.COINCIDENCE, message.getFrom().getUserName(), message.getChat().getTitle(), message.getText()));
                questionsService.save(new Questions(message));
            }
        }
    }


    public void executeCommand(String command, Message message) {

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
            case MessageUtil.SHOWTODAYQUESTIONS -> {
                List<Questions> show = questionsService.findAllByToday();
                ByteArrayInputStream excelFile = questionsToExcel(show);
                InputStream inputStream = excelFile;

                SendDocument sendDocumentRequest = new SendDocument();
                sendDocumentRequest.setChatId(message.getChatId().toString());
                sendDocumentRequest.setDocument(new InputFile(inputStream, "questions" + LocalDateTime.now() +".xlsx"));

                try {
                    execute(sendDocumentRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public static ByteArrayInputStream questionsToExcel(List<Questions> questions) {
        String[] COLUMNs = {"ID", "Message ID", "Text", "Answer", "Author", "Time"};
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Questions");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < COLUMNs.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(COLUMNs[i]);
            }

            int rowIdx = 1;
            for (Questions question : questions) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(question.getId());
                row.createCell(1).setCellValue(question.getMessageId());
                row.createCell(2).setCellValue(question.getText());
                row.createCell(3).setCellValue(question.getAnswer());
                row.createCell(4).setCellValue(question.getAuthor());
                row.createCell(5).setCellValue(question.getTime().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Excel file: " + e.getMessage());
        }
    }
}
