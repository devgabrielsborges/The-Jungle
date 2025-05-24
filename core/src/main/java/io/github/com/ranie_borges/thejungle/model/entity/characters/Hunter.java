package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hunter extends Character {
    private static final Logger logger = LoggerFactory.getLogger(Hunter.class);

    public Hunter(String name, float xPosition, float yPosition) {
        super(
            name,
            100, // life
            90,  // energy
            95,  // sanity
            12.0f, // attackDamage (higher)
            xPosition,
            yPosition
        );
        setInventoryInitialCapacity(5); // Less inventory, focused on hunt
    }

    @Override
    public int getFoodWaterGatheringBonusQuantity() {
        // Hunters are better at finding food/water.
        // This could be a chance-based bonus or a flat bonus.
        // For simplicity, let's give a flat +1 to quantity for items identified as Food
        // (like berries) or items that directly provide water.
        // This is applied in Character.tryCollectNearbyMaterial
        return 1; // Gets 1 extra unit of qualifying food/water items.
    }

    @Override
    public boolean attack(double attackDamage, Creature creature) {
        if (creature == null) return false;
        // Hunters are skilled fighters
        double totalDamage = this.getAttackDamage() + attackDamage + 2.0; // Small flat bonus for Hunter's skill
        logger.info("{} (Hunter) strikes {} with precision for {} damage.", getName(), creature.getName(), totalDamage);
        float creatureLife = creature.getLifeRatio();
        creatureLife -= (float) totalDamage;
        creature.setLifeRatio(Math.max(0, creatureLife));
        return creatureLife <= 0;
    }

    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        // Hunters might be better at stealth or assessing situations
        boolean avoided = hasTraitLucky && Math.random() > 0.3; // Higher chance
        logger.info("{} (Hunter) {} the fight.", getName(), avoided ? "skillfully avoided" : "engaged in");
        return avoided;
    }

    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        // The bonus resource logic is handled in the base Character.tryCollectNearbyMaterial
        // by checking getFoodWaterGatheringBonusQuantity().
        // Standard collection call here.
        if (nearbyItem != null && !isInventoryFull() && canCarryMore(nearbyItem.getWeight())) {
            insertItemInInventory(nearbyItem); // Base method will handle quantity via bonus
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
            logger.info("{} (Hunter) has a source of hydration.", getName());
        } else {
            logger.warn("{} (Hunter) is out of drinkable items.", getName());
        }
    }
    // useItem is inherited from Character.java
}
