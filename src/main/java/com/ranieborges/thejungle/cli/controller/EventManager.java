package com.ranieborges.thejungle.cli.controller;

import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.creatures.Bear;
import com.ranieborges.thejungle.cli.model.entity.creatures.Deer; // Added Deer
import com.ranieborges.thejungle.cli.model.entity.creatures.Fish; // Added Fish (for piranha example)
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf;
import com.ranieborges.thejungle.cli.model.entity.itens.*; // Import all item types
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineEffect;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.ToolType;
import com.ranieborges.thejungle.cli.model.events.*;
import com.ranieborges.thejungle.cli.model.events.utils.enums.AfflictionType;
import com.ranieborges.thejungle.cli.model.events.utils.enums.ClimateType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


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
            System.err.println("EventManager Constructor: Random instance was null, creating a new one.");
            this.random = new Random();
        }
        if (this.scanner == null) {
            System.err.println("EventManager Constructor: Scanner instance was null, creating a new one.");
            this.scanner = new Scanner(System.in);
        }
    }

    private void initializeEvents() {
        if (this.availableEvents == null) {
            this.availableEvents = new ArrayList<>();
        } else {
            this.availableEvents.clear();
        }

        availableEvents.add(new ClimaticEvent("Sudden Blizzard", "A fierce blizzard suddenly engulfs the area, visibility drops to near zero!", 0.05, ClimateType.BLIZZARD, 3));
        availableEvents.add(new ClimaticEvent("Heavy Downpour", "Dark clouds gather and a heavy rain begins to fall, quickly soaking you.", 0.10, ClimateType.HEAVY_RAIN, 2));
        availableEvents.add(new ClimaticEvent("Scorching Heat Wave", "The sun beats down mercilessly, and the air becomes oppressively hot and still.", 0.07, ClimateType.HEAT_WAVE, 2));
        availableEvents.add(new ClimaticEvent("Thick Fog Rolls In", "A dense, disorienting fog quickly envelops the surroundings, reducing visibility.", 0.08, ClimateType.THICK_FOG, 1));
        availableEvents.add(new ClimaticEvent("Unseasonal Frost", "A biting frost settles unexpectedly, chilling you to the bone.", 0.04, ClimateType.THICK_FOG, 1)); // Re-using THICK_FOG as a generic "cold snap" type, or add new ClimateType

        availableEvents.add(new CreatureEncounterEvent("Wolf Pack Sighting", "The howls of a wolf pack echo nearby, they sound hungry and close.", 0.12, Wolf.class));
        availableEvents.add(new CreatureEncounterEvent("Lone Wolf Stalks", "You sense you're being followed... a lone, gaunt wolf emerges from the shadows!", 0.08, Wolf.class));
        availableEvents.add(new CreatureEncounterEvent("Bear Confrontation", "A massive bear, disturbed from its foraging, rears up and blocks your path!", 0.06, Bear.class));
        availableEvents.add(new CreatureEncounterEvent("Startled Deer", "A deer bolts from the undergrowth, nearly knocking you over.", 0.07, Deer.class)); // Deer are Fleeing
        availableEvents.add(new CreatureEncounterEvent("Aggressive Piranhas", "The water ahead churns... a school of piranhas with a taste for blood!", 0.05, Fish.class)); // Fish constructor handles piranha type

        List<Item> shelterCache = new ArrayList<>();
        shelterCache.add(new Food("Canned Beans", "A can of preserved beans, surprisingly intact.", 0.5f, 40f, FoodType.CANNED));
        shelterCache.add(new Material("Tattered Tarp", "A piece of waterproof tarp, a bit worn but usable.", 0.3f, MaterialType.OTHER, 5));
        availableEvents.add(new DiscoveryEvent("Abandoned Campsite", "You stumble upon an old, hastily abandoned campsite. Some supplies might be left.", 0.10, DiscoveryEvent.DiscoveryType.ABANDONED_SHELTER, shelterCache));

        availableEvents.add(new DiscoveryEvent("Clear Spring", "You discover a small, clear spring bubbling from mossy rocks. The water looks pure.", 0.08, DiscoveryEvent.DiscoveryType.WATER_SOURCE));

        List<Item> rareNodeItems = new ArrayList<>();
        rareNodeItems.add(new Material("Shimmering Ore", "A vein of shimmering, unknown ore. Seems valuable.", 2.0f, MaterialType.METAL_ORE, 50));
        availableEvents.add(new DiscoveryEvent("Rare Mineral Vein", "You notice a peculiar glint in a rock formation, revealing a rare mineral deposit.", 0.04, DiscoveryEvent.DiscoveryType.RARE_RESOURCE_NODE, rareNodeItems));

        List<Item> hiddenStash = new ArrayList<>();
        hiddenStash.add(new Medicine("Old First-Aid Pouch", "A weathered pouch. Some medical supplies might still be good.", 0.7f, MedicineType.BANDAGE, MedicineEffect.HEAL_WOUNDS, 20f, 2));
        hiddenStash.add(new Tool("Rusty Knife", "A rusty but still sharp knife.", 0.3f, ToolType.KNIFE, 15, 0.7f, 4f));
        availableEvents.add(new DiscoveryEvent("Hidden Stash", "Tucked away beneath a loose stone, you find a small, hidden stash of supplies!", 0.06, DiscoveryEvent.DiscoveryType.HIDDEN_CACHE, hiddenStash));

        List<Item> shrineLoot = new ArrayList<>();
        shrineLoot.add(new Material("Ancient Tablet Fragment", "A piece of an old stone tablet with strange carvings.", 0.5f, MaterialType.STONE, 10)); // Could be a quest item or lore
        availableEvents.add(new DiscoveryEvent("Ruined Shrine Detail", "Amongst some ruins, you find a small, weathered shrine with an interesting artifact.", 0.05, DiscoveryEvent.DiscoveryType.MYSTERIOUS_RUINS_DETAIL, shrineLoot));

        availableEvents.add(new DiscoveryEvent("Weathered Signpost", "You find a barely legible signpost pointing towards what might be a landmark or different area.", 0.07, DiscoveryEvent.DiscoveryType.HIDDEN_CACHE, null)); // No items, just info

        availableEvents.add(new HealthEvent("Twisted Ankle", "You misstep on uneven ground and twist your ankle painfully.", 0.07, AfflictionType.SPRAINED_ANKLE, -5f, -5f, -10f));
        availableEvents.add(new HealthEvent("Minor Laceration", "A sharp branch or rock scratches your arm, drawing blood.", 0.08, AfflictionType.MINOR_CUT, -10f, -2f, 0f));
        availableEvents.add(new HealthEvent("Sudden Fever", "You suddenly feel feverish, weak, and disoriented.", 0.04, AfflictionType.FEVER, -5f, -10f, -15f));
        availableEvents.add(new HealthEvent("Food Poisoning Symptoms", "That last meal isn't sitting right... you feel nauseous and weak.", 0.05, AfflictionType.FOOD_POISONING, -10f, -8f, -10f));
        availableEvents.add(new HealthEvent("Deep Wound Infection", "An old wound starts to look angry and infected, throbbing with pain.", 0.02, AfflictionType.INFECTION, -15f, -10f, -20f));
        availableEvents.add(new HealthEvent("Chilling Cold", "The cold seeps deep into your bones. You struggle to stay warm.", 0.06, AfflictionType.HYPOTHERMIA, -2f, -5f, -10f)); // Initial effects
        availableEvents.add(new HealthEvent("Sudden Dehydration", "An overwhelming thirst hits you, making you feel dizzy and weak.", 0.05, AfflictionType.DEHYDRATION_SPELL, 0f, -5f, -15f)); // Mainly affects thirst/energy/sanity

        availableEvents.add(new FactionInteractionEvent("Faction Sighting", "You spot a group in the distance that seems organized. They might have seen you too.", 0.15));
        availableEvents.add(new FactionInteractionEvent("Nomad Encounter", "You come across a temporary camp of what appear to be Peaceful Nomads.", 0.08, "nomads_peaceful"));
        availableEvents.add(new FactionInteractionEvent("Desperate Plea", "A small, haggard group of Desperate Survivors approaches, begging for food.", 0.07, "survivors_desperate"));
        availableEvents.add(new FactionInteractionEvent("Hunters' Warning", "You stumble into territory marked by the Brutal Hunters. They don't look pleased.", 0.06, "hunters_brutal"));

    }

    public String triggerRandomEvent(Character player, Ambient ambient, TurnController turnController) {
        if (random == null || scanner == null) {
            Message.displayOnScreen("Warning: EventManager not fully initialized. Cannot trigger event.");
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
            } else {
                switch (event) {
                    case ClimaticEvent climaticEvent when ambientEventModifiers.containsKey("ClimaticEventGeneral") ->
                        probability *= ambientEventModifiers.get("ClimaticEventGeneral");
                    case
                        CreatureEncounterEvent creatureEncounterEvent when ambientEventModifiers.containsKey("CreatureEncounterGeneral") ->
                        probability *= ambientEventModifiers.get("CreatureEncounterGeneral");
                    case
                        DiscoveryEvent discoveryEvent when ambientEventModifiers.containsKey("DiscoveryEventGeneral") ->
                        probability *= ambientEventModifiers.get("DiscoveryEventGeneral");
                    case HealthEvent healthEvent when ambientEventModifiers.containsKey("HealthEventGeneral") ->
                        probability *= ambientEventModifiers.get("HealthEventGeneral");
                    case
                        FactionInteractionEvent factionInteractionEvent when ambientEventModifiers.containsKey("FactionInteractionGeneral") ->
                        probability *= ambientEventModifiers.get("FactionInteractionGeneral");
                    default -> {
                    }
                }
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
