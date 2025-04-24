package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.AmbientControllerException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.InvalidAmbientException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.ResourceOperationException;
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

public class AmbientController<A extends Ambient, C extends Character> {
    private static final Logger logger = LoggerFactory.getLogger(AmbientController.class);
    private final EventController eventController;
    private Clime globalClime;
    private Set<A> ambients;
    private List<A> visitedAmbients;
    private GameState gameState;

    public AmbientController(GameState gameState, EventController eventController) {
        this.ambients = new HashSet<>();
        this.visitedAmbients = new ArrayList<>();
        this.eventController = eventController;
        this.gameState = gameState;
    }

    public Set<A> getAmbients() {
        return ambients;
    }

    public void setAmbients(Set<A> ambients) {
        try {
            if (ambients == null) {
                logger.error("Cannot set ambients: ambients is null");
                throw new IllegalArgumentException("Ambients cannot be null");
            }
            this.ambients = ambients;
            logger.debug("Set {} ambients", ambients.size());
        } catch (Exception e) {
            logger.error("Failed to set ambients: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set ambients", e);
        }
    }

    public Clime getGlobalClime() {
        return globalClime;
    }

    public void setGlobalClime(Clime globalClime) {
        try {
            if (globalClime == null) {
                logger.error("Cannot set global clime: clime is null");
                throw new IllegalArgumentException("Global clime cannot be null");
            }

            this.globalClime = globalClime;

            for (A ambient : ambients) {
                if (ambient.getClimes().isEmpty()) {
                    ambient.addClime(globalClime);
                }
            }
            logger.info("Set global clime to {}", globalClime);
        } catch (Exception e) {
            logger.error("Failed to set global clime: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set global clime", e);
        }
    }

    public List<A> getVisitedAmbients() {
        return visitedAmbients;
    }

    public void setVisitedAmbients(List<A> visitedAmbients) {
        try {
            if (visitedAmbients == null) {
                logger.error("Cannot set visited ambients: visited ambients is null");
                throw new IllegalArgumentException("Visited ambients cannot be null");
            }
            this.visitedAmbients = visitedAmbients;
            logger.debug("Set {} visited ambients", visitedAmbients.size());
        } catch (Exception e) {
            logger.error("Failed to set visited ambients: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set visited ambients", e);
        }
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
            throw new AmbientControllerException("Failed to set game state", e);
        }
    }

    public boolean changeAmbient(C character, A newAmbient) {
        try {
            if (character == null) {
                logger.error("Cannot change ambient: character is null");
                throw new IllegalArgumentException("Character cannot be null");
            }
            if (newAmbient == null) {
                logger.error("Cannot change ambient: new ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }
            if (!ambients.contains(newAmbient)) {
                logger.error("Cannot change ambient: ambient {} does not exist in game", newAmbient.getName());
                throw new InvalidAmbientException("Ambient does not exist in game: " + newAmbient.getName());
            }

            gameState.setCurrentAmbient(newAmbient);

            if (!visitedAmbients.contains(newAmbient)) {
                visitedAmbients.add(newAmbient);
            }

            generateEvent(newAmbient);

            logger.info("Character {} moved to environment {}", character.getName(), newAmbient.getName());
            return true;
        } catch (Exception e) {
            logger.error("Failed to change ambient: {}", e.getMessage());
            return false;
        }
    }

    public void generateEvent(A ambient) {
        try {
            if (ambient == null) {
                logger.error("Cannot generate event: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }

            Event event = eventController.drawEvent(ambient);
            if (event != null) {
                eventController.applyEvent(event, gameState.getPlayerCharacter());
                logger.info("Applied event {} in ambient {}", event.getName(), ambient.getName());
            } else {
                logger.debug("No event generated for ambient {}", ambient.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to generate event: {}", e.getMessage());
            throw new AmbientControllerException("Error generating event", e);
        }
    }

    public boolean modifyResources(A ambient, Item item) {
        try {
            if (ambient == null) {
                logger.error("Cannot modify resources: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }
            if (item == null) {
                logger.error("Cannot modify resources: item is null");
                throw new IllegalArgumentException("Item cannot be null");
            }

            Set<Item> currentResources = new HashSet<>(ambient.getResources());
            boolean removed = currentResources.remove(item);

            if (removed) {
                ambient.setResources(currentResources);
                logger.info("Resource {} removed from ambient {}", item.getName(), ambient.getName());
                return true;
            }

            logger.debug("Resource {} not found in ambient {}", item.getName(), ambient.getName());
            return false;
        } catch (Exception e) {
            logger.error("Failed to modify resources: {}", e.getMessage());
            throw new ResourceOperationException("Failed to modify resources");
        }
    }

    public void regenerateResources(A currentAmbient, int resourceCount, boolean isDaytime) {
        try {
            if (currentAmbient == null) {
                logger.error("Cannot regenerate resources: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }

            // Implementation will be added later
            logger.warn("Resource regeneration not yet implemented");
            throw new UnsupportedOperationException("Resource regeneration not yet implemented");
        } catch (UnsupportedOperationException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            logger.error("Failed to regenerate resources: {}", e.getMessage());
            throw new ResourceOperationException("Failed to regenerate resources");
        }
    }

    public boolean removeResource(Ambient ambient, Item resource) {
        try {
            if (ambient == null) {
                logger.error("Cannot remove resource: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }
            if (resource == null) {
                logger.error("Cannot remove resource: resource is null");
                throw new IllegalArgumentException("Resource cannot be null");
            }

            boolean removed = ambient.getResources().remove(resource);
            if (removed) {
                logger.info("Resource {} removed from ambient {}", resource.getName(), ambient.getName());
            } else {
                logger.debug("Resource {} not found in ambient {}", resource.getName(), ambient.getName());
            }
            return removed;
        } catch (Exception e) {
            logger.error("Failed to remove resource: {}", e.getMessage());
            throw new ResourceOperationException("Failed to remove resource");
        }
    }
}
