package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.*;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for managing map generation and map-related operations
 */
public class MapManager implements UI {
    private static final Logger logger = LoggerFactory.getLogger(MapManager.class);

    private final Random random = new Random();
    private Ambient currentAmbient;
    private int[][] map;
    private int currentAmbientUseCount = 0;

    /**
     * Create a new MapManager with the given ambient
     *
     * @param ambient The initial ambient
     */
    public MapManager(Ambient ambient) {
        this.currentAmbient = ambient;
        generateMap();
    }

    /**
     * Generate a map for the current ambient, or rotate to a new ambient if needed
     *
     * @return The generated map
     */
    public int[][] generateMap() {
        try {
            // Increment ambient use counter
            currentAmbientUseCount++;

            // Check if we need to rotate ambient
            if (currentAmbientUseCount > Ambient.MAX_AMBIENT_USES) {
                // Switch to a new ambient
                Ambient newAmbient = getNextAmbient();

                // Update reference to current ambient
                currentAmbient = newAmbient;

                logger.info("Rotating ambient to: {}", newAmbient.getName());
                currentAmbientUseCount = 1; // Reset counter for new ambient
            }

            // Generate the map for the current ambient
            map = currentAmbient.generateMap(MAP_WIDTH, MAP_HEIGHT);

            logger.info("Map generated for ambient: {} (use {}/{})",
                    currentAmbient.getName(),
                    currentAmbientUseCount,
                    Ambient.MAX_AMBIENT_USES);

            return map;

        } catch (Exception e) {
            logger.error("Error generating map: {}", e.getMessage());
            // Fallback map generation
            map = new int[MAP_HEIGHT][MAP_WIDTH];
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    map[y][x] = (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) ? TILE_WALL
                            : TILE_GRASS;
                }
            }
            return map;
        }
    }

    /**
     * Get current map
     *
     * @return The current map array
     */
    public int[][] getMap() {
        return map;
    }

    /**
     * Get next ambient that is different from current
     *
     * @return A new ambient instance
     */
    private Ambient getNextAmbient() {
        // Get current ambient name
        String currentName = currentAmbient.getName();

        // Define all available ambient types
        Ambient[] ambients = {
                new Jungle(),
                new Cave(),
                new LakeRiver(),
                new Mountain(),
                new Ruins()
        };

        // Filter out current ambient
        List<Ambient> availableAmbients = new ArrayList<>();
        for (Ambient a : ambients) {
            if (!a.getName().equals(currentName)) {
                availableAmbients.add(a);
            }
        }

        // Pick a random ambient from available options
        return availableAmbients.get(random.nextInt(availableAmbients.size()));
    }

    /**
     * Generate additional doors for cave ambients
     */
    public void generateCaveDoors() {
        int doorsPlaced = 0;
        int attempts = 0;
        while (doorsPlaced < 2 && attempts < 1000) {
            int x = random.nextInt(MAP_WIDTH);
            int y = random.nextInt(MAP_HEIGHT);

            if (map[y][x] == TILE_WALL && hasAdjacentCave(y, x)) {
                map[y][x] = TILE_DOOR;
                doorsPlaced++;
            }
            attempts++;
        }

        // Remove isolated doors
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map[y][x] == TILE_DOOR) {
                    boolean hasCaveNearby = false;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0)
                                continue;
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_HEIGHT) {
                                if (map[ny][nx] == TILE_CAVE) {
                                    hasCaveNearby = true;
                                    break;
                                }
                            }
                        }
                        if (hasCaveNearby)
                            break;
                    }
                    if (!hasCaveNearby) {
                        map[y][x] = TILE_WALL;
                    }
                }
            }
        }
    }

    /**
     * Check if a tile has an adjacent cave tile
     */
    public boolean hasAdjacentCave(int y, int x) {
        return (x > 0 && map[y][x - 1] == TILE_CAVE) ||
                (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE) ||
                (y > 0 && map[y - 1][x] == TILE_CAVE) ||
                (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE);
    }

    /**
     * Check if a tile has an adjacent floor tile
     */
    public boolean hasAdjacentFloor(int y, int x) {
        return (y > 0 && map[y - 1][x] == TILE_GRASS) ||
                (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_GRASS) ||
                (x > 0 && map[y][x - 1] == TILE_GRASS) ||
                (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_GRASS);
    }

    /**
     * Check if a position is valid for a character to move to
     */
    public boolean isValidPosition(int x, int y) {
        return x > 0 && x < MAP_WIDTH - 1 && y > 0 && y < MAP_HEIGHT - 1 &&
                (map[y][x] == TILE_GRASS ||
                        (currentAmbient.getName().equals("Cave") && map[y][x] == TILE_CAVE));
    }

    /**
     * Check if a position is valid for spawning
     */
    public boolean isValidSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
            return false;
        return map[y][x] == TILE_GRASS;
    }

    /**
     * Check if a position is valid for spawning in a cave
     */
    public boolean isValidCaveSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
            return false;
        return map[y][x] == TILE_CAVE;
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
     * Add doors at valid positions
     */
    public void addDoors(int doorsToAdd) {
        int attempts = 0;
        while (doorsToAdd > 0 && attempts < 1000) { // Limit attempts
            int x = random.nextInt(MAP_WIDTH);
            int y = random.nextInt(MAP_HEIGHT);

            // Check conditions for door placement
            if (map[y][x] == TILE_WALL && isAdjacentToCave(x, y)) {
                map[y][x] = TILE_DOOR;
                doorsToAdd--;
            }
            attempts++;
        }
    }

    /**
     * Check if a wall is adjacent to a cave tile
     */
    public boolean isAdjacentToCave(int x, int y) {
        if (x > 0 && map[y][x - 1] == TILE_CAVE)
            return true;
        if (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE)
            return true;
        if (y > 0 && map[y - 1][x] == TILE_CAVE)
            return true;
        if (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE)
            return true;
        return false;
    }

    /**
     * Count the number of doors in the map
     */
    public int countDoors() {
        int count = 0;
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map[y][x] == TILE_DOOR) {
                    count++;
                }
            }
        }
        return count;
    }
}
