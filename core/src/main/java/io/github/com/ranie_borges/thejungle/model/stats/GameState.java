package io.github.com.ranie_borges.thejungle.model.stats;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.controller.AmbientController;
import io.github.com.ranie_borges.thejungle.controller.EventController;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    @Expose
    private Character playerCharacter;

    @Expose
    private Ambient currentAmbient;

    @Expose
    private List<Event> activeEvents;

    @Expose
    private int daysSurvived;

    @Expose
    private OffsetDateTime offsetDateTime;

    private EventController eventController;
    private AmbientController ambientController;

    public GameState() {
        this.activeEvents = new ArrayList<>();
        this.offsetDateTime = OffsetDateTime.now();
        this.eventController = new EventController(this);
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

    public EventController getEventController() {
        return eventController;
    }

    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }

    public AmbientController getAmbientController() {
        return ambientController;
    }

    public void setAmbientController(AmbientController ambientController) {
        this.ambientController = ambientController;
    }
}
