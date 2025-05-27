package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.*;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.events.events.SurvivorRuinsEvent;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.*;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResourceController implements UI {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    private final List<Material> materialsOnMap = new ArrayList<>();
    private final List<Deer> deers = new ArrayList<>();
    private final List<Cannibal> cannibals = new ArrayList<>();
    private final List<Fish> fishes = new ArrayList<>();
    private final List<NPC> NPCS = new ArrayList<>();
    private final List<Boat> boats = new ArrayList<>();
    private final List<RadioGuy> radioGuys = new ArrayList<>();
    private static final float NPC_SPAWN_PROBABILITY = 0.5f;
    private static final float Boat_SPAWN_PROBABILITY = 0.5f;
    private static final float RadioGuy_SPAWN_PROBABILITY = 0.9f;

    private final Random random = new Random();
    private static final int MAX_NPC_TO_SPAWN = 1;
    private static final int MAX_Boat_TO_SPAWN = 1;
    private static final int MAX_RADIOGUY_TO_SPAWN = 1;



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
    public List<NPC> spawnNPC(Ambient ambient, int[][] map) { //
        this.NPCS.clear(); //
        if (!(ambient instanceof Ruins)) { //
            return this.NPCS; //
        }

        // **NOVA LÓGICA DE PROBABILIDADE**
        if (random.nextFloat() >= NPC_SPAWN_PROBABILITY) {
            logger.debug("NPC spawn chance failed for Ruins: {}. No NPC will be spawned this time.", ambient.getName());
            return this.NPCS; // Retorna a lista vazia, pois a chance de spawn falhou
        }

        // Se a chance de spawn for bem-sucedida, determina aleatoriamente quantos NPCs (0 até MAX_NPC_TO_SPAWN)
        int actualNpcSpawnCount = random.nextInt(MAX_NPC_TO_SPAWN + 1);

        if (actualNpcSpawnCount == 0) {
            logger.debug("NPC spawn event occurred for Ruins: {}, but random count was 0. No NPC will be spawned.", ambient.getName());
            return this.NPCS;
        }

        try {
            this.NPCS.addAll(Creature.regenerateCreatures( //
                actualNpcSpawnCount, // Usa a contagem aleatória (por exemplo, 0 ou 1 se MAX_NPC_TO_SPAWN for 1)
                map, //
                MAP_WIDTH, //
                MAP_HEIGHT, //
                TILE_GRASS, //
                TILE_SIZE, //
                NPC::new, //
                ambient, //
                NPC::canSpawnIn //
            ));
            // Corrigir a mensagem de log
            logger.debug("Attempted to spawn {} NPCs in {}. Actually spawned: {}", actualNpcSpawnCount, ambient.getName(), this.NPCS.size()); //
            return this.NPCS; //
        } catch (Exception e) {
            // Corrigir a mensagem de log de erro
            logger.error("Error spawning NPCs: {}", e.getMessage(), e); //
            return new ArrayList<>(); //
        }
    }
    public List<Boat> spawnBoat(Ambient ambient, int[][] map) {
        this.boats.clear();
        if (!(ambient instanceof LakeRiver)) { //
            return this.boats;
        }

        if (random.nextFloat() >= Boat_SPAWN_PROBABILITY) {
            logger.debug("Boat spawn chance failed for LakeRiver: {}. No boat will be spawned this time.", ambient.getName());
            return this.boats;
        }

        int actualBoatSpawnCount = random.nextInt(MAX_Boat_TO_SPAWN + 1);

        if (actualBoatSpawnCount == 0) {
            logger.debug("Boat spawn event occurred for LakeRiver: {}, but random count was 0. No boat will be spawned.", ambient.getName());
            return this.boats;
        }

        try {
            this.boats.addAll(Creature.regenerateCreatures(
                actualBoatSpawnCount,
                map,
                MAP_WIDTH,
                MAP_HEIGHT,
                TILE_WATER,
                TILE_SIZE,
                Boat::new,
                ambient,
                Boat::canSpawnIn
            ));
            logger.debug("Attempted to spawn {} boats in {}. Actually spawned: {}", actualBoatSpawnCount, ambient.getName(), this.boats.size());
            return this.boats;
        } catch (Exception e) {
            logger.error("Error spawning boats: {}", e.getMessage(), e);
            return new ArrayList<>();
        }


    }
    public List<RadioGuy> spawnRadioGuy(Ambient ambient, int[][] map) {
        this.radioGuys.clear();
        if (!(ambient instanceof Mountain)) { //
            return this.radioGuys;
        }

        if (random.nextFloat() >= RadioGuy_SPAWN_PROBABILITY) {
            logger.debug("radioGuys spawn chance failed for Mountain: {}. No boat will be spawned this time.", ambient.getName());
            return this.radioGuys;
        }

        int actualRadioGuySpawnCount = random.nextInt(MAX_RADIOGUY_TO_SPAWN + 1);

        if (actualRadioGuySpawnCount == 0) {
            return this.radioGuys;
        }

        try {
            this.radioGuys.addAll(Creature.regenerateCreatures(
                actualRadioGuySpawnCount,
                map,
                MAP_WIDTH,
                MAP_HEIGHT,
                TILE_GRASS,
                TILE_SIZE,
                RadioGuy::new,
                ambient,
                RadioGuy::canSpawnIn
            ));
            logger.debug("Attempted to spawn {} radioGuys in {}. Actually spawned: {}", actualRadioGuySpawnCount, ambient.getName(), this.radioGuys.size());
            return this.radioGuys;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Material> getMaterialsOnMap() { return materialsOnMap; }
    public List<Deer> getDeers() { return deers; }
    public List<Cannibal> getCannibals() { return cannibals; }
    public List<Fish> getFishes() { return fishes; } // Getter for fish
}
