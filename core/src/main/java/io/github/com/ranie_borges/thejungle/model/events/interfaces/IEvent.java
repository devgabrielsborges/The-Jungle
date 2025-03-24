package io.github.com.ranie_borges.thejungle.model.events.interfaces;

import io.github.com.ranie_borges.thejungle.model.entity.Character;

public interface IEvent {
    <T extends Character> void execute(T character);
}
