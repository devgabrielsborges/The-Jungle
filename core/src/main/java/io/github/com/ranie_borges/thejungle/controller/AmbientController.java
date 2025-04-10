package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AmbientController<A extends Ambient, C extends Character<?>> {
    private static final Logger logger = LoggerFactory.getLogger(AmbientController.class);
    private final EventController<C> eventController;
    private Clime globalClime;
    private Set<A> ambients;
    private List<A> visitedAmbients;
    private GameState<C, A> gameState;

    public AmbientController(GameState<C, A> gameState, EventController<C> eventController) {
        this.ambients = new HashSet<>();
        this.visitedAmbients = new ArrayList<>();
        this.eventController = eventController;
        this.gameState = gameState;
    }

    public Set<A> getAmbients() {
        return ambients;
    }

    public void setAmbients(Set<A> ambients) {
        this.ambients = ambients;
    }

    public Clime getGlobalClime() {
        return globalClime;
    }

    public void setGlobalClime(Clime globalClime) {
        this.globalClime = globalClime;

        for (A ambient : ambients) {
            if (ambient.getClimes().isEmpty()) {
                ambient.addClime(globalClime);
            }
        }
    }

    public List<A> getVisitedAmbients() {
        return visitedAmbients;
    }

    public void setVisitedAmbients(List<A> visitedAmbients) {
        this.visitedAmbients = visitedAmbients;
    }

    public GameState<C, A> getGameState() {
        return gameState;
    }

    public void setGameState(GameState<C, A> gameState) {
        this.gameState = gameState;
    }

    public boolean changeAmbient(C character, A newAmbient) {
        if (character == null || newAmbient == null || !ambients.contains(newAmbient)) {
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

    public void generateEvent(A ambient) {
        if (ambient == null) {
            return;
        }

        Event event = eventController.drawEvent(ambient);
        if (event != null) {
            eventController.applyEvent(event, gameState.getPlayerCharacter());
        }
    }

    public boolean modifyResources(A ambient, Item item) {
        if (ambient == null || item == null) {
            return false;
        }

        Set<Item> currentResources = new HashSet<>(ambient.getResources());
        boolean removed = currentResources.remove(item);

        if (removed) {
            ambient.setResources(currentResources);
            return true;
        }

        return false;
    }

    public void regenerateResources(A currentAmbient, int resourceCount, boolean isDaytime) {
        throw new UnsupportedOperationException("Resource regeneration not yet implemented");
    }

    public boolean removeResource(A ambient, Item resource) {
        if (ambient == null || resource == null) {
            return false;
        }

        return ambient.getResources().remove(resource);
    }
}
