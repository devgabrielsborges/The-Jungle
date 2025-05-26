package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.world.Ambient;

import java.util.List;
import java.util.Map;

public class Jungle extends Ambient {
    private static final float JUNGLE_EXPLORATION_DIFFICULTY = 1.5f;

    public Jungle() {
        super(
                "Jungle",
                "A dense and humid tropical forest, teeming with life and mystery.",
                JUNGLE_EXPLORATION_DIFFICULTY,
                "Hot and humid with occasional rain"
        );
    }

    @Override
    public String explore(Character character) {
        return "";
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return List.of();
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        return List.of();
    }

    @Override
    public String changeWeather() {
        return "";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        return Map.of();
    }
}
