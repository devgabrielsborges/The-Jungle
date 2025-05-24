package io.github.com.ranie_borges.thejungle.controller.managers;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CharacterManager implements UI {
    private static final Logger logger = LoggerFactory.getLogger(CharacterManager.class);

    private Character character;
    private Ambient currentAmbient;
    private int[][] map;

    public CharacterManager(Character character, Ambient ambient) {
        this.character = character;
        this.currentAmbient = ambient;
    }

    public boolean updateCharacterMovement(float delta) {
        if (character == null || map == null) {
            logger.warn("Character or map is null in updateCharacterMovement, skipping.");
            return false;
        }

        boolean passedThroughDoor = character.tryMove(
            delta, map, TILE_SIZE, TILE_WALL, TILE_DOOR, TILE_CAVE, MAP_WIDTH, MAP_HEIGHT);

        if (currentAmbient instanceof Jungle jungle) {
            int tileX = (int) ((character.getPosition().x + TILE_SIZE / 2f) / TILE_SIZE);
            int tileY = (int) ((character.getPosition().y + TILE_SIZE / 4f) / TILE_SIZE);

            if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
                character.setInTallGrass(jungle.isTallGrass(tileX, tileY));
                if (character.isInTallGrass()) {
                    jungle.checkSnakeBite(character);
                }
            } else {
                character.setInTallGrass(false);
            }
        } else {
            character.setInTallGrass(false);
        }
        return passedThroughDoor;
    }

    public void safeSpawnCharacter() {
        if (character == null || map == null) {
            logger.error("Cannot safe spawn character, character or map is null.");
            return;
        }
        boolean spawnFound = false;
        int attempts = 0;
        int maxAttempts = 1000;

        while (!spawnFound && attempts < maxAttempts) {
            int x = (int) (Math.random() * MAP_WIDTH);
            int y = (int) (Math.random() * MAP_HEIGHT);

            if (y < 0 || y >= map.length || x < 0 || x >= map[0].length) continue;

            int tile = map[y][x];
            boolean validTile = tile == TILE_GRASS ||
                (currentAmbient != null && currentAmbient.getName().toLowerCase().contains("cave") && tile == TILE_CAVE);

            if (validTile && currentAmbient instanceof Jungle && ((Jungle)currentAmbient).isTallGrass(x,y)) {
                    validTile = false;
                }


            if (validTile) {
                character.getPosition().set(x * TILE_SIZE, y * TILE_SIZE);
                spawnFound = true;
                logger.info("Character {} safely spawned at tile ({},{}) in {}.", character.getName(), x, y, currentAmbient != null ? currentAmbient.getName() : "Unknown Ambient");
            }
            attempts++;
        }

        if (!spawnFound) {
            float fallbackX = ((float) MAP_WIDTH / 2) * TILE_SIZE;
            float fallbackY = ((float) MAP_HEIGHT / 2) * TILE_SIZE;
            character.getPosition().set(fallbackX, fallbackY);
            logger.warn("Couldn't find safe spawn for {} after {} attempts. Using center fallback ({},{}).",
                character.getName(), maxAttempts, (int)(fallbackX/TILE_SIZE), (int)(fallbackY/TILE_SIZE));
        }
    }

    public void updateCharacterStats(float delta) {
        if (character == null) return;

        float baseHungerDepletionPerSecond = 0.5f; // Example: 0.5 hunger points per second
        float baseThirstDepletionPerSecond = 0.75f; // Example: 0.75 thirst points per second
        // Energy depletion might be tied to actions more than time, or a very slow passive drain
        float energyDepletionPerSecond = 0.1f;

        float hungerModifier = character.getHungerDepletionModifier();
        float thirstModifier = character.getThirstDepletionModifier();

        float hungerToDeplete = baseHungerDepletionPerSecond * hungerModifier * delta;
        float thirstToDeplete = baseThirstDepletionPerSecond * thirstModifier * delta;
        float energyToDeplete = energyDepletionPerSecond * delta;

        if (hungerModifier < 1.0f) logger.trace("{} has reduced hunger depletion: {} (-{}%)", character.getName(), hungerToDeplete, (1-hungerModifier)*100);
        if (thirstModifier < 1.0f) logger.trace("{} has reduced thirst depletion: {} (-{}%)", character.getName(), thirstToDeplete, (1-thirstModifier)*100);


        character.setHunger(character.getHunger() - hungerToDeplete);
        character.setThirsty(character.getThirsty() - thirstToDeplete);
        character.setEnergy(character.getEnergy() - energyToDeplete);

        // Penalties for critical stats
        float lifePenalty = 0;
        if (character.getHunger() <= 0) {
            lifePenalty += 2.0f * delta; // Example: 2 life points per second if starving
        } else if (character.getHunger() <= 10) {
            lifePenalty += 0.5f * delta; // Example: 0.5 life points per second if very hungry
        }

        if (character.getThirsty() <= 0) {
            lifePenalty += 3.0f * delta; // Example: 3 life points per second if dehydrated
        } else if (character.getThirsty() <= 10) {
            lifePenalty += 0.75f * delta; // Example: 0.75 life points per second if very thirsty
        }

        if (lifePenalty > 0) {
            character.setLife(character.getLife() - lifePenalty);
             logger.debug("{} lost {} life due to critical hunger/thirst. Current life: {}", character.getName(), lifePenalty, character.getLife());
        }
    }

    public void setMap(int[][] map) { this.map = map; }
    public int[][] getMap() { return map; }
    public Character getCharacter() { return character; }
    public void setCharacter(Character character) { this.character = character; }
    public Ambient getCurrentAmbient() { return currentAmbient; }
    public void setCurrentAmbient(Ambient ambient) { this.currentAmbient = ambient; }
}
