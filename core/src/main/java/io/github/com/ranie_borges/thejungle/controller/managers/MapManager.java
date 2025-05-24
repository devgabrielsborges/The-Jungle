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

    private final transient Random random = new Random(); // Added transient
    private Ambient currentAmbient;
    private int[][] map;
    private int currentAmbientUseCount = 0;
    private Ambient ambientBeforeRotation;

    public MapManager(Ambient initialAmbient) {
        if (initialAmbient == null) {
            logger.warn("MapManager initialized with null ambient. Defaulting to Jungle.");
            this.currentAmbient = new Jungle();
        } else {
            this.currentAmbient = initialAmbient;
        }
        this.ambientBeforeRotation = this.currentAmbient;
    }

    public boolean checkAndRotateAmbient() {
        this.ambientBeforeRotation = this.currentAmbient;
        currentAmbientUseCount++;
        boolean ambientRotated = false;

        if (currentAmbientUseCount > Ambient.MAX_AMBIENT_USES) {
            Ambient newAmbient = getNextAmbientDifferentFrom(this.currentAmbient);
            setCurrentAmbientInternal(newAmbient, 1);
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

    private void setCurrentAmbientInternal(Ambient newAmbient, int useCount) {
        this.currentAmbient = newAmbient;
        this.currentAmbientUseCount = useCount;
        logger.info("MapManager current ambient explicitly set to: {} with use count: {}", newAmbient.getName(), useCount);
    }

    public void generateMapForCurrentAmbient() {
        try {
            if (currentAmbient == null) {
                logger.error("CurrentAmbient is null in MapManager. Cannot generate map. Defaulting to Jungle.");
                this.currentAmbient = new Jungle();
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

    public int[][] getMap() { return map; }

    private Ambient getNextAmbientDifferentFrom(Ambient ambientToAvoid) {
        String avoidName = (ambientToAvoid != null) ? ambientToAvoid.getName() : "";
        Ambient[] allAmbients = { new Cave(), new Jungle(), new LakeRiver(), new Mountain(), new Ruins() };
        List<Ambient> availableAmbients = new ArrayList<>();
        for (Ambient a : allAmbients) {
            if (!a.getName().equals(avoidName)) availableAmbients.add(a);
        }
        if (availableAmbients.isEmpty()) {
            if (!"Jungle".equals(avoidName)) return new Jungle();
            return allAmbients.length > 0 ? allAmbients[0] : new Jungle();
        }
        return availableAmbients.get(random.nextInt(availableAmbients.size()));
    }

    public Ambient getAmbientBeforeRotation() { return ambientBeforeRotation; }

    public void forceSetCurrentAmbient(Ambient ambient, boolean resetUsageCount) {
        this.currentAmbient = ambient;
        if (resetUsageCount) this.currentAmbientUseCount = 1;
        logger.info("MapManager current ambient forced to: {} (Usage count {}).", ambient.getName(), this.currentAmbientUseCount);
    }

    public void generateCaveDoors() {
        int doorsPlaced = 0; int attempts = 0;
        while (doorsPlaced < 2 && attempts < 1000) {
            int x = random.nextInt(MAP_WIDTH); int y = random.nextInt(MAP_HEIGHT);
            if (map[y][x] == TILE_WALL && hasAdjacentCave(y, x)) { map[y][x] = TILE_DOOR; doorsPlaced++; }
            attempts++;
        }
        for (int y_coord = 0; y_coord < MAP_HEIGHT; y_coord++) {
            for (int x_coord = 0; x_coord < MAP_WIDTH; x_coord++) {
                if (map[y_coord][x_coord] == TILE_DOOR) {
                    boolean hasCaveNearby = false;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue;
                            int nx = x_coord + dx; int ny = y_coord + dy;
                            if (nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_HEIGHT) {
                                if (map[ny][nx] == TILE_CAVE) { hasCaveNearby = true; break; }
                            }
                        }
                        if (hasCaveNearby) break;
                    }
                    if (!hasCaveNearby) map[y_coord][x_coord] = TILE_WALL;
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

    public boolean hasAdjacentFloor(int y, int x) { /* ... */ return false;} // Implementation needed if used
    public boolean isValidPosition(int x, int y) { /* ... */ return false;} // Implementation needed if used
    public boolean isValidSpawnTile(int x, int y) { /* ... */ return false;} // Implementation needed if used
    public boolean isValidCaveSpawnTile(int x, int y) { /* ... */ return false;} // Implementation needed if used

    public Ambient getCurrentAmbient() { return currentAmbient; }

    public void setCurrentMap(int[][] loadedMap) {
        if (loadedMap != null && loadedMap.length > 0 && loadedMap[0].length > 0) {
            logger.info("MapManager: Setting map from external source with dimensions: {}x{}", loadedMap.length, loadedMap[0].length);
            this.map = loadedMap;
        } else {
            logger.warn("MapManager: Attempted to set null or empty map. Map not changed.");
        }
    }

    public void externallySetCurrentAmbient(Ambient ambient) {
        if (ambient != null) {
            this.currentAmbient = ambient;
            this.currentAmbientUseCount = 1; // Start fresh for this externally set ambient
            this.ambientBeforeRotation = ambient;
            logger.info("MapManager: Current ambient externally set to: {}. Usage count reset to 1.", ambient.getName());
        } else {
            logger.warn("MapManager: Attempted to externally set a null ambient.");
        }
    }

    public void addDoors(int doorsToAdd) { /* ... */ } // Implementation needed if used
    public boolean isAdjacentToCave(int x, int y) { /* ... */ return false;} // Implementation needed if used
    public int countDoors() { /* ... */ return 0;} // Implementation needed if used
    public int getCurrentAmbientUseCount() { return currentAmbientUseCount; }
}
