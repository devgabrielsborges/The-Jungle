package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;

public class Hud {
    private final Texture sidebarTexture;
    private final Texture classIcon;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final Texture backpackTexture;

    public Hud(Texture sidebarTexture, Texture classIcon, BitmapFont font, Texture backpackTexture) {
        this.sidebarTexture = sidebarTexture;
        this.classIcon = classIcon;
        this.font = font;
        this.layout = new GlyphLayout();
        this.backpackTexture = new Texture(Gdx.files.internal("Gameplay/backpack.png"));
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Character character, GameState gameState, int width, int height) {
        float barX = 30;
        float baseY = height - 450.0f;
        float spacing = 60;
        float sidebarWidth = 300;

        batch.draw(sidebarTexture, 0, 0, sidebarWidth, height);
        batch.draw(sidebarTexture, width - sidebarWidth, 0, sidebarWidth, height);

        layout.setText(font, character.getName());
        font.draw(batch, character.getName(), sidebarWidth / 2f - layout.width / 2f, height - 380.0f);

        layout.setText(font, gameState.getCurrentAmbient().getName().toUpperCase());
        font.draw(batch, layout, width - sidebarWidth + (sidebarWidth - layout.width) / 2f, height - 20.0f);

        batch.draw(classIcon, 20, height - 360.0f, 260, 300);

        font.setColor(Color.WHITE);
        font.draw(batch, "Life", barX, baseY + 45);
        font.draw(batch, "Hunger", barX, baseY - spacing + 45);
        font.draw(batch, "Thirst", barX, baseY - spacing * 2 + 45);
        font.draw(batch, "Sanity", barX, baseY - spacing * 3 + 45);
        font.draw(batch, "Energy", barX, baseY - spacing * 4 + 45);
        font.draw(batch, "Days: " + gameState.getDaysSurvived(), width - sidebarWidth + 20, height - 60.0f);

        if (backpackTexture != null) {
            float backpackX = width - 250; // Ajuste para o canto direito com margem de 10px
            float backpackY = 30; // Margem inferior
            float backpackWidth = 200; // Largura ajustada
            float backpackHeight = 200; // Altura ajustada
            batch.draw(backpackTexture, backpackX, backpackY, backpackWidth, backpackHeight);

            String text = "Pressione 'I'";
            layout.setText(font, text);
            font.draw(batch, text, backpackX + (backpackWidth - layout.width) / 2, backpackY + 20);
        }
        batch.end();

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
