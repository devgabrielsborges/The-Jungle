package com.ranieborges.thejungle.cli.model.world;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Random;


@Getter
public abstract class Ambient {
    private final String name;
    private final String description;
    private final float explorationDifficulty;

    @Setter
    private String currentWeather;

    public transient final Random random = new Random();

    public Ambient(String name, String description, float explorationDifficulty, String defaultWeather) {
        this.name = name;
        this.description = description;
        this.explorationDifficulty = explorationDifficulty;
        this.currentWeather = defaultWeather;
    }

    public abstract String explore(Character character);
    public abstract List<String> getAvailableResourceTypes();
    public abstract List<Class<? extends Creature>> getTypicalCreatures();
    public abstract String changeWeather();
    public abstract Map<String, Double> getEventProbabilities();

    @Override
    public String toString() {
        return TerminalStyler.style(String.format("%s: %s (Difficulty: %.1f, Weather: %s)", name, description, explorationDifficulty, currentWeather), TerminalStyler.BRIGHT_CYAN);
    }
}
