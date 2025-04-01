package io.github.com.ranie_borges.thejungle.model.stats.events.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreatureTest {
    private TestCreature creature;

    @Before
    public void setUp() {
        Set<Item> testDrops = new HashSet<>();
        testDrops.add(new TestItem("Item1"));

        Map<String, Sprite> testSprites = new HashMap<>();
        testSprites.put("default", new Sprite());

        creature = new TestCreature(
            "Test Creature",
            "A creature for testing",
            0.5f,
            10.0f,
            5.0f,
            Clime.JUNGLE,
            testDrops,
            testSprites
        );
    }

    @Test
    public void testCreatureInitialization() {
        assertEquals("Test Creature", creature.getName());
        assertEquals("A creature for testing", creature.getDescription());
        assertEquals(0.5f, creature.getProbability(), 0.001f);
        assertEquals(10.0f, creature.getLifeRatio(), 0.001f);
        assertEquals(5.0f, creature.getDamage(), 0.001f);
        assertEquals(Clime.JUNGLE, creature.getClimeSpawn());
        assertEquals(1, creature.getDrops().size());
        assertEquals(1, creature.getSprites().size());
    }

    @Test
    public void testCreatureSetters() {
        creature.setName("New Name");
        creature.setDescription("New Description");
        creature.setProbability(0.8f);
        creature.setLifeRatio(20.0f);
        creature.setDamage(10.0f);
        creature.setClimeSpawn(Clime.DESERT);

        assertEquals("New Name", creature.getName());
        assertEquals("New Description", creature.getDescription());
        assertEquals(0.8f, creature.getProbability(), 0.001f);
        assertEquals(20.0f, creature.getLifeRatio(), 0.001f);
        assertEquals(10.0f, creature.getDamage(), 0.001f);
        assertEquals(Clime.DESERT, creature.getClimeSpawn());
    }

    private static class TestCreature extends Creature {
        public TestCreature(String name, String description, float probability, float lifeRatio,
                            float damage, Clime climeSpawn, Set<Item> drops, Map<String, Sprite> sprites) {
            super(name, description, probability, lifeRatio, damage, climeSpawn, drops, sprites);
        }

        /**
         *
         */
        @Override
        public void attack() {

        }
    }

    private static class TestItem extends Item {
        public TestItem(String name) {
            super(name, 1.0f, 100.0f);
        }

        /**
         *
         */
        @Override
        public void useItem() {

        }

        /**
         *
         */
        @Override
        public void dropItem() {

        }
    }
}
