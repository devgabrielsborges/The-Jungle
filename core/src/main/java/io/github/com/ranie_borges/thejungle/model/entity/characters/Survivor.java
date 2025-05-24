package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Survivor extends Character {
    private static final Logger logger = LoggerFactory.getLogger(Survivor.class);

    public Survivor(String name, float xPosition, float yPosition) {
        super(
            name,
            100, // life
            80,  // energy
            85,  // sanity
            8.0f, // attackDamage
            xPosition,
            yPosition
        );
        setInventoryInitialCapacity(6); // Example capacity
        logger.info("Survivor {} created. HungerMod: {}, ThirstMod: {}", name, getHungerDepletionModifier(), getThirstDepletionModifier());
    }

    /**
     * Sobrevivente Nato: Menos impactado por fome e sede.
     * Returns a modifier for hunger depletion rate. 0.8f means 20% slower depletion.
     */
    @Override
    public float getHungerDepletionModifier() {
        return 0.80f; // 20% slower hunger depletion
    }

    /**
     * Sobrevivente Nato: Menos impactado por fome e sede.
     * Returns a modifier for thirst depletion rate. 0.8f means 20% slower depletion.
     */
    @Override
    public float getThirstDepletionModifier() {
        return 0.80f; // 20% slower thirst depletion
    }

    @Override
    public boolean attack(double attackDamage, Creature creature) {
        if (creature == null) return false;
        double totalDamage = this.getAttackDamage() + attackDamage;
        logger.info("{} attacks {} causing {} damage.", getName(), creature.getName(), totalDamage);

        float creatureLife = creature.getLifeRatio();
        creatureLife -= (float) totalDamage;
        creature.setLifeRatio(Math.max(0, creatureLife));

        return creatureLife <= 0;
    }

    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        boolean avoided = hasTraitLucky && Math.random() > 0.4;
        logger.info("{} {} the fight.", getName(), avoided ? "avoided" : "couldn't avoid");
        return avoided;
    }

    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        if (nearbyItem != null && !isInventoryFull() && canCarryMore(nearbyItem.getWeight())) {
            insertItemInInventory(nearbyItem);
            logger.info("{} collected: {}", getName(), nearbyItem.getName());
        } else {
            if(isInventoryFull()) logger.warn("{} couldn't collect {}: inventory full.", getName(), nearbyItem != null ? nearbyItem.getName() : "item");
            else if(nearbyItem != null && !canCarryMore(nearbyItem.getWeight())) logger.warn("{} couldn't collect {}: too heavy.", getName(), nearbyItem.getName());
            else logger.warn("{} couldn't collect item.", getName());
        }
    }

    @Override
    public void drink(boolean hasDrinkableItem) {
        if (hasDrinkableItem) {
            logger.info("{} has a drinkable item. Use it from inventory to quench thirst.", getName());
        } else {
            logger.warn("{} has nothing to drink.", getName());
        }
    }

    // useItem is inherited from Character.java and will use the modifiers.
    // render and dispose are inherited from Character.java.
}
