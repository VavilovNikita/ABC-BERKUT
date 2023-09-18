package com.vizdizbot.entyty;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "filters")
public class Filters {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "text", columnDefinition = "text")
    private String text;

    public Filters(String text) {
        this.text = text;
    }

    public Filters() {
    }

    @Override
    public String toString() {
        return text + "\n";
    }
}
