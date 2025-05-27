package com.ranieborges.thejungle.cli.controller;

import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.creatures.Bear;
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.itens.Medicine;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineType;
import com.ranieborges.thejungle.cli.model.events.ClimaticEvent;
import com.ranieborges.thejungle.cli.model.events.CreatureEncounterEvent;
import com.ranieborges.thejungle.cli.model.events.DiscoveryEvent;
import com.ranieborges.thejungle.cli.model.events.HealthEvent;
import com.ranieborges.thejungle.cli.model.events.utils.enums.AfflictionType;
import com.ranieborges.thejungle.cli.model.events.utils.enums.ClimateType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message; // For Message.displayOnScreen

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner; // Added for potential pause in triggerRandomEvent

import static com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineEffect.HEAL_WOUNDS;

public class EventManager {

    private List<Event> availableEvents;
    private transient Random random;
    private transient Scanner scanner;

    public EventManager(Random randomInstance, Scanner scannerInstance) {
        this.random = randomInstance;
        this.scanner = scannerInstance;
        this.availableEvents = new ArrayList<>();
        initializeEvents();

        if (this.random == null) {
            System.err.println("EventManager Constructor: Random instance was null, creating a new one. Ensure reinitializeTransientFields is called after loading if this was unintended.");
            this.random = new Random();
        }
        if (this.scanner == null) {
            System.err.println("EventManager Constructor: Scanner instance was null, creating a new one. Ensure reinitializeTransientFields is called after loading if this was unintended.");
            this.scanner = new Scanner(System.in);
        }
    }

    private void initializeEvents() {
        if (this.availableEvents == null) {
            this.availableEvents = new ArrayList<>();
        } else {
            this.availableEvents.clear();
        }

        // Climatic Events
        availableEvents.add(new ClimaticEvent("Sudden Blizzard", "A fierce blizzard suddenly engulfs the area!", 0.05, ClimateType.BLIZZARD, 3));
        availableEvents.add(new ClimaticEvent("Heavy Downpour", "Dark clouds gather and a heavy rain begins to fall.", 0.10, ClimateType.HEAVY_RAIN, 2));
        availableEvents.add(new ClimaticEvent("Scorching Heat Wave", "The sun beats down mercilessly, and the air becomes oppressively hot.", 0.07, ClimateType.HEAT_WAVE, 2));
        availableEvents.add(new ClimaticEvent("Thick Fog Rolls In", "A dense fog quickly envelops the surroundings, reducing visibility.", 0.08, ClimateType.THICK_FOG, 1));

        // Creature Encounter Events
        availableEvents.add(new CreatureEncounterEvent("Wolf Pack Sighting", "A pack of wolves is spotted nearby, they look hungry.", 0.12, Wolf.class));
        availableEvents.add(new CreatureEncounterEvent("Lone Wolf Stalks", "You sense you're being followed... a lone wolf emerges!", 0.08, Wolf.class));
        availableEvents.add(new CreatureEncounterEvent("Bear Confrontation", "A large bear blocks your path!", 0.06, Bear.class));

        // Discovery Events
        List<Item> shelterCache = new ArrayList<>();
        shelterCache.add(new Food("Canned Beans", "A can of preserved beans.", 0.5f, 40f, FoodType.CANNED));
        shelterCache.add(new Material("Tarp Fragment", "A piece of waterproof tarp.", 0.3f, MaterialType.OTHER, 5));
        availableEvents.add(new DiscoveryEvent("Abandoned Campsite", "You find an old, hastily abandoned campsite.", 0.10, DiscoveryEvent.DiscoveryType.ABANDONED_SHELTER, shelterCache));

        availableEvents.add(new DiscoveryEvent("Clear Spring", "You discover a small, clear spring bubbling from the rocks.", 0.08, DiscoveryEvent.DiscoveryType.WATER_SOURCE));

        List<Item> rareNodeItems = new ArrayList<>();
        rareNodeItems.add(new Material("Unusual Ore Vein", "A vein of shimmering, unknown ore.", 2.0f, MaterialType.METAL_ORE, 50));
        availableEvents.add(new DiscoveryEvent("Rare Mineral Vein", "You notice a peculiar glint in a rock formation.", 0.04, DiscoveryEvent.DiscoveryType.RARE_RESOURCE_NODE, rareNodeItems));

        List<Item> hiddenStash = new ArrayList<>();
        hiddenStash.add(new Medicine("Old First-Aid Kit", "A weathered first-aid kit, some supplies might still be good.", 0.7f, MedicineType.BANDAGE, HEAL_WOUNDS, 20f, 2));
        availableEvents.add(new DiscoveryEvent("Hidden Stash", "Tucked away, you find a small, hidden stash of supplies!", 0.06, DiscoveryEvent.DiscoveryType.HIDDEN_CACHE, hiddenStash));

        // Health Events
        availableEvents.add(new HealthEvent("Twisted Ankle", "You misstep on uneven ground and twist your ankle.", 0.07, AfflictionType.SPRAINED_ANKLE, -5f, -3f, -10f));
        availableEvents.add(new HealthEvent("Minor Laceration", "A sharp branch scratches your arm, drawing blood.", 0.08, AfflictionType.MINOR_CUT, -10f, -2f, 0f));
        availableEvents.add(new HealthEvent("Sudden Fever", "You suddenly feel feverish and weak.", 0.04, AfflictionType.FEVER, -5f, -10f, -15f));
        availableEvents.add(new HealthEvent("Food Poisoning Symptoms", "That last meal isn't sitting right... you feel nauseous.", 0.05, AfflictionType.FOOD_POISONING, -10f, -5f, -10f));
        availableEvents.add(new HealthEvent("Deep Wound Infection", "An old wound starts to look infected and feels worse.", 0.02, AfflictionType.INFECTION, -15f, -10f, -20f));
    }

