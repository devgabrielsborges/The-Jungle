package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;

import java.util.*;

public class Cave extends Ambient {

    public Cave() {
        super("Dark Cave",
                "A damp and dark cave system. The air is cold, and strange sounds echo from the depths.",
                1.3f,
                "Cold and Damp"); // "Pouca luz"
    }

    @Override
    public String explore(Character character) {
        String exploreMsg = character.getName() + " ventures deeper into the dark cave.";
        Message.displayOnScreen(exploreMsg);
        boolean hasLightSource = character.getInventory().getItems().stream()
            .anyMatch(item -> item.getName().toLowerCase().contains("torch") ||
                item.getName().toLowerCase().contains("lantern") ||
                item.getName().toLowerCase().contains("light"));        if (!hasLightSource) {
            Message.displayOnScreen("It's pitch black! Exploring without a light source is dangerous.");
            character.changeEnergy(-(10 * getExplorationDifficulty()));
            character.changeSanity(-5);
            exploreMsg += " It was very dark, increasing stress.";
        } else {
            character.changeEnergy(-(7 * getExplorationDifficulty()));
        }


        int outcome = random.nextInt(100);
        if (outcome < 30) { // Find Resource
            String[] possibleFinds = {"Rare Minerals", "Cave Fungi (Edible?)", "Ancient Bones", "Underground Spring Water"};
            String found = possibleFinds[random.nextInt(possibleFinds.length)];
            exploreMsg += " Found " + found + ".";
            Message.displayOnScreen("In the dim light, you discover some " + found + "!");
            switch (found) {
                case "Rare Minerals" -> {
                    int mineralAmount = random.nextInt(2) + 1; // 1-2 unidades
                    for (int i = 0; i < mineralAmount; i++) {
                        Material minerals = new Material("Rare Minerals", "Valuable minerals found deep in the cave", 0.8f, MaterialType.METAL_ORE, 5);
                        character.getInventory().addItem(minerals);
                    }
                    Message.displayOnScreen("You collect " + mineralAmount + " piece" + (mineralAmount > 1 ? "s" : "") + " of rare minerals.");
                }
                case "Cave Fungi (Edible?)" -> {
                    int fungiAmount = random.nextInt(3) + 1; // 1-3 unidades
                    float toxicityChance = 0.3f; // 30% de chance de ser tóxico
                    for (int i = 0; i < fungiAmount; i++) {
                        Food fungi = new Food("Cave Mushroom", "A strange glowing fungus from the cave depths", 0.2f,
                            15.0f, FoodType.MUSHROOM, 5, 0.0f, toxicityChance);
                        character.getInventory().addItem(fungi);
                    }
                    Message.displayOnScreen("You collect " + fungiAmount + " strange mushroom" + (fungiAmount > 1 ? "s" : "") + " that might be edible.");
                }
                case "Ancient Bones" -> {
                    int boneAmount = random.nextInt(2) + 1; // 1-2 unidades
                    for (int i = 0; i < boneAmount; i++) {
                        Material bones = new Material("Ancient Bones", "Old bones found in the cave, could be useful for crafting", 0.5f, MaterialType.BONE, 3);
                        character.getInventory().addItem(bones);
                    }
                    Message.displayOnScreen("You collect " + boneAmount + " ancient bone" + (boneAmount > 1 ? "s" : "") + ".");
                }
                case "Underground Spring Water" -> {
                    boolean isPure = random.nextFloat() > 0.4; // 60% de chance de ser pura
                    Food water;
                    if (isPure) {
                        water = new Food("Fresh Cave Water", "Clean water from an underground spring", 1.0f, 5.0f, FoodType.OTHER, 0.0f);
                        Message.displayOnScreen("The water looks clean and drinkable!");
                    } else {
                        water = new Food("Murky Cave Water", "Suspicious looking water from an underground pool", 1.0f,
                            3.0f, FoodType.OTHER, 10, 0.7f, 0.0f);
                        Message.displayOnScreen(TerminalStyler.warning("The water looks murky and might be contaminated."));
                    }
                    character.getInventory().addItem(water);
                }
                default -> throw new IllegalStateException("Unexpected value: " + found);
            }
        } else if (outcome < 60) { // Encounter Creature
            exploreMsg += " A chilling sound echoes from a nearby passage... a Cave Creature!";
            Message.displayOnScreen("You are not alone in these depths! A creature attacks!");
            String[] possibleCreatures = {"Cave Spider", "Giant Bat", "Blind Worm", "Cave Troll"};
            String creatureType = possibleCreatures[random.nextInt(possibleCreatures.length)];

            Creature caveCreature;
            switch (creatureType) {
                case "Cave Spider":
                    caveCreature = new Creature(creatureType, 30, 8, 15, Hostility.HOSTILE) {
                        @Override
                        public void attack(Character target) {
                            float damage = getAttackDamage() * (random.nextFloat() * 0.3f + 0.8f);
                            target.changeHealth(-damage);
                            Message.displayOnScreen(TerminalStyler.warning(getName() + " bites " + target.getName() + " with venomous fangs!"));
                            if (random.nextFloat() < 0.3f) { // 30% chance of poison
                                target.changeHealth(-5);
                                Message.displayOnScreen(TerminalStyler.error("Você sente veneno correndo nas suas veias! (-5 saúde)"));
                            }
                        }

                        @Override
                        public void act(Character player) {
                            if (isAlive()) {
                                attack(player);
                            }
                        }

                        @Override
                        public List<Item> dropLoot() {
                            List<Item> loot = new ArrayList<>();
                            if (random.nextFloat() < 0.7f) {
                                loot.add(new Material("Spider Silk", "Strong, sticky threads from a cave spider", 0.1f, MaterialType.FIBER, 2));
                            }
                            return loot;
                        }
                    };
                    break;
                case "Giant Bat":
                    caveCreature = new Creature(creatureType, 25, 6, 20, Hostility.NEUTRAL) {
                        @Override
                        public void attack(Character target) {
                            float damage = getAttackDamage() * (random.nextFloat() * 0.4f + 0.8f);
                            target.changeHealth(-damage);
                            Message.displayOnScreen(TerminalStyler.warning(getName() + " swoops down and claws at " + target.getName() + "!"));
                            target.changeSanity(-3);
                            Message.displayOnScreen("O guincho do morcego te desorientou! (-3 sanidade)");
                        }

                        @Override
                        public void act(Character player) {
                            if (isAlive()) {
                                attack(player);
                            }
                        }

                        @Override
                        public List<Item> dropLoot() {
                            List<Item> loot = new ArrayList<>();
                            if (random.nextFloat() < 0.5f) {
                                loot.add(new Material("Bat Wing", "Leathery wing from a giant bat", 0.3f, MaterialType.LEATHER, 1));
                            }
                            return loot;
                        }
                    };
                    break;
                default: // Cave Troll or Blind Worm
                    caveCreature = new Creature(creatureType, 50, 12, 8, Hostility.HOSTILE) {
                        @Override
                        public void attack(Character target) {
                            float damage = getAttackDamage() * (random.nextFloat() * 0.5f + 0.8f);
                            target.changeHealth(-damage);
                            Message.displayOnScreen(TerminalStyler.warning(getName() + " ataca " + target.getName() + " com força brutal!"));
                        }

                        @Override
                        public void act(Character player) {
                            if (isAlive()) {
                                attack(player);
                            }
                        }

                        @Override
                        public List<Item> dropLoot() {
                            List<Item> loot = new ArrayList<>();
                            if (random.nextFloat() < 0.6f) {
                                loot.add(new Material("Creature Remains", "Remains from a cave creature", 0.5f, MaterialType.BONE, 3));
                            }
                            return loot;
                        }
                    };
            }

            Message.displayOnScreen("Um " + caveCreature.getName() + " aparece na escuridão!");
            caveCreature.displayStatus();

            caveCreature.attack(character);

            character.changeSanity(-3);        } else if (outcome < 80) { // Minor Discovery: Hidden Tunnel or Chamber
            exploreMsg += " Discovered a narrow passage leading to an unexplored chamber.";
            Message.displayOnScreen("You find a narrow passage that seems to lead deeper into the cave.");
        } else if (outcome < 80) { // Minor Discovery: Hidden Tunnel or Chamber
            exploreMsg += " Discovered a narrow passage leading to an unexplored chamber.";
            Message.displayOnScreen("You find a narrow passage that seems to lead deeper into the cave.");

            Message.displayOnScreen("\nDo you want to explore this passage? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.startsWith("y")) {
                Message.displayOnScreen(TerminalStyler.style("You squeeze through the narrow passage...", TerminalStyler.CYAN));

                character.changeEnergy(-5);

                int secretFind = random.nextInt(100);

                if (secretFind < 30) {
                    exploreMsg += " Found a hidden treasure in the secret chamber!";
                    Message.displayOnScreen(TerminalStyler.success("You discover an ancient cache hidden away from prying eyes!"));

                    if (random.nextFloat() > 0.7f) { // 30% chance de item raro
                        Material rareGem = new Material("Luminous Crystal", "A rare gem that glows with inner light", 0.3f, MaterialType.STONE, 8);
                        character.getInventory().addItem(rareGem);
                        Message.displayOnScreen("You found a " + TerminalStyler.style("Luminous Crystal", TerminalStyler.BRIGHT_CYAN) + "!");
                    } else {
                        Material oldRelic = new Material("Ancient Artifact", "A small statue made by previous inhabitants", 0.5f, MaterialType.STONE, 5);
                        character.getInventory().addItem(oldRelic);
                        Message.displayOnScreen("You found an " + TerminalStyler.style("Ancient Artifact", TerminalStyler.YELLOW) + "!");
                    }

                } else {
                    exploreMsg += " Found an untouched underground spring.";
                    Message.displayOnScreen(TerminalStyler.success("The chamber opens to reveal a small, pristine underground spring!"));

                    Food purifiedWater = new Food("Crystal Clear Water", "Pure water from an untouched underground spring", 0.5f,
                        20, FoodType.OTHER, Integer.MAX_VALUE, 0.0f, 0.0f);
                    character.getInventory().addItem(purifiedWater);

                    character.changeSanity(5);
                    Message.displayOnScreen("The serene beauty of the underground spring restores your composure. (+5 sanity)");
                }
            } else {
                Message.displayOnScreen("You decide not to risk the narrow passage and continue exploring the main cave.");
            }
        } else {
            exploreMsg += " A section of the cave ceiling looks unstable.";
            Message.displayOnScreen("Loose rocks clatter nearby. The cave feels oppressive.");
            character.changeSanity(-2);
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("Iron Ore", "Coal", "Cave Mushrooms", "Guano", "Crystal Shards");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        // "Presença de criaturas desconhecidas"
        // return Arrays.asList(GiantBat.class, CaveSpider.class);
        return List.of(); // Placeholder
    }

    @Override
    public String changeWeather() {
        // Weather inside a cave is generally stable.
        setCurrentWeather("Cold and Damp"); // Stays consistent
        return "The atmosphere inside the " + getName() + " remains " + getCurrentWeather() + ".";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        // "Encontro com uma criatura hostil."
        // "Descoberta de um túnel oculto."
        // "Desmoronamento parcial, bloqueando saídas."
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("CaveCreatureEncounter", 0.20);
        eventProbs.put("FindHiddenTunnel", 0.15);
        eventProbs.put("MinorCaveIn", 0.10); // Could block path or cause minor damage/stress
        eventProbs.put("LoseWay", 0.05); // Disorientation, sanity loss
        return eventProbs;
    }
}
