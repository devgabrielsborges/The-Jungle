package io.github.com.ranie_borges.thejungle.game.events;

import io.github.com.ranie_borges.thejungle.model.Event;

public class Criature extends Event {
    protected Criature(String name, String description, float probability) {
        super(name, description, probability);
    }
}
