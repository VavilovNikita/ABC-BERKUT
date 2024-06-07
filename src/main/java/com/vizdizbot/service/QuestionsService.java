package com.vizdizbot.service;

import com.vizdizbot.entity.Filters;
import com.vizdizbot.entity.Questions;
import com.vizdizbot.repository.QuestionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionsService {

    private final QuestionsRepository questionsRepository;

    @Autowired
    public QuestionsService(QuestionsRepository questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    public Questions findByMessageId(Integer messageId) {
        Questions question = questionsRepository.findByMessageId(messageId);
        return question != null ? question : new Questions();
    }

    public void save(Questions questions) {
        questionsRepository.save(questions);
    }

    public List<Questions> findAllByToday() {
        return questionsRepository.findAllByCreationDate(LocalDate.now());
    }
}

