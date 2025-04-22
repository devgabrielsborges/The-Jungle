package io.github.com.ranie_borges.thejungle.model.enums;
public enum Discoveries {
    ABANDONED_SHELTER("Abandoned Shelter", "An old shelter that might contain useful items."),
    WATER_SOURCE("Water Source", "A natural spring providing clean water."),
    MYSTERIOUS_RUINS("Mysterious Ruins", "Ancient ruins with rare items, possibly trapped."),
    FRUIT_TREE("Fruit Tree", "A tree bearing edible fruits."),
    HIDDEN_CACHE("Hidden Cache", "A container with preserved supplies."),
    NATURAL_MEDICINE("Natural Medicine", "Plants with medicinal properties.");

    private final String name;
    private final String description;

    Discoveries(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
