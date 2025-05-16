package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.Character;

public class InventoryUI {
    private Texture background;
    private BitmapFont font;
    private GlyphLayout layout;

    public InventoryUI(Texture background, BitmapFont font) {
        this.background = background;
        this.font = font;
        this.layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, ShapeRenderer renderer, Character character) {
        float w = 400, h = 300;
        float x = (Gdx.graphics.getWidth() - w) / 2f;
        float y = (Gdx.graphics.getHeight() - h) / 2f;
        float slotSize = 48, padding = 12;
        int cols = 5, rows = 3;

        batch.begin();
        batch.draw(background, x, y, w, h);
        layout.setText(font, "Inventory");
        font.draw(batch, layout, x + (w - layout.width) / 2f, y + h - 20);
        batch.end();

        float gridW = cols * slotSize + (cols - 1) * padding;
        float gridH = rows * slotSize + (rows - 1) * padding;
        float startX = x + (w - gridW) / 2f;
        float startY = y + (h - gridH) / 2f;

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.LIGHT_GRAY);

        for (int i = 0; i < cols * rows; i++) {
            float sx = startX + (i % cols) * (slotSize + padding);
            float sy = startY + ((rows - 1 - i / cols)) * (slotSize + padding);
            renderer.rect(sx, sy, slotSize, slotSize);
        }
        renderer.end();

        batch.begin();
        for (int i = 0; i < character.getInventory().size; i++) {
            Item item = character.getInventory().get(i);
            if (item != null) {
                float sx = startX + (i % cols) * (slotSize + padding);
                float sy = startY + ((rows - 1 - i / cols)) * (slotSize + padding);
                Texture icon = item.getIconTexture();
                if (icon != null) {
                    batch.draw(icon, sx + 8, sy + 8, slotSize - 16, slotSize - 16);
                }
                layout.setText(font, "x" + item.getQuantity());
                font.draw(batch, layout, sx + slotSize - layout.width - 4, sy + 16);
            }
        }
        batch.end();
    }
}
