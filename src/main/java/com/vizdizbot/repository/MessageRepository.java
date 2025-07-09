package com.vizdizbot.repository;

import com.vizdizbot.entity.Message;
import com.vizdizbot.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserOrderByCreatedAtDesc(Users user);
}

