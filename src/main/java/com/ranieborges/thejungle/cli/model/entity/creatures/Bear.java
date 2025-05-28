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

public class Bear extends Creature {

    private static final float BEAR_MAX_HEALTH = 150f;
    private static final float BEAR_ATTACK_DAMAGE = 30f;
    private static final float BEAR_SPEED = 40f;
    private boolean enraged = false;

    public Bear() {
        super("Bear", BEAR_MAX_HEALTH, BEAR_ATTACK_DAMAGE, BEAR_SPEED, Hostility.NEUTRAL);
    }

    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
        if (isAlive() && getHealth() < getMaxHealth() * 0.5 && !enraged) { // Enrage below 50% health
            System.out.println(getName() + " becomes enraged!");
            setAttackDamage(getAttackDamage() * 1.5f); // Increase attack
            setHostility(Hostility.HOSTILE);
            enraged = true;
        }
    }

    @Override
    public void attack(Character target) {
        if (!isAlive() || target == null || target.getHealth() <= 0) {
            return;
        }
        String attackMove = enraged ? "savagely mauls" : "swipes with its powerful claws at";
        System.out.println(getName() + " " + attackMove + " " + target.getName() + "!");
        float damageDealt = getAttackDamage() + (random.nextFloat() * 10 - 5f); // Larger damage variance
        target.changeHealth(-Math.max(0, damageDealt));
    }

    @Override
    public void act(Character player) {
        if (!isAlive()) return;

        if (getHostility() == Hostility.HOSTILE && player != null && player.getHealth() > 0) {
            attack(player);
        } else if (getHostility() == Hostility.NEUTRAL) {
            System.out.println(getName() + " observes " + player.getName() + " warily.");
        } else {
            System.out.println(getName() + " lumbers through the forest.");
        }
    }

    @Override
    public List<Item> dropLoot() {
        List<Item> loot = new ArrayList<>();
        if (!isAlive()) {
            loot.add(new Food("Large Raw Meat", "A hefty chunk of bear meat.", 4.0f, 50f, FoodType.MEAT_RAW, 3, 0.4f, 0.5f));
            if (random.nextFloat() < 0.75) {
                loot.add(new Material("Bear Hide", "Thick and tough hide.", 2.5f, MaterialType.LEATHER, 30));
            }
            if (random.nextFloat() < 0.5) {
                loot.add(new Material("Bear Claw", "A sharp bear claw.", 0.2f, MaterialType.BONE, 10));
            }
        }
        return loot;
    }
}
