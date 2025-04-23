package io.github.com.ranie_borges.thejungle.model.enums;
public enum Discoveries {
    // Jungle
    VINE_BRIDGE("Vine Bridge", "A fragile bridge made of vines—useful but risky."),
    MONKEY_TERRITORY("Monkey Territory", "An area frequented by monkeys—may lead to food or trouble."),
    POISONOUS_PLANTS("Poisonous Plants", "Brightly colored flora—best avoided unless identified."),
    LOST_RELIC("Lost Relic", "An artifact hidden deep within the jungle—possibly valuable."),

    // Cave
    GLOWING_CRYSTALS("Glowing Crystals", "Luminescent crystals providing faint light and mystery."),
    BAT_COLONY("Bat Colony", "Home to hundreds of bats—noisy and possibly dangerous."),
    SUBTERRANEAN_POOL("Subterranean Pool", "A hidden pool with fresh water deep in the cave."),
    FOSSIL_SITE("Fossil Site", "Ancient fossils embedded in stone—could hold secrets."),

    // Ruins
    COLLAPSED_CHAMBER("Collapsed Chamber", "A once-hidden room now exposed, may hide treasures."),
    ENGRAVED_TABLET("Engraved Tablet", "A stone tablet with old inscriptions—perhaps a clue."),
    CURSED_STATUE("Cursed Statue", "A mysterious statue rumored to be cursed."),
    SECRET_PASSAGE("Secret Passage", "A narrow corridor leading to a deeper part of the ruins."),

    // Lake/River
    FLOATING_LOG("Floating Log", "Could be used to cross the water or craft tools."),
    TURBULENT_WATERS("Turbulent Waters", "Strong currents—crossing is dangerous."),
    FRESHWATER_MUSSELS("Freshwater Mussels", "A good source of food if harvested carefully."),
    SUNKEN_CACHE("Sunken Cache", "Supplies hidden beneath the water’s surface."),

    // Mountain
    WINDY_PEAK("Windy Peak", "A high point offering a wide view—and strong winds."),
    SNOW_PATCH("Snow Patch", "A lingering patch of snow, possibly hiding something beneath."),
    CLIFF_EDGE("Cliff Edge", "Dangerous but reveals distant landmarks."),
    EAGLE_NEST("Eagle Nest", "Nest of a large bird—approach with caution, may have useful materials.");

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
