package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2; // <-- Import adicionado
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cannibal extends Creature {

    private final Vector2 position = new Vector2(); // <-- Igual ao Deer!

    protected Cannibal(
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

    public Cannibal() {
        super(
            "Canibal",
            "Um selvagem canibal que habita a selva",
            0.2f,
            0.7f,
            15.0f,
            Clime.CAVE,
            createDrops(),
            createSprites()
        );
    }
    public static boolean canSpawnIn(Ambient ambient) {
        return ambient.getName().equalsIgnoreCase("Cave");
    }


    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/canibal.png"))));
        return sprites;
    }

    private static Set<Item> createDrops() {
        // Aqui você poderia adicionar itens dropáveis no futuro.
        return new HashSet<>();
    }

    public Vector2 getPosition() { // <-- Agora igualzinho o Deer
        return position;
    }



    @Override
    public void attack() {
        System.out.println(getName() + " ataca com sua lança afiada!");
    }
}
