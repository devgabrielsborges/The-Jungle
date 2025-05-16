package io.github.com.ranie_borges.thejungle.view.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;

public class Hud {
    private Texture sidebarTexture;
    private Texture classIcon;
    private BitmapFont font;
    private GlyphLayout layout;

    public Hud(Texture sidebarTexture, Texture classIcon, BitmapFont font) {
        this.sidebarTexture = sidebarTexture;
        this.classIcon = classIcon;
        this.font = font;
        this.layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Character character, GameState gameState, int width, int height) {
        float barX = 30;
        float baseY = height - 450;
        float spacing = 60;
        float SIDEBAR_WIDTH = 300;

        batch.draw(sidebarTexture, 0, 0, SIDEBAR_WIDTH, height);
        batch.draw(sidebarTexture, width - SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH, height);

        layout.setText(font, character.getName());
        font.draw(batch, character.getName(), SIDEBAR_WIDTH / 2f - layout.width / 2f, height - 380);

        layout.setText(font, gameState.getCurrentAmbient().getName().toUpperCase());
        font.draw(batch, layout, width - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - layout.width) / 2f, height - 20);

        batch.draw(classIcon, 20, height - 360, 260, 300);
        font.draw(batch, "Life", barX, baseY + 45);
        font.draw(batch, "Hunger", barX, baseY - spacing + 45);
        font.draw(batch, "Thirst", barX, baseY - spacing * 2 + 45);
        font.draw(batch, "Sanity", barX, baseY - spacing * 3 + 45);
        font.draw(batch, "Energy", barX, baseY - spacing * 4 + 45);
        font.draw(batch, "Days: " + gameState.getDaysSurvived(), width - SIDEBAR_WIDTH + 20, height - 60);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBar(shapeRenderer, Color.RED, character.getLife() / 100f, barX, baseY);
        drawBar(shapeRenderer, Color.ORANGE, character.getHunger() / 100f, barX, baseY - spacing);
        drawBar(shapeRenderer, Color.BLUE, character.getThirsty() / 100f, barX, baseY - spacing * 2);
        drawBar(shapeRenderer, Color.CYAN, character.getSanity() / 100f, barX, baseY - spacing * 3);
        drawBar(shapeRenderer, Color.YELLOW, character.getEnergy() / 100f, barX, baseY - spacing * 4);
        shapeRenderer.end();
    }

    private void drawBar(ShapeRenderer renderer, Color color, float percent, float x, float y) {
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(x, y, 240, 20);
        renderer.setColor(color);
        renderer.rect(x, y, 240 * Math.min(1, Math.max(0, percent)), 20);
    }
}
