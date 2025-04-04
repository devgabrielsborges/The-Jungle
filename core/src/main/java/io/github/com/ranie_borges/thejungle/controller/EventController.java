package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EventController<C extends Character<?>> {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final Map<String, Event> possibleEvents;
    private final List<Event> eventHistory;
    private final Random random;
    private GameState gameState;

    private static final int MAX_HISTORY_SIZE = 15;

    public EventController(GameState gameState) {
        this.possibleEvents = new HashMap<>();
        this.eventHistory = new ArrayList<>();
        this.random = new Random();
        this.gameState = gameState;
    }

    public Map<String, Event> getPossibleEvents() {
        return Collections.unmodifiableMap(possibleEvents);
    }

    public void addPossibleEvent(Event event) {
        if (event != null) {
            possibleEvents.put(event.getName(), event);
        }
    }

    public List<Event> getEventHistory() {
        return Collections.unmodifiableList(eventHistory);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Randomly selects an event compatible with the current environment
     * @param ambient The current environment
     * @return The selected event or null if no event occurs
     */
    public Event drawEvent(Ambient ambient) {
        if (ambient == null || ambient.getPossibleEvents().isEmpty()) {
            return null;
        }

        Map<Event, Double> environmentEvents = ambient.getPossibleEvents();

        // Filter out recently occurred events to prevent repetition
        List<Event> candidateEvents = new ArrayList<>();
        for (Event event : environmentEvents.keySet()) {
            if (event.isPossible() && !recentlyOccurred(event)) {
                candidateEvents.add(event);
            }
        }

        if (candidateEvents.isEmpty()) {
            return null;
        }

        // Weighted random selection based on probabilities
        double totalProbability = 0;
        for (Event event : candidateEvents) {
            totalProbability += environmentEvents.get(event);
        }

        double randomValue = random.nextDouble() * totalProbability;
        double cumulativeProbability = 0;

        for (Event event : candidateEvents) {
            cumulativeProbability += environmentEvents.get(event);
            if (randomValue <= cumulativeProbability) {
                return event;
            }
        }

        // Fallback to random selection if weighted selection fails
        return candidateEvents.get(random.nextInt(candidateEvents.size()));
    }

    /**
     * Applies an event to a character
     *
     * @param event     The event to apply
     * @param character The character affected by the event
     */
    public void applyEvent(Event event, Character<?> character) {
        if (event == null || character == null) {
            return;
        }

        event.execute(character);

        addToHistory(event);
        if (!gameState.getActiveEvents().contains(event)) {
            gameState.getActiveEvents().add(event);
        }

        logger.info("Event {} applied to character {}", event.getName(), character.getName());
    }

    /**
     * Removes an event after it has concluded
     * @param event The event to remove
     * @return True if event was successfully removed
     */
    public boolean removeEvent(Event event) {
        if (event == null) {
            return false;
        }

        boolean removed = gameState.getActiveEvents().remove(event);

        if (removed) {
            logger.info("Event {} has ended", event.getName());
        }

        return removed;
    }

    /**
     * Checks if an event occurred recently to prevent repetition
     * @param event The event to check
     * @return True if the event occurred recently
     */
    private boolean recentlyOccurred(Event event) {
        int recentThreshold = Math.min(MAX_HISTORY_SIZE, eventHistory.size());
        for (int i = 0; i < recentThreshold; i++) {
            if (eventHistory.get(eventHistory.size() - 1 - i).getName().equals(event.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an event to history and maintains history size
     * @param event The event to add
     */
    private void addToHistory(Event event) {
        eventHistory.add(event);

        if (eventHistory.size() > MAX_HISTORY_SIZE) {
            eventHistory.remove(0);
        }
    }
}
