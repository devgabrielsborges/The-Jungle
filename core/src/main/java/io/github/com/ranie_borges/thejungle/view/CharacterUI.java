package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.Character;

/**
 * CharacterUI class for rendering character inventory and related UI elements
 */
public class CharacterUI {
    private final Texture background;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private Item selectedItem = null;

    /**
     * Constructor for CharacterUI
     *
     * @param background Background texture for UI elements
     * @param font       Font used for text rendering
     */
    public CharacterUI(Texture background, BitmapFont font) {
        this.background = background;
        this.font = font;
        this.layout = new GlyphLayout();
    }

    /**
     * Get the currently selected item in the inventory UI
     *
     * @return The selected item, or null if none selected
     */
    public Item getSelectedItem() {
        return selectedItem;
    }

    /**
     * Clear the current item selection
     */
    public void clearSelection() {
        selectedItem = null;
    }

    /**
     * Render the inventory screen
     *
     * @param batch     SpriteBatch for rendering
     * @param renderer  ShapeRenderer for drawing UI outlines
     * @param character The character whose inventory to display
     */
    public void renderInventory(SpriteBatch batch, ShapeRenderer renderer, Character character) {
        float w = 400;
        float h = 300;
        float x = (Gdx.graphics.getWidth() - w) / 2f;
        float y = (Gdx.graphics.getHeight() - h) / 2f;

        float slotSize = 48;
        float padding = 12;
        int cols = 5;
        int rows = 3;

        float gridW = cols * slotSize + (cols - 1) * padding;
        float gridH = rows * slotSize + (rows - 1) * padding;
        float startX = x + (w - gridW) / 2f;
        float startY = y + (h - gridH) / 2f;

        batch.begin();
        batch.draw(background, x, y, w, h);
        layout.setText(font, "Inventory");
        font.draw(batch, layout, x + (w - layout.width) / 2f, y + h - 20);
        batch.end();

        // Draw grid slots
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.LIGHT_GRAY);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - 1 - row) * (slotSize + padding);
                renderer.rect(sx, sy, slotSize, slotSize);
            }
        }
        renderer.end();

        boolean usedOne = false;

        batch.begin();
        for (int i = 0; i < character.getInventory().size && i < cols * rows; i++) {
            if (usedOne)
                break;

            Item item = character.getInventory().get(i);
            if (item != null) {
                int row = i / cols;
                int col = i % cols;

                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - 1 - row) * (slotSize + padding);

                // Icon
                Texture icon = item.getIconTexture();
                if (icon != null) {
                    batch.draw(icon, sx + 4, sy + 4, slotSize - 8, slotSize - 8);
                }

                layout.setText(font, "x" + item.getQuantity());
                font.draw(batch, layout, sx + slotSize - layout.width - 4, sy + 14);

                // Hover
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
                boolean isHovering = mouseX >= sx && mouseX <= sx + slotSize &&
                        mouseY >= sy && mouseY <= sy + slotSize;

                if (isHovering && "Medicinal".equalsIgnoreCase(item.getName())) {
                    float boxWidth = 160;
                    float boxHeight = 28;
                    float boxX = sx + (slotSize - boxWidth) / 2;
                    float boxY = sy + slotSize + 10;

                    batch.setColor(0, 0, 0, 0.7f);
                    batch.draw(background, boxX, boxY, boxWidth, boxHeight);
                    batch.setColor(1, 1, 1, 1);

                    layout.setText(font, "[E] Use medicinal plants");
                    font.draw(batch, layout, boxX + (boxWidth - layout.width) / 2, boxY + 18);

                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        character.useItem(item);
                        usedOne = true; // Mark as used
                    }
                }
                if (isHovering && "Berry".equalsIgnoreCase(item.getName())) {
                    float boxWidth = 140;
                    float boxHeight = 28;
                    float boxX = sx + (slotSize - boxWidth) / 2;
                    float boxY = sy + slotSize + 10;

                    batch.setColor(0, 0, 0, 0.7f);
                    batch.draw(background, boxX, boxY, boxWidth, boxHeight);
                    batch.setColor(1, 1, 1, 1);

                    layout.setText(font, "[E] Eat berry");
                    font.draw(batch, layout, boxX + (boxWidth - layout.width) / 2, boxY + 18);

                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        character.useItem(item);
                        usedOne = true;
                    }
                }
            }
        }

        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < character.getInventorySize(); i++) {
                // Calculate slot position
                float slotX = startX + (i % cols) * (slotSize + padding);
                float slotY = startY + (rows - 1 - (float) i / cols) * (slotSize + padding);

                // Check if click was in this slot
                if (mouseX >= slotX && mouseX <= slotX + slotSize &&
                        mouseY >= slotY && mouseY <= slotY + slotSize) {
                    // Set the selected item
                    selectedItem = character.getInventory().get(i);
                    break;
                }
            }
        }

        batch.end();
    }
}
