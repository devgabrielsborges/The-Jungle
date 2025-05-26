package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf; // Example, could be scavengers
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ruins extends Ambient {

    public Ruins() {
        super("Abandoned Ruins",
                "The crumbling remains of an ancient structure. It might hold valuable supplies or hidden dangers.",
                1.1f, // Moderate difficulty, potential for traps
                "Dusty and Still"); // "Baixo risco climático"
    }

    @Override
    public String explore(Character character) {
        String exploreMsg = character.getName() + " cautiously explores the dilapidated ruins.";
        Message.displayOnScreen(exploreMsg);
        character.changeEnergy(-(6 * getExplorationDifficulty()));

        int outcome = random.nextInt(100);

        if (outcome < 35) { // 35% Find Resource (higher chance for tools/processed goods)
            // "Ferramentas antigas e munição."
            // "Alimentos enlatados ainda comestíveis."
            // "Mapas e pistas sobre o ambiente ao redor."
            String[] possibleFinds = {"Old Tools", "Canned Food", "Tattered Map Fragment", "Scrap Metal"};
            String found = possibleFinds[random.nextInt(possibleFinds.length)];
            exploreMsg += " Found some " + found + ".";
            Message.displayOnScreen("Sifting through the debris, you find some " + found + "!");
            // TODO: Conceptual: Add specific item to inventory
            // if (found.equals("Canned Food")) character.getInventory().addItem(new Food("Canned Beans", "Preserved beans.", 0.5f, 40f, Food.FoodType.CANNED));
        } else if (outcome < 55) { // 20% Encounter Creature (scavengers, traps)
            exploreMsg += " A section of the floor creaks ominously... it might be a trap or something lurking below!";
            Message.displayOnScreen("You hear scurrying sounds from the shadows. Something might be living here.");
            // TODO: Instantiate creature (e.g., Giant Spider, Scavenger Human NPC) or trigger a trap event
            // Example: if (random.nextBoolean()) new GiantSpider(); else triggerTrap();
        } else if (outcome < 75) { // 20% Minor Discovery (hidden passage, inscription)
            exploreMsg += " Discovered a faded inscription on a wall.";
            Message.displayOnScreen("You find a barely legible inscription on a crumbling wall, hinting at the past.");
        } else { // 25% Nothing significant or structural instability
            exploreMsg += " The ruins are unstable, and a section collapses nearby!";
            Message.displayOnScreen("Dust and debris fall as a nearby section of the ruins collapses. You find nothing useful this time.");
            character.changeSanity(-3); // Minor stress
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("Scrap Metal", "Old Fabric", "Broken Tools", "Canned Goods (Rare)", "Ancient Artifacts (Very Rare)");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        // Could be rats, spiders, desperate survivors, or even automated defenses if sci-fi.
        return Arrays.asList(Wolf.class); // Placeholder, add GiantSpider, ScavengerNPC etc.
    }

    @Override
    public String changeWeather() {
        // "Baixo risco climático: Normalmente oferecem abrigo contra o clima."
        // So, weather changes might be less impactful or less frequent here.
        String oldWeather = getCurrentWeather();
        if (random.nextInt(5) == 0) { // Less frequent changes
            setCurrentWeather("Eerily Calm");
        } else {
            setCurrentWeather("Dusty and Still");
        }
        if (!oldWeather.equals(getCurrentWeather())) {
            return "The atmosphere in the " + getName() + " shifted from " + oldWeather + " to " + getCurrentWeather() + ".";
        }
        return "The air in the " + getName() + " remains " + getCurrentWeather() + ".";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        // "Encontrar um grupo de sobreviventes (podem ser aliados ou hostis)."
        // "Armadilhas deixadas por antigos ocupantes."
        // "Descoberta de uma passagem secreta para outra área."
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("EncounterSurvivorsNPC", 0.10);
        eventProbs.put("TriggerTrap", 0.15);
        eventProbs.put("FindSecretPassage", 0.08);
        eventProbs.put("StructuralCollapse", 0.05); // Minor collapse, not necessarily damaging
        return eventProbs;
    }
}
