package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf; // Example
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mountain extends Ambient {

    public Mountain() {
        super("Steep Mountains",
                "A treacherous mountain range with jagged peaks and unstable paths. Resources are scarce but valuable.",
                1.5f, // Very difficult to explore
                "Cold and Windy");
    }

    @Override
    public String explore(Character character) {
        String exploreMsg = character.getName() + " carefully ascends the rocky mountain paths.";
        Message.displayOnScreen(exploreMsg);
        character.changeEnergy(-(8 * getExplorationDifficulty())); // Higher base cost

        int outcome = random.nextInt(100);
        if (outcome < 25) { // Find Resource
            exploreMsg += " Found some Iron Ore.";
            Message.displayOnScreen("You chip away at a rock face and find some Iron Ore!");
            // Conceptual: character.getInventory().addItem(new Material("Iron Ore", ...));
        } else if (outcome < 40) { // Encounter Creature (less frequent but potentially tougher)
            exploreMsg += " A shadow passes overhead... a Mountain Eagle circles!";
            Message.displayOnScreen("A large Mountain Eagle circles above, watching you!");
            // TODO: Instantiate Eagle and combat
        } else if (outcome < 60) { // Minor Discovery: Cave Entrance
            exploreMsg += " Discovered the entrance to a dark cave.";
            Message.displayOnScreen("You find the narrow entrance to a dark cave system.");
            // TODO: Option to enter cave (change ambient)
        } else { // Nothing or minor environmental challenge
            exploreMsg += " The wind howls, making progress difficult.";
            Message.displayOnScreen("The biting wind makes it hard to continue. You find little of interest.");
            character.changeSanity(-2);
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        // "Minérios e pedras preciosas."
        // "Água de degelo, mas precisa ser purificada."
        // "Refúgios naturais em cavernas." (Discovery, not a collectible resource type)
        return Arrays.asList("Iron Ore", "Rough Gemstones", "Clean Snow (for water)");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        // Mountain Goat, Eagle, potentially tougher wolves or bears at higher altitudes
        return Arrays.asList(Wolf.class); // Placeholder, add MountainGoat, Eagle etc.
    }

    @Override
    public String changeWeather() {
        String oldWeather = getCurrentWeather();
        int chance = random.nextInt(4);
        if (chance == 0) setCurrentWeather("Snow Storm");
        else if (chance == 1) setCurrentWeather("Dense Fog");
        else if (chance == 2) setCurrentWeather("Clear but Freezing");
        else setCurrentWeather("Cold and Windy");
        return "The weather on the " + getName() + " changed from " + oldWeather + " to " + getCurrentWeather() + ".";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        // "Nevasca repentina, reduzindo drasticamente a temperatura."
        // "Deslizamento de pedras, causando ferimentos."
        // "Descoberta de uma caverna segura."
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("SuddenBlizzard", 0.15);
        eventProbs.put("Rockslide", 0.10);
        eventProbs.put("FindSafeCave", 0.10); // Discovery event
        eventProbs.put("HighAltitudeSickness", 0.05);
        return eventProbs;
    }
}
