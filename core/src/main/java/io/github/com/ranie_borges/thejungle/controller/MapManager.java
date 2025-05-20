package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Responsible for map generation and tile-related operations.
 * Follows Single Responsibility Principle by encapsulating all map-related
 * functionality.
 */
public class MapManager implements UI {
    private static final Logger logger = LoggerFactory.getLogger(MapManager.class);
    private int[][] map;
    private Ambient ambient;

    public MapManager(Ambient ambient) {
        this.ambient = ambient;
        this.map = new int[MAP_HEIGHT][MAP_WIDTH];
        generateMap();
    }

    /**
     * Generate a map based on the current ambient
     */
    public void generateMap() {
        try {
            // Generate the map for the current ambient
            map = ambient.generateMap(MAP_WIDTH, MAP_HEIGHT);

            // Add special elements for cave environments
            if (ambient instanceof Cave) {
                generateCaveDoors();
            }

            logger.info("Map generated for ambient: {}", ambient.getName());
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
        }
    }

    /**
     * Create doors in cave environments
     */
    private void generateCaveDoors() {
        int doorsPlaced = 0;
        int attempts = 0;
        while (doorsPlaced < 2 && attempts < 1000) {
            int x = (int) (Math.random() * MAP_WIDTH);
            int y = (int) (Math.random() * MAP_HEIGHT);

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
     * Add a specific number of doors to the map
     */
    public void addDoors(int doorsToAdd) {
        int attempts = 0;
        while (doorsToAdd > 0 && attempts < 1000) { // Limit of attempts
            int x = (int) (Math.random() * MAP_WIDTH);
            int y = (int) (Math.random() * MAP_HEIGHT);

            // Conditions to place door:
            if (map[y][x] == TILE_WALL && isAdjacentToCave(x, y)) {
                map[y][x] = TILE_DOOR;
                doorsToAdd--;
            }
            attempts++;
        }
    }

    /**
     * Check if a wall tile is adjacent to a cave floor
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
     * Check if tile is adjacent to a cave floor
     */
    public boolean hasAdjacentCave(int y, int x) {
        return (x > 0 && map[y][x - 1] == TILE_CAVE) ||
                (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE) ||
                (y > 0 && map[y - 1][x] == TILE_CAVE) ||
                (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE);
    }

    /**
     * Check if tile is adjacent to a grass floor
     */
    public boolean hasAdjacentFloor(int y, int x) {
        return (y > 0 && map[y - 1][x] == TILE_GRASS) ||
                (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_GRASS) ||
                (x > 0 && map[y][x - 1] == TILE_GRASS) ||
                (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_GRASS);
    }

    /**
     * Count doors in the map
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

    /**
     * Check if the position is valid for movement
     */
    public boolean isValidPosition(int x, int y) {
        return x > 0 && x < MAP_WIDTH - 1 && y > 0 && y < MAP_HEIGHT - 1 &&
                (map[y][x] == TILE_GRASS || (ambient.getName().equals("Cave") && map[y][x] == TILE_CAVE));
    }

    /**
     * Check if position is valid for grass spawn
     */
    public boolean isValidSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
            return false;
        return map[y][x] == TILE_GRASS;
    }

    /**
     * Check if position is valid for cave spawn
     */
    public boolean isValidCaveSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
            return false;
        return map[y][x] == TILE_CAVE;
    }

    /**
     * Get a random valid spawn position
     */
    public int[] getRandomSpawnPosition() {
        Random rand = new Random();
        int maxAttempts = 1000;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = rand.nextInt(MAP_WIDTH);
            int y = rand.nextInt(MAP_HEIGHT);
            int tile = map[y][x];

            boolean isValidTile = tile == TILE_GRASS || (ambient instanceof Cave && tile == TILE_CAVE);

            if (isValidTile) {
                return new int[] { x, y };
            }
        }

        // Fallback to center of map
        return new int[] { MAP_WIDTH / 2, MAP_HEIGHT / 2 };
    }

    /**
     * Set the current ambient
     */
    public void setAmbient(Ambient ambient) {
        this.ambient = ambient;
    }

    /**
     * Get current map
     */
    public int[][] getMap() {
        return map;
    }

    /**
     * Set current map
     */
    public void setMap(int[][] map) {
        this.map = map;
    }
}