    public String triggerRandomEvent(Character player, Ambient ambient, TurnController turnController) {
        if (random == null || scanner == null) {
            Message.displayOnScreen("Warning: EventManager not fully initialized (random or scanner is null). Cannot trigger event.");
            return "The " + (ambient != null ? ambient.getName() : "area") + " remains eerily quiet due to an internal error.";
        }
        if (availableEvents == null || availableEvents.isEmpty()) {
            Message.displayOnScreen("Warning: No events available in EventManager. Attempting to re-initialize.");
            initializeEvents();
            if (availableEvents.isEmpty()) {
                return "No events are configured in the game.";
            }
        }

        Map<String, Double> ambientEventModifiers = (ambient != null) ? ambient.getEventProbabilities() : Collections.emptyMap();
        List<Event> possibleEventsThisTurn = new ArrayList<>();

        for (Event event : availableEvents) {
            if (event.canOccur(player, ambient)) {
                possibleEventsThisTurn.add(event);
            }
        }

        if (possibleEventsThisTurn.isEmpty()) {
            return "The " + (ambient != null ? ambient.getName() : "area") + " remains uneventful for now.";
        }

        Collections.shuffle(possibleEventsThisTurn, random);

        for (Event event : possibleEventsThisTurn) {
            double probability = event.getBaseProbability();
            if (ambientEventModifiers.containsKey(event.getName())) {
                probability = ambientEventModifiers.get(event.getName());
            } else if (event instanceof ClimaticEvent && ambientEventModifiers.containsKey("ClimaticEventGeneral")) {
                probability *= ambientEventModifiers.get("ClimaticEventGeneral");
            }

            if (random.nextDouble() < probability) {
                String eventOutcome = event.execute(player, ambient, turnController);
                Message.displayOnScreen("Press Enter to continue after the event...");
                scanner.nextLine();
                return eventOutcome;
            }
        }
        return "The " + (ambient != null ? ambient.getName() : "area") + " was relatively calm, no major events occurred.";
    }

    public void reinitializeTransientFields(Random random, Scanner scanner) {
        this.random = random;
        this.scanner = scanner;

        if (this.random == null) {
            System.err.println("EventManager.reinitializeTransientFields: Random instance is null, creating a new one.");
            this.random = new Random();
        }
        if (this.scanner == null) {
            System.err.println("EventManager.reinitializeTransientFields: Scanner instance is null, creating a new one.");
            this.scanner = new Scanner(System.in);
        }

        if (this.availableEvents == null || this.availableEvents.isEmpty()) {
            System.out.println("EventManager: availableEvents list is empty after load or reinitialization. Re-populating default events.");
            initializeEvents();
        }
    }
}
