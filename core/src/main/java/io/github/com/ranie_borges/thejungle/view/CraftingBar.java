package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.github.com.ranie_borges.thejungle.controller.CraftController;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Recipe;

import java.util.*;

public class CraftingBar {
    private final List<Recipe> recipes = Arrays.asList(
        new Recipe("Knife", Map.of("stick", 1, "rock", 1), Material::createKnife),
        new Recipe("Axe", Map.of("stick", 2, "rock", 3), Material::createAxe),
        new Recipe("Spear", Map.of("stick", 3, "rock", 1), Material::createSpear)
    );

    private final Map<String, Texture> icons = new HashMap<>();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    public CraftingBar() {
        font.getData().setScale(1.2f);
        for (Recipe r : recipes) {
            try {
                icons.put(r.getResultName().toLowerCase(), new Texture("icons/" + r.getResultName().toLowerCase() + ".png"));
            } catch (Exception e) {
                icons.put(r.getResultName().toLowerCase(), new Texture("icons/default.png"));
            }
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Character character, int screenWidth, int screenHeight) {
        float barY = 10;
        float slotSize = 64;
        float spacing = 16;
        float totalWidth = recipes.size() * (slotSize + spacing);
        float startX = (screenWidth - totalWidth) / 2f;

        int mouseX = Gdx.input.getX();
        int mouseY = screenHeight - Gdx.input.getY();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(startX - 20, barY - 20, totalWidth + 40, slotSize + 80);
        shapeRenderer.end();

        batch.begin();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            String name = recipe.getResultName();
            Texture icon = icons.get(name.toLowerCase());
            float x = startX + i * (slotSize + spacing);

            boolean canCraft = CraftController.canCraft(name.toLowerCase(), character.getInventory());
            batch.setColor(canCraft ? Color.WHITE : new Color(0.3f, 0.3f, 0.3f, 1f));
            batch.draw(icon, x, barY, slotSize, slotSize);
            batch.setColor(Color.WHITE);

            if (mouseX >= x && mouseX <= x + slotSize && mouseY >= barY && mouseY <= barY + slotSize) {

                layout.setText(font, name);
                font.draw(batch, name, x + (slotSize - layout.width) / 2f, barY + slotSize + 20);

                float detailY = barY + slotSize + 40;
                for (Map.Entry<String, Integer> entry : recipe.getRequiredItems().entrySet()) {
                    String req = entry.getKey() + ": " + entry.getValue();
                    layout.setText(font, req);
                    font.draw(batch, req, x + (slotSize - layout.width) / 2f, detailY);
                    detailY += 20;
                }

                if (Gdx.input.justTouched()) {
                    if (canCraft) {
                        Item crafted = recipe.craft();
                        if (crafted != null) {
                            CraftController.consumeIngredients(recipe, character.getInventory());
                            character.insertItemInInventory(crafted);
                        }
                    }
                }
            }
        }
        batch.end();
    }

    public void dispose() {
        for (Texture tex : icons.values()) tex.dispose();
        font.dispose();
    }
}
