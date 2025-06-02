package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf; // Example
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

import java.util.*;

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
        } else if (outcome < 40) { // Encounter Creature (less frequent but potentially tougher)
            exploreMsg += " A shadow passes overhead... a Mountain Eagle circles!";
            Message.displayOnScreen("A large Mountain Eagle circles above, watching you!");
            exploreMsg += " Uma águia de montanha surge dos céus!";
            Message.displayOnScreen(TerminalStyler.warning("Uma enorme águia mergulha do alto em sua direção!"));

            Creature eagle = new Creature("Águia de Montanha", 50, 15, 20, Hostility.NEUTRAL) {
                @Override
                public void attack(Character target) {
                    float damage = getAttackDamage() * (random.nextFloat() * 0.3f + 0.7f);
                    target.changeHealth(-damage);
                    Message.displayOnScreen(TerminalStyler.warning(getName() + " ataca " + target.getName() + " com suas garras afiadas!"));
                    target.changeSanity(-3);
                    Message.displayOnScreen("O ataque súbito da águia te deixa atordoado! (-3 sanidade)");
                }

                @Override
                public void act(Character player) {
                    if (isAlive()) {
                        if (getHealth() < getMaxHealth() * 0.3) {
                            setHostility(Hostility.FLEEING);
                            Message.displayOnScreen(getName() + " parece ferida e tenta escapar!");
                        } else {
                            attack(player);
                        }
                    }
                }

                @Override
                public List<Item> dropLoot() {
                    List<Item> loot = new ArrayList<>();
                    if (random.nextFloat() < 0.6f) {
                        loot.add(new Food("Carne de Ave", "Carne magra de uma ave de rapina", 0.5f,
                            20f, FoodType.MEAT_RAW, 2, 0.1f, 0.3f));
                    }
                    if (random.nextFloat() < 0.7f) {
                        loot.add(new Material("Penas de Águia", "Penas grandes e resistentes", 0.1f, MaterialType.FIBER, 3));
                    }
                    return loot;
                }
            };

            Message.displayOnScreen("Uma " + eagle.getName() + " ataca das alturas!");
            eagle.displayStatus();

            eagle.attack(character);

            character.changeSanity(-2);        } else if (outcome < 60) { // Minor Discovery: Cave Entrance
            exploreMsg += " Discovered the entrance to a dark cave.";
            Message.displayOnScreen("You find the narrow entrance to a dark cave system.");
            exploreMsg += " Discovered the entrance to a dark cave.";
            Message.displayOnScreen("You find the narrow entrance to a dark cave system.");

            Message.displayOnScreen("\nDo you want to enter the cave? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.startsWith("y")) {
                Message.displayOnScreen(TerminalStyler.style("You venture into the darkness of the cave...", TerminalStyler.CYAN));

                character.changeEnergy(-10);

                Cave caveAmbient = new Cave();
                character.setCurrentAmbient(caveAmbient);

                Message.displayOnScreen(TerminalStyler.info("You are now in: " + caveAmbient.getName()));

                character.changeSanity(-5);
                Message.displayOnScreen("The oppressive darkness of the cave grates on your nerves. (-5 sanity)");

                exploreMsg += " You decided to explore the cave.";
            } else {
                Message.displayOnScreen("You decide not to enter the cave for now and continue exploring the mountain.");
            }
        } else { // Nothing or minor environmental challenge        } else { // Nothing or minor environmental challenge
            exploreMsg += " The wind howls, making progress difficult.";
            Message.displayOnScreen("The biting wind makes it hard to continue. You find little of interest.");
            character.changeSanity(-2);
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("Iron Ore", "Rough Gemstones", "Clean Snow (for water)");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
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
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("SuddenBlizzard", 0.15);
        eventProbs.put("Rockslide", 0.10);
        eventProbs.put("FindSafeCave", 0.10);
        eventProbs.put("HighAltitudeSickness", 0.05);
        return eventProbs;
    }
}
