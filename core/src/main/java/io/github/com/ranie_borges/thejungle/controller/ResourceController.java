package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Fish; // Import Fish
import io.github.com.ranie_borges.thejungle.model.entity.creatures.NPC;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ResourceController implements UI {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    private final List<Material> materialsOnMap = new ArrayList<>();
    private final List<Deer> deers = new ArrayList<>();
    private final List<Cannibal> cannibals = new ArrayList<>();
    private final List<Fish> fishes = new ArrayList<>();
    private final List<NPC> NPCS = new ArrayList<>();


    public List<Material> spawnResources(Ambient ambient, int[][] map) {
        materialsOnMap.clear();
        try {
            if (ambient instanceof Cave) {
                materialsOnMap.addAll(Material.spawnSmallRocks(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE));
            } else if (ambient instanceof Jungle || ambient instanceof LakeRiver) {
                materialsOnMap.addAll(Material.spawnSticksAndRocks(5, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
                materialsOnMap.addAll(Material.spawnTrees(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
                materialsOnMap.addAll(Material.spawnMedicinalPlants(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
                materialsOnMap.addAll(Material.spawnBerryBushes(4, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
            }
            logger.debug("Spawned {} materials for ambient: {}", materialsOnMap.size(), ambient.getName());
            return materialsOnMap;
        } catch (Exception e) {
            logger.error("Error spawning resources: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Deer> spawnCreatures(Ambient ambient, int[][] map) {
        deers.clear();
        try {
            deers.addAll(Creature.regenerateCreatures(
                5, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE,
                Deer::new, ambient, Deer::canSpawnIn));
            logger.debug("Spawned {} deers in {}", deers.size(), ambient.getName());
            return deers;
        } catch (Exception e) {
            logger.error("Error spawning deer: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Cannibal> spawnCannibals(Ambient ambient, int[][] map) {
        cannibals.clear();
        try {
            cannibals.addAll(Creature.regenerateCreatures(
                3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE, // Cannibals prefer caves
                Cannibal::new, ambient, Cannibal::canSpawnIn));
            logger.debug("Spawned {} cannibals in {}", cannibals.size(), ambient.getName());
            return cannibals;
        } catch (Exception e) {
            logger.error("Error spawning cannibals: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // New method to spawn fish
    public List<Fish> spawnFish(Ambient ambient, int[][] map) {
        this.fishes.clear();
        if (!(ambient instanceof LakeRiver)) {
            // logger.debug("Skipping fish spawn, not in LakeRiver ambient. Current ambient: {}", ambient.getName());
            return this.fishes;
        }
        try {
            this.fishes.addAll(Creature.regenerateCreatures(
                5, // Number of fish to attempt to spawn
                map,
                MAP_WIDTH,
                MAP_HEIGHT,
                TILE_WATER, // Fish spawn in water tiles
                TILE_SIZE,
                Fish::new,
                ambient,
                Fish::canSpawnIn // Use the static canSpawnIn method from Fish class
            ));
            logger.debug("Spawned {} fishes in {}", this.fishes.size(), ambient.getName());
            return this.fishes;
        } catch (Exception e) {
            logger.error("Error spawning fish: {}", e.getMessage(), e);
            return new ArrayList<>(); // Return empty list on error
        }
    }
    public List<NPC> spawnNPC(Ambient ambient, int[][] map) {
        this.NPCS.clear();
        if (!(ambient instanceof Ruins)) {
            return this.NPCS;
        }
        try {
            this.NPCS.addAll(Creature.regenerateCreatures(
                1,
                map,
                MAP_WIDTH,
                MAP_HEIGHT,
                TILE_GRASS,
                TILE_SIZE,
                NPC::new,
                ambient,
                NPC::canSpawnIn
            ));
            logger.debug("Spawned {} fishes in {}", this.NPCS.size(), ambient.getName());
            return this.NPCS;
        } catch (Exception e) {
            logger.error("Error spawning fish: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Material> getMaterialsOnMap() { return materialsOnMap; }
    public List<Deer> getDeers() { return deers; }
    public List<Cannibal> getCannibals() { return cannibals; }
    public List<Fish> getFishes() { return fishes; } // Getter for fish
}
