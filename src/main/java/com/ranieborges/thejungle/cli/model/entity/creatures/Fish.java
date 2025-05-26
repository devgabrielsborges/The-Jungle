package com.ranieborges.thejungle.cli.model.entity.creatures;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FishType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Fish extends Creature {

    private static final float FISH_MAX_HEALTH = 10f; // Fish are generally fragile
    private static final float FISH_ATTACK_DAMAGE = 0f; // Fish don't typically attack
    private static final float FISH_SPEED = 30f; // Speed in water, may not be relevant for direct combat
    private final FishType fishType;

    public Fish(FishType type) {
        super(type.getDisplayName(), FISH_MAX_HEALTH, FISH_ATTACK_DAMAGE, FISH_SPEED, Hostility.PASSIVE);
        this.fishType = type;
        // Piranha could be an exception for hostility
        if (type == FishType.PIRANHA) {
            setHostility(Hostility.HOSTILE); // Piranhas might be aggressive
            setAttackDamage(5f); // Give Piranhas some bite
            setName("Piranha"); // Ensure name reflects the aggressive type
        }
    }

    public Fish() {
        this(FishType.SMALL_RIVER_FISH); // Default fish type
    }


    @Override
    public void attack(Character target) {
        if (this.fishType == FishType.PIRANHA && isAlive() && target != null && target.getHealth() > 0) {
            System.out.println(getName() + " nips aggressively at " + target.getName() + "!");
            float damageDealt = getAttackDamage() + (random.nextFloat() * 2 - 1f); // Small variance
            target.changeHealth(-Math.max(0, damageDealt));
        } else {
            System.out.println(getName() + " flails helplessly.");
        }
    }

    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
        // Fish don't typically change hostility when damaged, they just die or are caught.
    }

    @Override
    public void act(Character player) {
        if (!isAlive()) return;

        if (this.fishType == FishType.PIRANHA && getHostility() == Hostility.HOSTILE && player != null && player.getHealth() > 0) {
            // Simple AI for Piranha: if player is nearby (conceptual, as there's no distance here)
            System.out.println(getName() + " senses " + player.getName() + " and swims menacingly.");
            // attack(player); // Attack could be triggered by proximity or if player is in water
        } else {
            System.out.println(getName() + " swims around.");
        }
    }

    @Override
    public List<Item> dropLoot() {
        List<Item> loot = new ArrayList<>();
        if (!isAlive()) { // Should only drop loot if dead (caught/killed)
            float foodAmount;
            String foodName = "Raw Fish Meat";
            String foodDesc = "Freshly caught fish meat, best when cooked.";
            float foodWeight = 0.3f;

            switch (this.fishType) {
                case SALMON:
                    foodAmount = 25f;
                    foodName = "Raw Salmon";
                    foodWeight = 0.8f;
                    break;
                case TROUT:
                    foodAmount = 20f;
                    foodName = "Raw Trout";
                    foodWeight = 0.6f;
                    break;
                case CATFISH:
                    foodAmount = 15f;
                    foodName = "Raw Catfish";
                    foodWeight = 0.7f;
                    break;
                case PIRANHA:
                    foodAmount = 5f; // Piranhas are small
                    foodName = "Raw Piranha Meat";
                    foodWeight = 0.2f;
                    break;
                case SMALL_RIVER_FISH:
                default:
                    foodAmount = 10f;
                    foodWeight = 0.3f;
                    break;
            }
            // Food(String name, String description, float weight, float nutritionalValue, FoodType foodType,
            //      int turnsUntilSpoiled, float sicknessChanceOnSpoil, float sicknessChanceWhenRaw)
            loot.add(new Food(foodName, foodDesc, foodWeight, foodAmount, FoodType.MEAT_RAW, 1, 0.2f, 0.3f)); // Fish spoils quickly

            // Optionally, some fish might drop other minor items (e.g., scales for crafting)
            // if (random.nextFloat() < 0.1) {
            //     loot.add(new Material("Fish Scales", "Shiny fish scales.", 0.05f, Material.MaterialType.OTHER, 2));
            // }
        }
        return loot;
    }

}
