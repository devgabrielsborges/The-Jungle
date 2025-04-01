package io.github.com.ranie_borges.thejungle.model.entity;

import io.github.com.ranie_borges.thejungle.model.entity.Item;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemTest {
    private TestItem item;

    @Before
    public void setUp() {
        item = new TestItem("Test Item", 1.5f, 75.0f);
    }

    @Test
    public void testInitialization() {
        assertEquals("Test Item", item.getName());
        assertEquals(1.5f, item.getWeight(), 0.001f);
        assertEquals(75.0f, item.getDurability(), 0.001f);
    }

    @Test
    public void testSetName() {
        item.setName("New Name");
        assertEquals("New Name", item.getName());

        // Test with empty name
        item.setName("");
        assertEquals("New Name", item.getName()); // Should not change
    }

    @Test
    public void testSetWeight() {
        item.setWeight(2.5f);
        assertEquals(2.5f, item.getWeight(), 0.001f);

        // Test with negative value
        item.setWeight(-1.0f);
        assertEquals(0.0f, item.getWeight(), 0.001f); // Should be clamped to 0
    }

    @Test
    public void testSetDurability() {
        item.setDurability(50.0f);
        assertEquals(50.0f, item.getDurability(), 0.001f);

        // Test with values outside the range
        item.setDurability(150.0f);
        assertEquals(100.0f, item.getDurability(), 0.001f); // Should be clamped to 100

        item.setDurability(-10.0f);
        assertEquals(0.0f, item.getDurability(), 0.001f); // Should be clamped to 0
    }

    @Test
    public void testItemUsage() {
        item.setDurability(100.0f);
        item.useItem();
        assertTrue(item.isUsed());
        assertEquals(90.0f, item.getDurability(), 0.001f);
    }

    private static class TestItem extends Item {
        private boolean isUsed = false;

        public TestItem(String name, float weight, float durability) {
            super(name, weight, durability);
        }

        @Override
        public void useItem() {
            isUsed = true;
            setDurability(getDurability() - 10.0f);
        }

        @Override
        public void dropItem() {
            // Implementation for testing
        }

        public boolean isUsed() {
            return isUsed;
        }
    }
}
