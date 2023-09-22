package com.vizdizbot;

import com.vizdizbot.config.BotProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({BotProperties.class})
@SpringBootApplication
public class VizDizBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(VizDizBotApplication.class, args);
    }

}
