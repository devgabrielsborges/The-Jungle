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

public class InventoryUI {
    private final Texture background;
    private final BitmapFont font;
    private final GlyphLayout layout;

    public InventoryUI(Texture background, BitmapFont font) {
        this.background = background;
        this.font = font;
        this.layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, ShapeRenderer renderer, Character character) {
        float w = 400;
        float h = 300;
        float x = (Gdx.graphics.getWidth() - w) / 2f;
        float y = (Gdx.graphics.getHeight() - h) / 2f;

        // Improved slot sizing and grid layout
        float slotSize = 48;
        float padding = 12;
        int cols = 5;
        int rows = 3;

        // Calculate grid dimensions
        float gridW = cols * slotSize + (cols - 1) * padding;
        float gridH = rows * slotSize + (rows - 1) * padding;
        float startX = x + (w - gridW) / 2f;
        float startY = y + (h - gridH) / 2f;

        // Draw background and title
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
            if (usedOne) break;

            Item item = character.getInventory().get(i);
            if (item != null) {
                int row = i / cols;
                int col = i % cols;

                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - 1 - row) * (slotSize + padding);

                // Ícone
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
                        usedOne = true; // ✅ marca que usou
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

                    layout.setText(font, "[E] Comer amora");
                    font.draw(batch, layout, boxX + (boxWidth - layout.width) / 2, boxY + 18);

                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        character.useItem(item);
                        usedOne = true;
                    }
                }
            }
        }
        batch.end();


    }
}
