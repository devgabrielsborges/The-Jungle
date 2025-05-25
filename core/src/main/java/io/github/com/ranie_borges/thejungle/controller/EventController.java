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
    private final Map<Event, Float> possibleEvents; // This seems unused if events come from Ambient
    @Expose
    private final List<Event> eventHistory;
    private final transient Random random; // Added transient
    private GameState gameState; // Should not be exposed directly if managed by AmbientController
    private static final int MAX_HISTORY_SIZE = 15;

    public EventController(GameState gameState) {
        this.possibleEvents = new HashMap<>();
        this.eventHistory = new ArrayList<>();
        this.random = new Random();
        this.gameState = gameState;
    }

    // ... (rest of the methods remain the same, ensure 'random' is used where 'new Random()' was)
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

    public void setGameState(GameState gameState) { // Added for flexibility if needed
        this.gameState = gameState;
    }


    public Event drawEvent(Ambient ambient) {
        try {
            if (ambient == null) {
                logger.debug("Cannot draw event: ambient is null");
                return null;
            }
            // Use events defined in the Ambient object itself
            Map<Event, Double> environmentEvents = ambient.getPossibleEvents();
            if (environmentEvents == null || environmentEvents.isEmpty()) {
                logger.debug("No possible events defined for ambient: {}", ambient.getName());
                return null;
            }

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

            double totalProbability = 0;
            for (Event event : candidateEvents) {
                totalProbability += environmentEvents.get(event); // Probability from Ambient's map
            }

            if (totalProbability <= 0) { // Avoid division by zero or infinite loop if all probs are 0
                logger.debug("Total probability for candidate events is zero or less for ambient: {}. Selecting randomly.", ambient.getName());
                return candidateEvents.get(random.nextInt(candidateEvents.size()));
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
            // Fallback if somehow no event is selected by weighted random (should be rare if totalProbability > 0)
            logger.warn("Weighted random selection failed for ambient: {}. Fallback to random unweighted selection.", ambient.getName());
            return candidateEvents.get(random.nextInt(candidateEvents.size()));
        } catch (Exception e) {
            logger.error("Error drawing event: {}", e.getMessage(), e);
            throw new EventControllerException("Error drawing event", e);
        }
    }

    public void applyEvent(Event event, Character character, Ambient ambient) {
        try {
            validateEvent(event);
            validateCharacter(character);

            event.execute(character, ambient); // Event logic needs to be implemented in subclasses

            addToHistory(event);
            if (gameState != null && gameState.getActiveEvents() != null && !gameState.getActiveEvents().contains(event)) {
                gameState.getActiveEvents().add(event);
            } else if (gameState == null || gameState.getActiveEvents() == null) {
                logger.warn("GameState or activeEvents list is null, cannot add event {} to active events.", event.getName());
            }

            logger.info("Event {} applied to character {}", event.getName(), character.getName());
        } catch (Exception e) {
            logger.error("Failed to apply event {}: {}", event != null ? event.getName() : "null", e.getMessage(), e);
        }
    }

    private void validateEvent(Event event) {
        if (event == null) {
            throw new InvalidEventException("Event cannot be null");
        }
    }

    private void validateCharacter(Character character) {
        if (character == null) {
            throw new IllegalArgumentException("Character cannot be null");
        }
    }

    public boolean removeEvent(Event event) {
        try {
            if (event == null) {
                logger.warn("Cannot remove null event.");
                return false;
            }
            if (gameState == null || gameState.getActiveEvents() == null) {
                logger.warn("GameState or activeEvents list is null, cannot remove event {}.", event.getName());
                return false;
            }

            boolean removed = gameState.getActiveEvents().remove(event);
            if (removed) {
                logger.info("Event {} has ended and was removed from active events.", event.getName());
            } else {
                logger.debug("Event {} not found in active events for removal.", event.getName());
            }
            return removed;
        } catch (Exception e) {
            logger.error("Failed to remove event {}: {}",event != null ? event.getName() : "null", e.getMessage(), e);
            return false;
        }
    }

    private boolean recentlyOccurred(Event event) {
        try {
            if (event == null || eventHistory.isEmpty()) {
                return false;
            }
            // Check last few events (e.g., half of max history or up to 5)
            int checkDepth = Math.min(MAX_HISTORY_SIZE / 2, Math.min(5, eventHistory.size()));
            for (int i = 0; i < checkDepth; i++) {
                if (eventHistory.get(eventHistory.size() - 1 - i).getName().equals(event.getName())) {
                    logger.trace("Event {} occurred recently.", event.getName());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking recent events: {}", e.getMessage(), e);
            return false;
        }
    }

    private void addToHistory(Event event) {
        try {
            if (event == null) return;
            eventHistory.add(event);
            if (eventHistory.size() > MAX_HISTORY_SIZE) {
                eventHistory.remove(0);
            }
            logger.trace("Added event {} to history. History size: {}", event.getName(), eventHistory.size());
        } catch (Exception e) {
            logger.error("Failed to add event to history: {}", e.getMessage(), e);
        }
    }

    public void generateRandomEvent(Ambient ambient) {
        try {
            if (ambient == null) {
                logger.warn("Cannot generate random event: ambient is null");
                return; // Don't throw, just don't generate an event
            }
            if (gameState == null || gameState.getPlayerCharacter() == null) {
                logger.warn("Cannot generate random event: GameState or PlayerCharacter is null.");
                return;
            }


            Event selectedEvent = drawEvent(ambient);

            if (selectedEvent != null) {
                logger.info("Generated random event: {} for ambient: {}",
                    selectedEvent.getName(), ambient.getName());
                applyEvent(selectedEvent, gameState.getPlayerCharacter(), ambient);
            } else {
                logger.debug("No event generated for ambient: {}", ambient.getName());
            }

        } catch (Exception e) { // Catch broader exceptions from drawEvent or applyEvent
            logger.error("Error during random event generation process for ambient {}: {}", ambient != null ? ambient.getName() : "null", e.getMessage(), e);
        }
    }
}
