package io.github.com.ranie_borges.thejungle.game.creatures;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.Creature;
import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.NonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;

import java.util.Map;
import java.util.Set;

public class Bunny extends Creature {

    protected Bunny(
        String name,
        String description,
        float probability,
        float lifeRatio,
        float damage,
        Clime climeSpawn,
        Set<Item> drops,
        Map<String, Sprite> sprites
    ) {
        super(name, description, probability, lifeRatio, damage, climeSpawn, drops, sprites);
    }

    /**
     * @param attackDamage The amount of damage to inflict
     * @param npc          The non-player character target
     * @return
     */
    @Override
    public boolean attack(double attackDamage, NonPlayerCharacter npc) {
        return false;
    }

    /**
     * @param hasTraitLucky Whether the character has the lucky trait
     * @return
     */
    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        return false;
    }

    /**
     * @param hasItemNear     Whether an item is available nearby
     * @param isInventoryFull Whether the character's inventory is full
     */
    @Override
    public void collectItem(boolean hasItemNear, boolean isInventoryFull) {

    }

    /**
     * @param hasDrinkableItem Whether the character has a drinkable item
     */
    @Override
    public void drink(boolean hasDrinkableItem) {

    }

    /**
     * @param item The item to use
     */
    @Override
    public void useItem(Item item) {

    }
}
