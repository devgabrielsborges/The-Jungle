package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.com.ranie_borges.thejungle.core.Main;

public class BattleScreen implements Screen {
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;
    private final Texture background;
    private final Texture playerSprite;
    private final Texture enemySprite;
    private final BitmapFont font;
    private final Main game;
    private final Screen previousScreen;
    private int playerHealth = 100;
    private int enemyHealth = 100;
    private String battleMessage = "O que você quer fazer?";
    private int selectedAction = 0;
    private final String[] actions = {"Atacar", "Usar Item", "Fugir"};

    public BattleScreen(Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        background = new Texture("Gameplay/battle_background.png");
        playerSprite = new Texture("sprites/character/personagem_luta.png");
        enemySprite = new Texture("sprites/criaturas/veado_luta.png");
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(playerSprite, 100, 100, 512, 512);
        batch.draw(enemySprite, 1400, 800, 256, 256);

        // Exibe as barras de vida
        font.draw(batch, "Vida do Jogador: " + playerHealth, 50, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "Vida do Inimigo: " + enemyHealth, Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 50);

        font.draw(batch, battleMessage, 50, 50);

        for (int i = 0; i < actions.length - 1; i++) { // Exclui a última ação ("Fugir")
            if (i == selectedAction) {
                font.setColor(1, 1, 0, 1); // Amarelo para a ação selecionada
            } else {
                font.setColor(1, 1, 1, 1); // Branco para as outras ações
            }
            font.draw(batch, actions[i], 50, 100 - i * 20);
        }

        // Exibe o texto fixo para "Fugir"
        font.setColor(1, 1, 1, 1); // Branco
        font.draw(batch, "Clique ESC para fugir", 50, 100 - (actions.length - 1) * 20);

        batch.end();

        handleInput();
    }


    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedAction = (selectedAction - 1 + actions.length) % actions.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedAction = (selectedAction + 1) % actions.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeAction();
        }
    }

    private void executeAction() {
        switch (selectedAction) {
            case 0: // Atacar
                enemyHealth -= 10; // Reduz a vida do inimigo
                battleMessage = "Você atacou! Vida do inimigo: " + enemyHealth;

                // O inimigo também ataca
                playerHealth -= 5;
                battleMessage += "\nO inimigo contra-atacou! Sua vida: " + playerHealth;

                // Verifica se alguém perdeu
                checkBattleEnd();
                break;
            case 1: // Usar Item
                playerHealth = Math.min(playerHealth + 20, 100); // Cura o jogador, mas não ultrapassa 100
                battleMessage = "Você usou um item e recuperou vida! Sua vida: " + playerHealth;
                break;
        }
    }

    private void checkBattleEnd() {
        if (enemyHealth <= 0) {
            battleMessage = "Você venceu a batalha!";
        } else if (playerHealth <= 0) {
            battleMessage = "Você perdeu a batalha!";
        }
    }
    public void reset() {
        battleMessage = "O que você quer fazer?";
        selectedAction = 0;
    }
    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        background.dispose();
        playerSprite.dispose();
        enemySprite.dispose();
        font.dispose();
    }
}
