package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Bird extends Creature {

    protected Bird(
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

    public Bird() {
        super(
            "Pássaro",
            "Um pássaro selvagem da floresta",
            0.3f,
            0.4f,
            5.0f,
            Clime.FOREST,
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("imagemAqui"))));
        return sprites;
    }

    private static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        drops.add(new Item("Carne Crua de Ave", "Carne de ave que pode ser consumida", 15) {
            @Override
            public void useItem() {

            }

            @Override
            public void dropItem() {

            }
        });
        drops.add(new Item("Pena", "Pena de pássaro que pode ser usada para crafting", 0) {
            @Override
            public void useItem() {

            }

            @Override
            public void dropItem() {

            }
        });
        return drops;
    }

    @Override
    public void attack() {
        System.out.println(getName() + " bica violentamente!");
    }
}
