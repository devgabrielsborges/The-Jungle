package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.controller.CraftController;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftingScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private Table table;
    private List<Recipe> availableRecipes;
    private Array<Item> inventory;

    public CraftingScreen(Array<Item> inventory) {
        this.inventory = inventory;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        loadAvailableRecipes();
        buildUI();
    }

    private void loadAvailableRecipes() {
        List<Item> itemList = new ArrayList<>();
        for (Item item : inventory) {
            if (item != null) itemList.add(item);
        }
        List<Item> filtered = new ArrayList<>();
        for (Item item : inventory) {
            if (item != null) filtered.add(item);
        }
        this.availableRecipes = CraftController.getAvailableRecipes(filtered);
    }

    private void buildUI() {
        table.clear();
        table.add(new Label("Receitas Disponíveis", skin)).colspan(2).padBottom(10);
        table.row();

        for (Recipe recipe : availableRecipes) {
            TextButton recipeButton = new TextButton(recipe.getResultName(), skin);
            recipeButton.addListener(event -> {
                if (event.toString().equals("touchDown")) {
                    showRecipeDetails(recipe);
                }
                return true;
            });
            table.add(recipeButton).pad(5).row();
        }

        TextButton backButton = new TextButton("Voltar", skin);
        backButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                // TODO: voltar para a tela anterior
            }
            return true;
        });
        table.add(backButton).colspan(2).padTop(20);
    }

    private void showRecipeDetails(Recipe recipe) {
        Dialog dialog = new Dialog("Detalhes da Receita", skin) {
            @Override
            protected void result(Object obj) {
                if ((Boolean) obj) {
                    List<Item> currentItems = new ArrayList<>();
                    for (Item i : inventory) if (i != null) currentItems.add(i);

                    boolean canCraft = recipe.matches(currentItems);
                    if (CraftController.canCraft(recipe.getResultName(), currentItems)) {
                        CraftController.consumeIngredients(recipe, inventory);
                        Item crafted = recipe.craft();
                        if (crafted != null) {
                            inventory.add(crafted);
                            loadAvailableRecipes();
                            buildUI();
                        }
                    } else {
                        Dialog warning = new Dialog("Erro", skin);
                        warning.text("Você não possui os itens necessários!");
                        warning.button("Ok");
                        warning.show(stage);
                    }

                }
            }
        };

        StringBuilder details = new StringBuilder("Materiais necessários:\n");
        for (Map.Entry<String, Integer> entry : recipe.getRequiredItems().entrySet()) {
            details.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        dialog.text(details.toString().trim());
        dialog.button("Craftar", true);
        dialog.button("Cancelar", false);
        dialog.show(stage);
    }


    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    private void insertItemInInventory(Array<Item> inventory, Item item) {
        for (int i = 0; i < inventory.size; i++) {
            Item slot = inventory.get(i);
            if (slot != null && slot.getName().equalsIgnoreCase(item.getName())) {
                slot.addQuantity(item.getQuantity());
                return;
            }
        }
        for (int i = 0; i < inventory.size; i++) {
            if (inventory.get(i) == null) {
                inventory.set(i, item);
                return;
            }
        }
        inventory.add(item);
    }

}
