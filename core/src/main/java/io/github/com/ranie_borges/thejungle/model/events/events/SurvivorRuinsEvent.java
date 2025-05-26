package io.github.com.ranie_borges.thejungle.model.events.events;

import java.util.Random;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.NPC;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SurvivorRuinsEvent {
    private static final Random random = new Random();
    private static final float EVENT_PROBABILITY = 0.01f;

    public static void triggerEvent(Ruins ruins, SpriteBatch batch) {
        if (random.nextFloat() < EVENT_PROBABILITY) {
            NPC.talk();

            Texture survivorTexture = new Texture(Gdx.files.internal("sprites/npcs/npc3.png"));

            int spawnX = 50 + random.nextInt(201);
            int spawnY = 50 + random.nextInt(201);

            batch.begin();
            batch.draw(survivorTexture, spawnX, spawnY, 64, 64);
            batch.end();

            survivorTexture.dispose();
        }
    }
}
