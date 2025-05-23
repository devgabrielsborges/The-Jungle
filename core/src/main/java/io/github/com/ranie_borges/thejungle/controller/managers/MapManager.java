package io.github.com.ranie_borges.thejungle.controller.managers;

import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.*;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager implements UI {
    private static final Logger logger = LoggerFactory.getLogger(MapManager.class);

    private final Random random = new Random();
    private Ambient currentAmbient;
    private int[][] map;
    private int currentAmbientUseCount = 0;
    private Ambient ambientBeforeRotation; // New field

    public MapManager(Ambient initialAmbient) {
        if (initialAmbient == null) {
            logger.warn("MapManager initialized with null ambient. Defaulting to Jungle.");
            this.currentAmbient = new Jungle();
        } else {
            this.currentAmbient = initialAmbient;
        }
        this.ambientBeforeRotation = this.currentAmbient; // Initialize
    }

    /**
     * Checks if the ambient needs to be rotated based on usage count.
     * If rotation occurs, updates currentAmbient and resets use count.
     * Sets ambientBeforeRotation to the ambient type that just completed its cycle.
     * @return true if the ambient type was just rotated.
     */
    public boolean checkAndRotateAmbient() {
        this.ambientBeforeRotation = this.currentAmbient; // Store current before potential change
        currentAmbientUseCount++;
        boolean ambientRotated = false;

        if (currentAmbientUseCount > Ambient.MAX_AMBIENT_USES) {
            Ambient newAmbient = getNextAmbientDifferentFrom(this.currentAmbient); // Ensure it's different
            setCurrentAmbientInternal(newAmbient, 1); // Internal method to set and reset count
            ambientRotated = true;
            logger.info("Ambient rotated. Previous: {}, New: {}. Use count reset to 1.",
                this.ambientBeforeRotation.getName(), this.currentAmbient.getName());
        } else {
            logger.info("Ambient {} usage: {}/{}",
                this.currentAmbient.getName(),
                currentAmbientUseCount,
                Ambient.MAX_AMBIENT_USES);
        }
        return ambientRotated;
    }

    // Call this if "Stay Here" is chosen, or a specific ambient is chosen by player
    private void setCurrentAmbientInternal(Ambient newAmbient, int useCount) {
        this.currentAmbient = newAmbient;
        this.currentAmbientUseCount = useCount;
        logger.info("MapManager current ambient explicitly set to: {} with use count: {}", newAmbient.getName(), useCount);
    }


    public void generateMapForCurrentAmbient() {
        try {
            if (currentAmbient == null) {
                logger.error("CurrentAmbient is null in MapManager. Cannot generate map.");
                this.currentAmbient = new Jungle(); // Risky fallback
            }
            map = currentAmbient.generateMap(MAP_WIDTH, MAP_HEIGHT);
            logger.info("Map generated for ambient: {}", currentAmbient.getName());
        } catch (Exception e) {
            logger.error("Error generating map for current ambient ({}): {}", currentAmbient != null ? currentAmbient.getName() : "null", e.getMessage(), e);
            map = new int[MAP_HEIGHT][MAP_WIDTH];
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    map[y][x] = (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) ? TILE_WALL : TILE_GRASS;
                }
            }
        }
    }

    public int[][] getMap() {
        return map;
    }

    private Ambient getNextAmbientDifferentFrom(Ambient ambientToAvoid) {
        String avoidName = (ambientToAvoid != null) ? ambientToAvoid.getName() : "";

        Ambient[] allAmbients = {
            new Cave(), new Jungle(), new LakeRiver(), new Mountain(), new Ruins()
        };

        List<Ambient> availableAmbients = new ArrayList<>();
        for (Ambient a : allAmbients) {
            if (!a.getName().equals(avoidName)) {
                availableAmbients.add(a);
            }
        }

        if (availableAmbients.isEmpty()) {
            // This case should ideally not happen if there's more than one ambient type.
            // Fallback to Jungle if it's not the one to avoid, otherwise pick first from allAmbients.
            if (!"Jungle".equals(avoidName)) return new Jungle();
            return allAmbients.length > 0 ? allAmbients[0] : new Jungle(); // Absolute fallback
        }
        return availableAmbients.get(random.nextInt(availableAmbients.size()));
    }

    public Ambient getAmbientBeforeRotation() {
        return ambientBeforeRotation;
    }

    public void forceSetCurrentAmbient(Ambient ambient, boolean resetUsageCount) {
        this.currentAmbient = ambient;
        if (resetUsageCount) {
            this.currentAmbientUseCount = 1; // Start fresh count for this ambient type
        }
        // ambientBeforeRotation is not changed here as this is a direct override.
        logger.info("MapManager current ambient forced to: {} (Usage count {}).", ambient.getName(), this.currentAmbientUseCount);
    }


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

        for (int y_coord = 0; y_coord < MAP_HEIGHT; y_coord++) {
            for (int x_coord = 0; x_coord < MAP_WIDTH; x_coord++) {
                if (map[y_coord][x_coord] == TILE_DOOR) {
                    boolean hasCaveNearby = false;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0)
                                continue;
                            int nx = x_coord + dx;
                            int ny = y_coord + dy;
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
                        map[y_coord][x_coord] = TILE_WALL;
                    }
                }
            }
        }
    }

    public boolean hasAdjacentCave(int y, int x) {
        return (x > 0 && map[y][x - 1] == TILE_CAVE) ||
            (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE) ||
            (y > 0 && map[y - 1][x] == TILE_CAVE) ||
            (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE);
    }

    public boolean hasAdjacentFloor(int y, int x) {
        return (y > 0 && map[y - 1][x] == TILE_GRASS) ||
            (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_GRASS) ||
            (x > 0 && map[y][x - 1] == TILE_GRASS) ||
            (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_GRASS);
    }

    public boolean isValidPosition(int x, int y) {
        if (currentAmbient == null) return false; // Safety check
        return x > 0 && x < MAP_WIDTH - 1 && y > 0 && y < MAP_HEIGHT - 1 &&
            (map[y][x] == TILE_GRASS ||
                (currentAmbient.getName().toLowerCase().contains("cave") && map[y][x] == TILE_CAVE));
    }

    public boolean isValidSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
            return false;
        return map[y][x] == TILE_GRASS;
    }

    public boolean isValidCaveSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
            return false;
        return map[y][x] == TILE_CAVE;
    }

    public Ambient getCurrentAmbient() {
        return currentAmbient;
    }

    public void setCurrentMap(int[][] loadedMap) {
        if (loadedMap != null && loadedMap.length > 0 && loadedMap[0].length > 0) {
            logger.info("MapManager: Setting map from external source with dimensions: {}x{}",
                loadedMap.length, loadedMap[0].length);
            this.map = loadedMap;
        } else {
            logger.warn("MapManager: Attempted to set null or empty map. Map not changed.");
        }
    }

    // Renamed from setCurrentAmbient to avoid confusion with internal logic
    public void externallySetCurrentAmbient(Ambient ambient) {
        if (ambient != null) {
            this.currentAmbient = ambient;
            this.currentAmbientUseCount = 0; // Or 1 if it counts as the first use
            this.ambientBeforeRotation = ambient; // Align this as well
            logger.info("MapManager: Current ambient externally set to: {}. Usage count reset.", ambient.getName());
        } else {
            logger.warn("MapManager: Attempted to externally set a null ambient.");
        }
    }

    public void addDoors(int doorsToAdd) {
        int attempts = 0;
        while (doorsToAdd > 0 && attempts < 1000) {
            int x = random.nextInt(MAP_WIDTH);
            int y = random.nextInt(MAP_HEIGHT);

            if (map[y][x] == TILE_WALL && isAdjacentToCave(x, y)) {
                map[y][x] = TILE_DOOR;
                doorsToAdd--;
            }
            attempts++;
        }
    }

    public boolean isAdjacentToCave(int x, int y) {
        if (x > 0 && map[y][x - 1] == TILE_CAVE)
            return true;
        if (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE)
            return true;
        if (y > 0 && map[y - 1][x] == TILE_CAVE)
            return true;
        return y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE;
    }

    public int countDoors() {
        int count = 0;
        for (int y_coord = 0; y_coord < MAP_HEIGHT; y_coord++) {
            for (int x_coord = 0; x_coord < MAP_WIDTH; x_coord++) {
                if (map[y_coord][x_coord] == TILE_DOOR) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getCurrentAmbientUseCount() {
        return currentAmbientUseCount;
    }
}
