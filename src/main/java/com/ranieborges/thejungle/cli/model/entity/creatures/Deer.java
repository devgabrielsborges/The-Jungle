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

public class Deer extends Creature {

    private static final float DEER_MAX_HEALTH = 50f;
    private static final float DEER_ATTACK_DAMAGE = 0f;
    private static final float DEER_SPEED = 75f;

    public Deer() {
        super("Deer", DEER_MAX_HEALTH, DEER_ATTACK_DAMAGE, DEER_SPEED, Hostility.FLEEING);
    }

    @Override
    public void attack(Character target) {
        // Deer do not attack
        System.out.println(getName() + " is startled and tries to run away from " + target.getName() + "!");
    }

    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
        if (isAlive()) {
            setHostility(Hostility.FLEEING);
        }
    }

    @Override
    public void act(Character player) {
        if (!isAlive()) return;

        if (getHostility() == Hostility.FLEEING) {
            System.out.println(getName() + " spots " + (player != null ? player.getName() : "something") + " and darts away!");
        } else {
            System.out.println(getName() + " grazes peacefully.");
        }
    }

    @Override
    public List<Item> dropLoot() {
        List<Item> loot = new ArrayList<>();
        if (!isAlive()) {
            loot.add(new Food("Venison", "Raw deer meat.", 2.0f, 30f, FoodType.MEAT_RAW, 2, 0.3f, 0.4f));
            if (random.nextFloat() < 0.5) {
                loot.add(new Material("Deer Hide", "Supple deer hide.", 1.0f, MaterialType.LEATHER, 20));
            }
        }
        return loot;
    }
}
