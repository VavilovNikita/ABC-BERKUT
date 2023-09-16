package com.vizdizbot.service;

import com.vizdizbot.entyty.Filters;
import com.vizdizbot.repository.FiltersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FiltersService {
    FiltersRepository filtersRepository;

    @Autowired
    public FiltersService(FiltersRepository filtersRepository) {
        this.filtersRepository = filtersRepository;
    }

    public List<Filters> findAll() {
        return filtersRepository.findAll();
    }

    public void save(Filters filters) {
        filtersRepository.save(filters);
    }

    public Optional<Filters> getByText(String text) {
        return filtersRepository.findByText(text);
    }

    public void delete(Filters filters) {
        filtersRepository.delete(filters);
    }
}
