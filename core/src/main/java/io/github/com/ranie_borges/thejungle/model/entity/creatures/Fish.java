package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Fish extends Creature {
    private Vector2 position = new Vector2();

    public Fish() {
        super(
            "Peixe",
            "Um peixe aquático que habita a água",
            0.5f,
            0.5f,
            1.0f,
            Clime.FOREST, // Ajuste se necessário
            createDrops(),
            createSprites()
        );
    }

    @Override
    public void attack() {
        System.out.println(getName() + " está atacando!");
    }

    // Atualizado para retornar um drop com o item "Fish Meat"
    private static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        drops.add(new Item("RawFish", 10, 15) {
            @Override
            public void useItem() {
                // Comportamento ao usar o item (restauração de vida ou fome)
            }
            @Override
            public void dropItem() {
                // Comportamento ao dropar o item
            }
        });
        return drops;
    }

    public static boolean canSpawnIn(Ambient ambient) {
        return ambient.getName().toLowerCase().contains("lake");
    }

    public static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/fish.png"))));
        return sprites;
    }

    public Vector2 getPosition() {
        return position;
    }
}
