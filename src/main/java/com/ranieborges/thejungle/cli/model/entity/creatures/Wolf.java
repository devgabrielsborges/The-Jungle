package com.ranieborges.thejungle.cli.model.entity.creatures;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;

import java.util.ArrayList;
import java.util.List;

public class Wolf extends Creature {

    public Wolf() {
        super("Wolf", 70f, 15f, 60f, Hostility.HOSTILE);
    }

    @Override
    public void attack(Character target) {
        if (!isAlive() || target == null || target.getHealth() <= 0) {
            return;
        }
        System.out.println(getName() + " lunges and bites " + target.getName() + "!");
        float damageDealt = getAttackDamage() + (random.nextFloat() * 5 - 2.5f);
        target.changeHealth(-Math.max(0, damageDealt));
    }

    @Override
    public void act(Character player) {
        if (!isAlive()) return;

        if (getHostility() == Hostility.HOSTILE && player != null && player.getHealth() > 0) {
            System.out.println(getName() + " growls menacingly at " + player.getName() + ".");
            attack(player);
        } else {
            System.out.println(getName() + " prowls the area.");
        }
    }

    @Override
    public List<Item> dropLoot() {
        List<Item> loot = new ArrayList<>();
        if (!isAlive()) {
            if (random.nextFloat() < 0.6) {
                loot.add(new Food("Raw Wolf Meat", "Meat from a wolf, needs cooking.", 1.5f, 20f, FoodType.MEAT_RAW, 2, 0.5f, 0.6f));
            }
            if (random.nextFloat() < 0.4) {
                loot.add(new Material("Wolf Pelt", "The fur of a wolf.", 0.8f, MaterialType.LEATHER, 15));
            }
        }
        return loot;
    }
}
