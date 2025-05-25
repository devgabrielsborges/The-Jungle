package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Doctor extends Character {
    private static final Logger logger = LoggerFactory.getLogger(Doctor.class);

    public Doctor(String name, float xPosition, float yPosition) {
        super(
            name,
            80,  // life (less than survivor/lumberjack)
            60,  // energy
            100, // sanity (higher)
            6.0f, // attackDamage (lower)
            xPosition,
            yPosition
        );
        setInventoryInitialCapacity(10); // More space for medical supplies
    }

    @Override
    public float getHealingEffectivenessModifier() {
        return 1.30f; // 30% more effective healing from items
    }

    // "Pode tratar ferimentos sem necessidade de itens raros"
    // This could be implemented by:
    // 1. Giving Doctor specific, easy recipes for basic medicine.
    // 2. Allowing Doctor to use common items (like certain plants) as weak medicine.
    // For now, the getHealingEffectivenessModifier covers "better healing abilities".
    // The "sem itens raros" part is partially addressed if they can make basic healing items better.
    // A specific "treat wound" ability could be added later.

    @Override
    public boolean attack(double attackDamage, Creature creature) {
        if (creature == null) return false;
        double totalDamage = this.getAttackDamage() + attackDamage;
        logger.info("{} (Doctor) attacks {} with {} damage (less effective in combat).", getName(), creature.getName(), totalDamage);
        float creatureLife = creature.getLifeRatio();
        creatureLife -= (float) totalDamage;
        creature.setLifeRatio(Math.max(0, creatureLife));
        return creatureLife <= 0;
    }

    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        // Doctors might be less inclined or able to avoid fights through pure luck
        boolean avoided = hasTraitLucky && Math.random() > 0.7; // Lower chance
        logger.info("{} (Doctor) {} the fight.", getName(), avoided ? "avoided" : "couldn't avoid");
        return avoided;
    }

    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        if (nearbyItem != null && !isInventoryFull() && canCarryMore(nearbyItem.getWeight())) {
            // Doctors might have a higher chance of identifying and collecting medicinal plants
            // For now, generic collection.
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
            logger.info("{} (Doctor) has a drinkable item.", getName());
        } else {
            logger.warn("{} (Doctor) has nothing to drink.", getName());
        }
    }
    // useItem is inherited from Character.java and will use the modifiers
}
