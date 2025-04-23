package io.github.com.ranie_borges.thejungle.model.world.ambients.enums;

public enum ScenarioType {
    JUNGLE(0, "Jungle", 0.2f, 0.3f),
    RUINS(1, "Ruins", 0.4f, 0.1f),
    CAVE(2, "Cave", 0.6f, 0.05f),
    VILLAGE(3, "Village", 0.15f, 0.5f);

    public final int id;
    public final String name;
    public final float wallDensity;
    public final float itemDensity;

    ScenarioType(int id, String name, float wallDensity, float itemDensity) {
        this.id = id;
        this.name = name;
        this.wallDensity = wallDensity;
        this.itemDensity = itemDensity;
    }
}
