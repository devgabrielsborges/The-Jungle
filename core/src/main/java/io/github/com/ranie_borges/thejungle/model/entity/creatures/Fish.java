package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;

import java.util.Map;
import java.util.Set;
//TODO implement default attributes for Fish
//TODO implement default methods for Fish
public class Fish extends Creature {

    protected Fish(
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
