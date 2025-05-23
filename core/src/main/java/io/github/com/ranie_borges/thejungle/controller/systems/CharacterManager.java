package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages character states, statistics, and interaction with the game
 * environment
 */
public class CharacterManager implements UI {
    private static final Logger logger = LoggerFactory.getLogger(CharacterManager.class);

    private Character character;
    private Ambient currentAmbient;
    private int[][] map;
    private boolean isMoving = false;
    private float stateTime = 0;

    /**
     * Create a new CharacterManager
     *
     * @param character The character to manage
     * @param ambient   The current ambient
     */
    public CharacterManager(Character character, Ambient ambient) {
        this.character = character;
        this.currentAmbient = ambient;
    }

    /**
     * Try to move the character based on inputs and map constraints
     *
     * @param delta Time since last frame
     * @return true if the character passed through a door
     */
    public boolean updateCharacterMovement(float delta) {
        if (character == null)
            return false;

        boolean passedThroughDoor = character.tryMove( // Certifique-se que a chamada para tryMove inclua 'currentAmbient'
            delta,
            map,
            TILE_SIZE,    // Ou UI.TILE_SIZE se TILE_SIZE não for campo direto aqui
            TILE_WALL,    // Ou UI.TILE_WALL etc.
            TILE_DOOR,
            TILE_CAVE,
            TILE_WATER,
            TILE_WETGRASS,
            MAP_WIDTH,    // Ou UI.MAP_WIDTH etc.
            MAP_HEIGHT,
            currentAmbient // <<< AQUI você passa o ambiente atual
        );

        // ... resto do método ...
        if (currentAmbient instanceof Jungle) {
            Jungle jungle = (Jungle) currentAmbient;
            float centerX = character.getPosition().x + TILE_SIZE / 2f; // Use TILE_SIZE da UI ou passe como parâmetro
            float centerY = character.getPosition().y + TILE_SIZE / 2f; // Use TILE_SIZE da UI ou passe como parâmetro
            int tileX = (int) (centerX / TILE_SIZE); // Use TILE_SIZE da UI ou passe como parâmetro
            int tileY = (int) (centerY / TILE_SIZE); // Use TILE_SIZE da UI ou passe como parâmetro

            if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) { // Use MAP_WIDTH e MAP_HEIGHT da UI ou passe como parâmetro
                character.setInTallGrass(jungle.isTallGrass(tileX, tileY));
            } else {
                character.setInTallGrass(false);
            }
            jungle.checkSnakeBite(character);
        } else {
            character.setInTallGrass(false);
        }
        return passedThroughDoor;
    }

    /**
     * Spawn the character safely on the current map
     */
    public void safeSpawnCharacter() {
        boolean spawnFound = false;
        int attempts = 0;
        int maxAttempts = 1000;

        while (!spawnFound && attempts < maxAttempts) {
            int x = (int) (Math.random() * MAP_WIDTH);
            int y = (int) (Math.random() * MAP_HEIGHT);
            int tile = map[y][x];

            boolean validTile = tile == TILE_GRASS ||
                    (currentAmbient.getName().toLowerCase().contains("cave") && tile == TILE_CAVE);

            if (validTile) {
                character.getPosition().set(x * TILE_SIZE, y * TILE_SIZE);
                spawnFound = true;
            }

            attempts++;
        }

        if (!spawnFound) {
            character.getPosition().set(((float) MAP_WIDTH / 2) * TILE_SIZE, ((float) MAP_HEIGHT / 2) * TILE_SIZE);
            logger.warn("Couldn't find safe spawn after {} attempts. Using center fallback.", maxAttempts);
        }

    }

    /**
     * Update character stats over time
     *
     * @param delta Time since last frame
     */
    public void updateCharacterStats(float delta) {
        float hungerDepletion = 0.01f * delta;
        float thirstDepletion = 0.015f * delta;
        float energyDepletion = 0.005f * delta;

        character.setHunger(Math.max(0, character.getHunger() - hungerDepletion));
        character.setThirsty(Math.max(0, character.getThirsty() - thirstDepletion));
        character.setEnergy(Math.max(0, character.getEnergy() - energyDepletion));

        if (character.getHunger() <= 10 || character.getThirsty() <= 10) {
            character.setLife(Math.max(0, character.getLife() - 0.05f * delta));
        }
    }

    /**
     * Set the current map
     */
    public void setMap(int[][] map) {
        this.map = map;
    }

    /**
     * Get the current map
     */
    public int[][] getMap() {
        return map;
    }

    /**
     * Get the character
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Set the character
     */
    public void setCharacter(Character character) {
        this.character = character;
    }

    /**
     * Get the current ambient
     */
    public Ambient getCurrentAmbient() {
        return currentAmbient;
    }

    /**
     * Set the current ambient
     */
    public void setCurrentAmbient(Ambient ambient) {
        this.currentAmbient = ambient;
    }

    /**
     * Check if the character is moving
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Set if the character is moving
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * Get the animation state time
     */
    public float getStateTime() {
        return stateTime;
    }
}
