package com.vizdizbot.entyty;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Filters")
public class Filters {
    public Filters(String text) {
        this.text = text;
    }

    public Filters() {
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "text", columnDefinition="text")
    private String text;

    @Override
    public String toString() {
        return text + "\n";
    }
}
