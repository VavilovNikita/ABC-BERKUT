package com.vizdizbot.service;

import com.vizdizbot.entyty.Bot;
import com.vizdizbot.entyty.Filters;
import com.vizdizbot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotService {
    private final BotRepository botRepository;

    @Autowired
    public BotService(BotRepository botRepository) {
        this.botRepository = botRepository;
    }

    public Bot get() {
        return botRepository.findById(1).get();
    }

    public void save(Bot bot) {
        botRepository.save(bot);
    }
}
