package io.github.com.ranie_borges.thejungle.model.interfaces;

import io.github.com.ranie_borges.thejungle.model.Character;

public interface IEvent {
    <T extends Character> void execute(T character);
}
