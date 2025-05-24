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
import io.github.com.ranie_borges.thejungle.model.entity.itens.Recipe;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Tool;   // Import Tool
import io.github.com.ranie_borges.thejungle.model.entity.itens.Weapon; // Import Weapon

import java.util.*;

public class CraftingBar {
    private final List<Recipe> recipes = Arrays.asList(
        new Recipe("Knife", Map.of("stick", 1, "rock", 1), Tool::createKnife), // Changed from Material::createKnife
        new Recipe("Axe", Map.of("stick", 2, "rock", 3), Tool::createAxe),     // Changed from Material::createAxe
        new Recipe("Spear", Map.of("stick", 3, "rock", 1), Weapon::createWoodenSpear) // Changed from Material::createSpear
    );

    private final Map<String, Texture> icons = new HashMap<>();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    public CraftingBar() {
        font.getData().setScale(1.2f);
        for (Recipe r : recipes) {
            String iconName = r.getResultName().toLowerCase().replace(" ", "_"); // Handle spaces in names for consistency
            try {
                icons.put(r.getResultName().toLowerCase(), new Texture(Gdx.files.internal("icons/" + iconName + ".png")));
            } catch (Exception e) {
                System.err.println("Warning: Could not load icon for " + r.getResultName() + " at icons/" + iconName + ".png. Using default.");
                try {
                    icons.put(r.getResultName().toLowerCase(), new Texture(Gdx.files.internal("icons/default.png")));
                } catch (Exception e2) {
                    System.err.println("Error: Could not load default icon icons/default.png.");
                }
            }
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Character character, int screenWidth, int screenHeight) {
        float barY = 10;
        float slotSize = 64;
        float spacing = 16;
        float totalWidth = recipes.size() * (slotSize + spacing) - spacing; // Adjust total width to not include last spacing
        float startX = (screenWidth - totalWidth) / 2f;

        int mouseX = Gdx.input.getX();
        int mouseY = screenHeight - Gdx.input.getY();

        if (shapeRenderer != null) { // Null check for safety
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.DARK_GRAY); // Or any other color you prefer
            float barBackgroundHeight = slotSize + 80; // Height for icons + text
            shapeRenderer.rect(startX - spacing, barY - spacing, totalWidth + (spacing*2) , barBackgroundHeight);
            shapeRenderer.end();
        }


        batch.begin();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            String name = recipe.getResultName();
            Texture icon = icons.get(name.toLowerCase());
            float x = startX + i * (slotSize + spacing);

            boolean canCraft = CraftController.canCraft(name, character.getInventory()); // Pass GDX Array

            if (icon != null) {
                batch.setColor(canCraft ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 0.7f)); // Dim if cannot craft
                batch.draw(icon, x, barY + ( (slotSize + 80 - spacing*2) - slotSize ) / 2 , slotSize, slotSize); // Centered icon a bit higher
            } else {
                font.setColor(Color.LIGHT_GRAY);
                layout.setText(font, name.substring(0, Math.min(name.length(), 3)));
                font.draw(batch, layout, x + (slotSize - layout.width)/2, barY + slotSize/2 + layout.height/2);
            }
            batch.setColor(Color.WHITE); // Reset color

            // Tooltip / Interaction logic
            if (mouseX >= x && mouseX <= x + slotSize && mouseY >= barY && mouseY <= barY + slotSize + 40) { // Adjusted hover area
                font.setColor(Color.YELLOW);
                layout.setText(font, name);
                font.draw(batch, layout, x + (slotSize - layout.width) / 2f, barY + slotSize + 25); // Tooltip name

                float detailY = barY + slotSize + 10;
                font.setColor(Color.WHITE);
                for (Map.Entry<String, Integer> entry : recipe.getRequiredItems().entrySet()) {
                    String req = entry.getKey() + ": " + entry.getValue();
                    layout.setText(font, req);
                    font.draw(batch, req, x + (slotSize - layout.width) / 2f -15 , detailY); // Align left of center
                    detailY -= 15; // Move text downwards
                }

                if (Gdx.input.justTouched()) {
                    if (canCraft) {
                        Item craftedItem = CraftController.craft(name, character.getInventory()); // This consumes ingredients
                        if (craftedItem != null) {
                            character.insertItemInInventory(craftedItem);
                        } else {
                            Gdx.app.log("INFO","Crafting " + name + " failed even though canCraft was true.");
                        }
                    } else {
                        Gdx.app.log("INFO","Cannot craft " + name + ". Missing ingredients.");
                    }
                }
            }
        }
        batch.end();
        font.setColor(Color.WHITE); // Reset font color outside loop
    }

    public void dispose() {
        for (Texture tex : icons.values()) {
            if (tex != null) tex.dispose();
        }
        icons.clear();
        if (font != null) font.dispose();
    }
}
