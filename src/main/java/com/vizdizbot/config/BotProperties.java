package com.vizdizbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    Long homeChatId;

    String userName;

    String token;
}
