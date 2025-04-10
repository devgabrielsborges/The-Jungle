package io.github.com.ranie_borges.thejungle.model.stats;

import io.github.com.ranie_borges.thejungle.controller.AmbientController;
import io.github.com.ranie_borges.thejungle.controller.EventController;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameState<C extends Character<?>, A extends Ambient> {
    private C playerCharacter;
    private A currentAmbient;
    private List<Event> activeEvents;
    private int daysSurvived;
    private OffsetDateTime offsetDateTime;
    private EventController<C> eventController;
    private AmbientController<A, C> ambientController;

    public GameState() {
        this.activeEvents = new ArrayList<>();
        this.offsetDateTime = OffsetDateTime.now();
        this.eventController = new EventController<>(this);
    }

    public C getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(C playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public A getCurrentAmbient() {
        return currentAmbient;
    }

    public void setCurrentAmbient(A currentAmbient) {
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

    public EventController<C> getEventController() {
        return eventController;
    }

    public void setEventController(EventController<C> eventController) {
        this.eventController = eventController;
    }

    public AmbientController<A, C> getAmbientController() {
        return ambientController;
    }

    public void setAmbientController(AmbientController<A, C> ambientController) {
        this.ambientController = ambientController;
    }

    public void copyFrom(GameState<C, A> other) {
        this.playerCharacter = other.getPlayerCharacter();
        this.currentAmbient = other.getCurrentAmbient();
        this.daysSurvived = other.getDaysSurvived();
        this.offsetDateTime = other.getOffsetDateTime();
        this.activeEvents = new ArrayList<>(other.getActiveEvents());
    }
}
