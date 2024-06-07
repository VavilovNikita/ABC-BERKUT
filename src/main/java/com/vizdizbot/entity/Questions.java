package com.vizdizbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "questions")
public class Questions {

    @Id
    @GeneratedValue
    private Long id;

    @Unique
    @Column(name = "messageId")
    private Integer messageId;

    @Column(name = "text", columnDefinition = "text")
    private String text;

    @Column(name = "answer", columnDefinition = "text")
    private String answer;

    @Column(name = "author", columnDefinition = "text")
    private String author;

    @Column(name = "time")
    private Date time;

    public Questions(Message message) {
        this.messageId = message.getMessageId();
        this.text = message.getText();
        this.author = message.getFrom().getUserName();
        this.time = new Date((long) message.getDate() * 1000);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Questions() {
    }

    @Override
    public String toString() {
        return "Вопрос=" + text +
            " Ответ=" + answer +
            " АвторВопроса=" + author;
    }
}
