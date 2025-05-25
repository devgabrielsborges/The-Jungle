// Arquivo: core/src/main/java/io/github/com/ranie_borges/thejungle/model/events/events/SurvivorRuinsEvent.java
package io.github.com.ranie_borges.thejungle.model.events.events;

import java.util.Random;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.NPC;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SurvivorRuinsEvent {
    private static final Random random = new Random();
    private static final float EVENT_PROBABILITY = 0.5f;

    public static void triggerEvent(Ruins ruins, SpriteBatch batch) {
        if (random.nextFloat() < EVENT_PROBABILITY) {
            NPC.talk();

            Texture survivorTexture = new Texture(Gdx.files.internal("sprites/npcs/npc3.png"));

            batch.begin();
            batch.draw(survivorTexture, 100, 100, 64, 64);
            batch.end();

            survivorTexture.dispose();
        }
    }
}
