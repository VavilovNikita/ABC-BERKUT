package com.vizdizbot.repository;

import com.vizdizbot.entyty.Filters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FiltersRepository extends JpaRepository<Filters, Integer> {
    Optional<Filters> findByText(String text);
}
