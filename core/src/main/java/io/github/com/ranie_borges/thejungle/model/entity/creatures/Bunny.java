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

    public Bunny() {
        super(
            "Coelho",
            "Um coelho selvagem que vive na floresta",
            0.5f,
            0.2f,
            2.0f,
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
        drops.add(new Item("Carne Crua de Coelho", "Carne de coelho que pode ser consumida", 10) {
            @Override
            public void useItem() {

            }

            @Override
            public void dropItem() {

            }
        });
        drops.add(new Item("Pele de Coelho", "Pele macia que pode ser usada para crafting", 0) {
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
        System.out.println(getName() + " ataca com suas patas traseiras!");
    }
}
