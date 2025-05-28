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

    private final FishType fishType;

    public Fish(FishType type) {
        super(type.getDisplayName(), 10f, 2f, 30f, Hostility.PASSIVE);
        this.fishType = type;
        if (type == FishType.PIRANHA) {
            setHostility(Hostility.HOSTILE);
            setAttackDamage(5f);
            setName("Piranha");
        }
    }

    public Fish() {
        this(FishType.SMALL_RIVER_FISH);
    }


    @Override
    public void attack(Character target) {
        if (this.fishType == FishType.PIRANHA && isAlive() && target != null && target.getHealth() > 0) {
            System.out.println(getName() + " nips aggressively at " + target.getName() + "!");
            float damageDealt = getAttackDamage() + (random.nextFloat() * 2 - 1f);
            target.changeHealth(-Math.max(0, damageDealt));
        } else {
            System.out.println(getName() + " flails helplessly.");
        }
    }

    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
    }

    @Override
    public void act(Character player) {
        if (!isAlive()) return;

        if (this.fishType == FishType.PIRANHA && getHostility() == Hostility.HOSTILE && player != null && player.getHealth() > 0) {
            System.out.println(getName() + " senses " + player.getName() + " and swims menacingly.");
        } else {
            System.out.println(getName() + " swims around.");
        }
    }

    @Override
    public List<Item> dropLoot() {
        List<Item> loot = new ArrayList<>();
        if (!isAlive()) {
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
                    foodAmount = 5f;
                    foodName = "Raw Piranha Meat";
                    foodWeight = 0.2f;
                    break;
                case SMALL_RIVER_FISH:
                default:
                    foodAmount = 10f;
                    foodWeight = 0.3f;
                    break;
            }

            loot.add(new Food(foodName, foodDesc, foodWeight, foodAmount, FoodType.MEAT_RAW, 1, 0.2f, 0.3f)); // Fish spoils quickly

        }
        return loot;
    }

}
