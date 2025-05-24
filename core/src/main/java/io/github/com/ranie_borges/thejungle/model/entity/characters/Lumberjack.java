package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lumberjack extends Character {
    private static final Logger logger = LoggerFactory.getLogger(Lumberjack.class);

    public Lumberjack(String name, float xPosition, float yPosition) {
        super(
            name,
            120, // life (higher)
            100, // energy (higher)
            90,  // sanity
            10.0f, // attackDamage (good, but Hunter might be more precise)
            xPosition,
            yPosition
        );
        setInventoryInitialCapacity(8); // More space for wood/materials
    }

    @Override
    public float getWoodCuttingYieldModifier() {
        return 1.5f; // Gets 50% more wood from trees
    }

    // The "Mecânico" aspect (repairing, better crafting) from PDF could be:
    // @Override
    // public float getCraftingResourceCostModifier() { return 0.9f; } // 10% cheaper resource costs for crafting
    // @Override
    // public float getItemRepairEffectivenessModifier() { return 1.2f; } // 20% more effective repairs

    @Override
    public boolean attack(double attackDamage, Creature creature) {
        if (creature == null) return false;
        // Lumberjacks are strong
        double totalDamage = this.getAttackDamage() + attackDamage + 3.0; // Strength bonus
        logger.info("{} (Lumberjack) swings powerfully at {} for {} damage.", getName(), creature.getName(), totalDamage);
        float creatureLife = creature.getLifeRatio();
        creatureLife -= (float) totalDamage;
        creature.setLifeRatio(Math.max(0, creatureLife));
        return creatureLife <= 0;
    }

    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        // Lumberjacks might be less agile for avoiding fights
        boolean avoided = hasTraitLucky && Math.random() > 0.8; // Lower chance
        logger.info("{} (Lumberjack) {} the fight.", getName(), avoided ? "managed to avoid" : "faced");
        return avoided;
    }

    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        // Bonus for wood collection is handled in cutTree/cutTreeWithAxe
        // Generic collection for other items.
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
            logger.info("{} (Lumberjack) has a drinkable item.", getName());
        } else {
            logger.warn("{} (Lumberjack) needs a drink.", getName());
        }
    }
}
