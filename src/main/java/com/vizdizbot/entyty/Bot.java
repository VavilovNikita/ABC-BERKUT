package com.vizdizbot.entyty;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bot")
public class Bot {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name", columnDefinition = "text")
    private String name;

    @Column(name = "token", columnDefinition = "text")
    private String token;

    @Column(name = "home_chat_id")
    private Long homeChatId;
}
