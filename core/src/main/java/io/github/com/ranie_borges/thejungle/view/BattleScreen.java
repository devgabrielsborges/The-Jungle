// Arquivo: core/src/main/java/io/github/com/ranie_borges/thejungle/view/BattleScreen.java
package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;

public class BattleScreen {
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;
    private final Texture background;
    private final Texture playerSprite;
    private final Texture enemySprite;
    private final BitmapFont font;
    private final Main game;
    private final Screen previousScreen;
    private int enemyHealth = 100;
    private String battleMessage = "O que você quer fazer?";
    private int selectedAction = 0;
    private final String[] actions = {"Atacar", "Usar Item", "Fugir"};
    private Creature currentEnemy;

    // Campos para as barras
    private float barX;
    private float baseY;
    private float spacing;

    public BattleScreen(Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        background = new Texture("Gameplay/battle_background.png");
        playerSprite = new Texture("sprites/character/personagem_luta.png");
        enemySprite = new Texture("sprites/criaturas/veado_luta.png");
        font = new BitmapFont();

        // Inicializa a posição e espaçamento das barras
        barX = 50;
        baseY = Gdx.graphics.getHeight() - 150;
        spacing = 20;
    }

    private void drawBar(ShapeRenderer renderer, Color color, float progress, float x, float y) {
        float barWidth = 200;
        float barHeight = 15;
        // Desenha o fundo da barra
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(x, y, barWidth, barHeight);
        // Desenha a barra preenchida conforme o progresso
        renderer.setColor(color);
        renderer.rect(x, y, progress * barWidth, barHeight);
    }

    public void setCurrentEnemy(Creature enemy) {
        this.currentEnemy = enemy;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Character character, GameState gameState,
                       int width, int height) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(playerSprite, 100, 100, 512, 512);
        batch.draw(enemySprite, 1400, 800, 256, 256);

        // Exibe a vida atual do personagem
        font.draw(batch, "Vida do Jogador: " + character.getLife(), 50, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "Vida do Inimigo: " + enemyHealth, Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 50);

        font.draw(batch, battleMessage, 50, 50);

        for (int i = 0; i < actions.length - 1; i++) { // Exclui a última ação ("Fugir")
            if (i == selectedAction) {
                font.setColor(1, 1, 0, 1);
            } else {
                font.setColor(1, 1, 1, 1);
            }
            font.draw(batch, actions[i], 50, 100 - i * 20);
        }

        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Clique ESC para fugir", 50, 100 - (actions.length - 1) * 20);

        batch.end();

        // Renderiza a barra de vida do jogador usando os valores de character
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBar(shapeRenderer, Color.RED, character.getLife() / 100f, barX, baseY);
        shapeRenderer.end();

        handleInput(character);
    }

    private void handleInput(Character character) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedAction = (selectedAction - 1 + actions.length) % actions.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedAction = (selectedAction + 1) % actions.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeAction(character);
        }
    }

    private void executeAction(Character character) {
        switch (selectedAction) {
            case 0: // Atacar
                enemyHealth -= 80;
                battleMessage = "Você atacou! Vida do inimigo: " + enemyHealth;
                // Dano refletido na barra de vida do jogador
                character.setLife(character.getLife() - 5);
                battleMessage += "\nO inimigo contra-atacou! Sua vida: " + character.getLife();
                checkBattleEnd(character);
                break;
            case 1: // Usar Item
                // Exemplo de recuperação
                character.setLife(Math.min(character.getLife() + 20, 100));
                battleMessage = "Você usou um item e recuperou vida! Sua vida: " + character.getLife();
                break;
        }
    }

    private void checkBattleEnd(Character character) {
        if (enemyHealth <= 0) {
            battleMessage = "Você venceu a batalha!";
            // Ações de fim de batalha podem ser adicionadas aqui.
        } else if (character.getLife() <= 0) {
            battleMessage = "Você perdeu a batalha!";
        }
    }

    public void reset() {
        battleMessage = "O que você quer fazer?";
        selectedAction = 0;
    }

    public void resize(int width, int height) {}
    public void show() {}
    public void hide() {}
    public void pause() {}
    public void resume() {}

    public void dispose() {
        batch.dispose();
        renderer.dispose();
        background.dispose();
        playerSprite.dispose();
        enemySprite.dispose();
        font.dispose();
    }
}
