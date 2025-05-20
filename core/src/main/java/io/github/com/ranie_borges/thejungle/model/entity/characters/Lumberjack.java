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
            120,
            100,
            100,
            100,
            90,
            15.0f,
            "sprites/character/personagem_luta.png",
            xPosition,
            yPosition
        );
        setInventoryInitialCapacity(6);
    }

    @Override
    public void dropItem(Item item) {
        getInventory().removeValue(item, true);
        logger.info("{} dropped item: {}", getName(), item.getName());
    }

    @Override
    public boolean attack(double attackDamage, Creature creature) {
        if (creature == null) return false;
        double totalDamage = attackDamage + getAttackDamage();
        logger.info("{} swings axe at {} for {} damage.", getName(), creature.getName(), totalDamage);

        float creatureLife = creature.getLifeRatio();
        creatureLife -= (float) totalDamage;
        creature.setLifeRatio(Math.max(0, creatureLife));

        return creatureLife <= 0;
    }

    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        boolean avoided = hasTraitLucky && Math.random() > 0.5;
        logger.info("{} {} the fight.", getName(), avoided ? "avoided" : "couldn't avoid");
        return avoided;
    }

    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        if (nearbyItem != null && !isInventoryFull && getInventory().size < getInventoryInitialCapacity()) {
            getInventory().add(nearbyItem);
            logger.info("{} collected: {}", getName(), nearbyItem.getName());
        } else {
            logger.warn("{} couldn't collect the item.", getName());
        }
    }

    @Override
    public void drink(boolean hasDrinkableItem) {
        if (hasDrinkableItem) {
            setLife(Math.min(getLife() + 5, 120));
            logger.info("{} drank and recovered health. Current health: {}", getName(), getLife());
        } else {
            logger.warn("{} has nothing to drink.", getName());
        }
    }

    @Override
    public void useItem(Item item) {
        if (item != null && getInventory().contains(item, true)) {
            item.useItem();
            logger.info("{} used: {}", getName(), item.getName());
            getInventory().removeValue(item, true);
        } else {
            logger.warn("{} doesn't have the item: {}", getName(), item != null ? item.getName() : "null");
        }
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
