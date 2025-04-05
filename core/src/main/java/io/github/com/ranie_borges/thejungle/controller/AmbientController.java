package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AmbientController<T extends Ambient> {
    private static final Logger logger = LoggerFactory.getLogger(AmbientController.class);
    private Set<T> ambients;
    private Clime globalClime;
    private List<T> visitedAmbients;
    private final EventController eventController;
    private GameState gameState;

    public AmbientController(GameState gameState, EventController eventController) {
        this.ambients = new HashSet<>();
        this.visitedAmbients = new ArrayList<>();
        this.eventController = eventController;
        this.gameState = gameState;
    }

    public Set<T> getAmbients() {
        return ambients;
    }

    public void setAmbients(Set<T> ambients) {
        this.ambients = ambients;
    }

    public Clime getGlobalClime() {
        return globalClime;
    }

    public void setGlobalClime(Clime globalClime) {
        this.globalClime = globalClime;

        // Apply global climate to all environments that don't override it
        for (T ambient : ambients) {
            if (ambient.getClimes().isEmpty()) {
                ambient.addClime(globalClime);
            }
        }
    }

    public List<T> getVisitedAmbients() {
        return visitedAmbients;
    }

    public void setVisitedAmbients(List<T> visitedAmbients) {
        this.visitedAmbients = visitedAmbients;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Moves the player character to a new environment
     * @param character The player character
     * @param newAmbient The target environment
     * @return True if the move was successful, false otherwise
     */
    public boolean changeAmbient(Character character, T newAmbient) {
        if (character == null || newAmbient == null || !ambients.contains(newAmbient)) {
            logger.warn("Invalid character or environment in changeAmbient");
            return false;
        }

        gameState.setCurrentAmbient(newAmbient);

        if (!visitedAmbients.contains(newAmbient)) {
            visitedAmbients.add(newAmbient);
        }

        generateEvent(newAmbient);

        logger.info("Character {} moved to environment {}", character.getName(), newAmbient.getName());
        return true;
    }

    /**
     * Generates a random event based on the current environment
     *
     * @param ambient The current environment
     */
    public void generateEvent(T ambient) {
        if (ambient == null) {
            return;
        }

        Event event = eventController.drawEvent(ambient);
        if (event != null) {
            eventController.applyEvent(event, gameState.getPlayerCharacter());
            logger.info("Event generated in {}: {}", ambient.getName(), event.getName());
        }

    }

    /**
     * Updates available resources as they're collected
     * @param ambient The environment to modify
     * @param item The item being collected
     * @return True if resource was successfully modified
     */
    public boolean modifyResources(T ambient, Item item) {
        if (ambient == null || item == null) {
            return false;
        }

        Set<Item> currentResources = new HashSet<>(ambient.getResources());
        boolean removed = currentResources.remove(item);

        if (removed) {
            ambient.setResources(currentResources);
            logger.info("Resource {} collected from {}", item.getName(), ambient.getName());
            return true;
        }

        return false;
    }

    public void regenerateResources(T currentAmbient, int resourceCount, boolean isDaytime) {
        throw new UnsupportedOperationException("Resource regeneration not yet implemented");
    }
}
