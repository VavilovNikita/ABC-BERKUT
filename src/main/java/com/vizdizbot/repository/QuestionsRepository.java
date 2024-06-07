package com.vizdizbot.repository;

import com.vizdizbot.entity.Filters;
import com.vizdizbot.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Integer> {
    Questions findByMessageId(Integer id);

    @Query("SELECT q FROM Questions q WHERE DATE(q.time) = :today")
    List<Questions> findAllByCreationDate(@Param("today") LocalDate today);
}
