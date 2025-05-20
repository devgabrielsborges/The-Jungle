package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for spawning resources and creatures in different ambient types
 */
public class ResourceSpawner implements UI {
    private static final Logger logger = LoggerFactory.getLogger(ResourceSpawner.class);

    private List<Material> materialsOnMap = new ArrayList<>();
    private List<Deer> deers = new ArrayList<>();
    private List<Cannibal> cannibals = new ArrayList<>();

    /**
     * Spawn resources appropriate for the given ambient
     * 
     * @param ambient The ambient to spawn resources in
     * @param map     The current map
     * @return The list of materials placed on the map
     */
    public List<Material> spawnResources(Ambient ambient, int[][] map) {
        materialsOnMap.clear();

        try {
            if (ambient instanceof Cave) {
                materialsOnMap = Material.spawnSmallRocks(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE);
            } else if (ambient instanceof Jungle || ambient instanceof LakeRiver) {
                materialsOnMap = Material.spawnSticksAndRocks(5, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE);
                materialsOnMap.addAll(Material.spawnTrees(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
                materialsOnMap
                        .addAll(Material.spawnMedicinalPlants(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
                materialsOnMap.addAll(Material.spawnBerryBushes(4, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE));
            } else {
                // For other ambient types, start with an empty list
                materialsOnMap = new ArrayList<>();
            }

            logger.debug("Spawned {} materials for ambient: {}", materialsOnMap.size(), ambient.getName());
            return materialsOnMap;
        } catch (Exception e) {
            logger.error("Error spawning resources: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Spawn deer appropriate for the given ambient
     * 
     * @param ambient The ambient to spawn creatures in
     * @param map     The current map
     * @return The list of spawned deer
     */
    public List<Deer> spawnCreatures(Ambient ambient, int[][] map) {
        try {
            // Spawn deers in any ambient with grass
            deers = Creature.regenerateCreatures(
                    5, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE,
                    Deer::new, ambient, Deer::canSpawnIn);

            logger.debug("Spawned {} deers", deers.size());
            return deers;
        } catch (Exception e) {
            logger.error("Error spawning deer: {}", e.getMessage());
            deers = new ArrayList<>();
            return deers;
        }
    }

    /**
     * Spawn cannibals appropriate for the given ambient
     * 
     * @param ambient The ambient to spawn creatures in
     * @param map     The current map
     * @return The list of spawned cannibals
     */
    public List<Cannibal> spawnCannibals(Ambient ambient, int[][] map) {
        try {
            // Spawn cannibals mainly in caves
            cannibals = Creature.regenerateCreatures(
                    3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE,
                    Cannibal::new, ambient, Cannibal::canSpawnIn);

            logger.debug("Spawned {} cannibals", cannibals.size());
            return cannibals;
        } catch (Exception e) {
            logger.error("Error spawning cannibals: {}", e.getMessage());
            cannibals = new ArrayList<>();
            return cannibals;
        }
    }

    /**
     * Get the materials currently on the map
     */
    public List<Material> getMaterialsOnMap() {
        return materialsOnMap;
    }

    /**
     * Get the deers currently on the map
     */
    public List<Deer> getDeers() {
        return deers;
    }

    /**
     * Get the cannibals currently on the map
     */
    public List<Cannibal> getCannibals() {
        return cannibals;
    }
}
