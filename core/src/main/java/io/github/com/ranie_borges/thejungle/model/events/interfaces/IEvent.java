package io.github.com.ranie_borges.thejungle.model.events.interfaces;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

public interface IEvent {
    void execute(Character character, Ambient ambient);   // applies some event effect on the Character
}
