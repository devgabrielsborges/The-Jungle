package io.github.com.ranie_borges.thejungle.controller;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.controller.exceptions.event.EventControllerException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.event.InvalidEventException;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final Map<Event, Float> possibleEvents;
    @Expose
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

    public Map<Event, Float>getPossibleEvents() {
        return possibleEvents;
    }

    public void addPossibleEvent(Event event, float probability) {
        try {
            if (event == null) {
                logger.error("Cannot add event: event is null");
                throw new InvalidEventException("Event cannot be null");
            }
            possibleEvents.put(event, probability);
            logger.info("Added event: {}", event.getName());
        } catch (Exception e) {
            logger.error("Failed to add event: {}", e.getMessage());
            throw new EventControllerException("Failed to add event", e);
        }
    }

    public List<Event> getEventHistory() {
        return Collections.unmodifiableList(eventHistory);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        try {
            if (gameState == null) {
                logger.error("Cannot set game state: game state is null");
                throw new IllegalArgumentException("Game state cannot be null");
            }
            this.gameState = gameState;
            logger.debug("Game state set");
        } catch (Exception e) {
            logger.error("Failed to set game state: {}", e.getMessage());
            throw new EventControllerException("Failed to set game state", e);
        }
    }

    /**
     * Randomly selects an event compatible with the current environment
     * @param ambient The current environment
     * @return The selected event or null if no event occurs
     */
    public Event drawEvent(Ambient ambient) {
        try {
            if (ambient == null) {
                logger.debug("Cannot draw event: ambient is null");
                return null;
            }
            if (ambient.getPossibleEvents().isEmpty()) {
                logger.debug("No possible events for ambient: {}", ambient.getName());
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
                logger.debug("No candidate events available for ambient: {}", ambient.getName());
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
                    logger.debug("Selected event: {} for ambient: {}", event.getName(), ambient.getName());
                    return event;
                }
            }

            // Fallback to random selection if weighted selection fails
            Event selectedEvent = candidateEvents.get(random.nextInt(candidateEvents.size()));
            logger.debug("Fallback selection - event: {} for ambient: {}", selectedEvent.getName(), ambient.getName());
            return selectedEvent;
        } catch (Exception e) {
            logger.error("Error drawing event: {}", e.getMessage());
            throw new EventControllerException("Error drawing event", e);
        }
    }

    /**
     * Applies an event to a character
     *
     * @param event     The event to apply
     * @param character The character affected by the event
     */
    public void applyEvent(Event event, Character character, Ambient ambient) {
        try {
            validateEvent(event);
            validateCharacter(character);

            event.execute(character, ambient);

            addToHistory(event);
            if (!gameState.getActiveEvents().contains(event)) {
                gameState.getActiveEvents().add(event);
            }

            logger.info("Event {} applied to character {}", event.getName(), character.getName());
        } catch (Exception e) {
            logger.error("Failed to apply event: {}", e.getMessage());
            throw new EventControllerException("Failed to apply event", e);
        }
    }

    private void validateEvent(Event event) {
        if (event == null) {
            logger.error("Cannot apply event: event is null");
            throw new InvalidEventException("Event cannot be null");
        }
    }

    private void validateCharacter(Character character) {
        if (character == null) {
            logger.error("Cannot apply event: character is null");
            throw new IllegalArgumentException("Character cannot be null");
        }
    }

    /**
     * Removes an event after it has concluded
     * @param event The event to remove
     * @return True if event was successfully removed
     */
    public boolean removeEvent(Event event) {
        try {
            if (event == null) {
                logger.error("Cannot remove event: event is null");
                return false;
            }

            boolean removed = gameState.getActiveEvents().remove(event);

            if (removed) {
                logger.info("Event {} has ended", event.getName());
            } else {
                logger.debug("Event {} not found in active events", event.getName());
            }

            return removed;
        } catch (Exception e) {
            logger.error("Failed to remove event: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an event occurred recently to prevent repetition
     * @param event The event to check
     * @return True if the event occurred recently
     */
    private boolean recentlyOccurred(Event event) {
        try {
            if (event == null) {
                return false;
            }

            int recentThreshold = Math.min(MAX_HISTORY_SIZE, eventHistory.size());
            for (int i = 0; i < recentThreshold; i++) {
                if (eventHistory.get(eventHistory.size() - 1 - i).getName().equals(event.getName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking recent events: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Adds an event to history and maintains history size
     * @param event The event to add
     */
    private void addToHistory(Event event) {
        try {
            if (event == null) {
                logger.error("Cannot add to history: event is null");
                return;
            }

            eventHistory.add(event);

            if (eventHistory.size() > MAX_HISTORY_SIZE) {
                eventHistory.remove(0);
            }

            logger.debug("Added event {} to history", event.getName());
        } catch (Exception e) {
            logger.error("Failed to add event to history: {}", e.getMessage());
        }
    }

    /**
     * Selects a random event compatible with the given ambient
     *
     * @param ambient The ambient to draw an event from
     * @return The selected event or null if no event occurs
     */
    public Event generateRandomEvent(Ambient ambient) {
        try {
            if (ambient == null) {
                logger.error("Cannot generate random event: ambient is null");
                throw new InvalidEventException("Ambient cannot be null");
            }

            Event selectedEvent = drawEvent(ambient);

            if (selectedEvent != null) {
                logger.info("Generated random event: {} for ambient: {}",
                          selectedEvent.getName(), ambient.getName());
            } else {
                logger.debug("No event generated for ambient: {}", ambient.getName());
            }

            return selectedEvent;
        } catch (Exception e) {
            logger.error("Error generating random event: {}", e.getMessage());
            throw new EventControllerException("Error generating random event", e);
        }
    }
}
