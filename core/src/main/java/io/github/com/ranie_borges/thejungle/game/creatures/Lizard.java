package io.github.com.ranie_borges.thejungle.game.creatures;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.Creature;
import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.NonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;

import java.util.Map;
import java.util.Set;

public class Lizard extends Creature {

    protected Lizard(
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

    @Override
    public void attack() {

    }
}
