package io.github.com.ranie_borges.thejungle.model.stats;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.events.Event;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Character playerCharacter;
    private Ambient currentAmbient;
    private List<Event> activeEvents;
    private int daysSurvived;
    private OffsetDateTime offsetDateTime;

    public GameState() {
        this.activeEvents = new ArrayList<>();
        this.offsetDateTime = OffsetDateTime.now();
    }

    public Character getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(Character playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public Ambient getCurrentAmbient() {
        return currentAmbient;
    }

    public void setCurrentAmbient(Ambient currentAmbient) {
        this.currentAmbient = currentAmbient;
    }

    public List<Event> getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(List<Event> activeEvents) {
        this.activeEvents = activeEvents;
    }

    public int getDaysSurvived() {
        return daysSurvived;
    }

    public void setDaysSurvived(int daysSurvived) {
        this.daysSurvived = daysSurvived;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

}
