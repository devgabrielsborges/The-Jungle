// Arquivo: core/src/main/java/io/github/com/ranie_borges/thejungle/view/BattleScreen.java
package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
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
    private final Texture cannibalSprite;

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
        cannibalSprite = new Texture("sprites/criaturas/canibal_luta.png");

        font = new BitmapFont();

        // Inicializa a posição e espaçamento das barras
        barX = 50;
        baseY = Gdx.graphics.getHeight() - 150;
        spacing = 20;
    }

    private void drawBar(ShapeRenderer renderer, Color color, float progress, float x, float y) {
        float barWidth = 200;
        float barHeight = 15;

        // Desenha borda
        renderer.setColor(0, 0, 0, 0.7f);
        renderer.rect(x - 2, y - 2, barWidth + 4, barHeight + 4);
        // Fundo da barra
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(x, y, barWidth, barHeight);
        // Progresso
        renderer.setColor(color);
        renderer.rect(x, y, progress * barWidth, barHeight);
    }
    public void resetEnemyHealth() {
        enemyHealth = 100;
    }
    public void setCurrentEnemy(Creature enemy) {
        this.currentEnemy = enemy;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Character character, GameState gameState,
                       int width, int height) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.setColor(0, 0, 0, 0.5f);
        batch.draw(playerSprite, 105, 95, 512, 512);
        batch.setColor(Color.WHITE);
        batch.draw(playerSprite, 100, 100, 512, 512);

        if (currentEnemy != null) {
            if (enemyHealth == 0) {
                batch.setColor(Color.RED);
            }
            if (currentEnemy instanceof Cannibal) {
                batch.draw(cannibalSprite, 1400, 800, 256, 256);
            } else {
                batch.draw(enemySprite, 1400, 800, 256, 256);
            }
            batch.setColor(Color.WHITE);
        }

        // Fundo semitransparente para a área de texto
        batch.setColor(0, 0, 0, 0.5f);
        batch.draw(background, 30, 20, 600, 120);
        batch.setColor(Color.WHITE);

        font.draw(batch, "Vida do Jogador: " + character.getLife(), 50, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "Vida do Inimigo: " + enemyHealth, Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 50);
        font.draw(batch, battleMessage, 50, 100);

        // Desenha as ações centralizadas com fonte maior
        float previousScaleX = font.getData().scaleX;
        float previousScaleY = font.getData().scaleY;
        font.getData().setScale(2f); // aumenta a fonte
        GlyphLayout layout = new GlyphLayout();
        float screenWidth = Gdx.graphics.getWidth();
        float centerY = Gdx.graphics.getHeight() / 2f;
        float totalHeight = 0;
        float spacing = 20;

        // Calcula altura total do menu
        for (String action : actions) {
            layout.setText(font, action);
            totalHeight += layout.height + spacing;
        }
        totalHeight -= spacing; // remove o espaçamento extra

        float startY = centerY + totalHeight / 2f;

        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            layout.setText(font, action);
            float textWidth = layout.width;
            float x = (screenWidth - textWidth) / 2f;
            if (i == selectedAction) {
                font.setColor(1, 1, 0, 1);
            } else {
                font.setColor(1, 1, 1, 1);
            }
            font.draw(batch, action, x, startY - i * (layout.height + spacing));
        }
        // Restaura a escala original
        font.getData().setScale(previousScaleX, previousScaleY);

        batch.end();

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
        // Bloqueia o ataque se o inimigo já estiver morto
        if (selectedAction == 0 && enemyHealth <= 0) {
            battleMessage = "O inimigo está morto! Não é possível atacar.";
            return;
        }

        switch (selectedAction) {
            case 0: // Atacar
                enemyHealth = Math.max(enemyHealth - 80, 0);
                battleMessage = "Você atacou! Vida do inimigo: " + enemyHealth;
                // Se o inimigo morrer, remove-o do mapa e finaliza a batalha
                if (enemyHealth == 0) {
                    battleMessage = "Você venceu a batalha!";
                    if (currentEnemy != null) {
                        if (game.getScreen() instanceof ProceduralMapScreen) {
                            ((ProceduralMapScreen) game.getScreen()).removeEnemyFromMap(currentEnemy);
                        }
                        currentEnemy = null;
                    }
                } else {
                    // Realiza contra-ataque com base no tipo de inimigo
                    if (currentEnemy instanceof Cannibal) {
                        // O canibal causa mais dano (ex.: 10 pontos)
                        character.setLife(character.getLife() - 10);
                        battleMessage += "\nO canibal contra-atacou! Sua vida: " + character.getLife();
                    } else {
                        // O deer causa menos dano (ex.: 5 pontos)
                        character.setLife(character.getLife() - 5);
                        battleMessage += "\nO inimigo contra-atacou! Sua vida: " + character.getLife();
                    }
                }
                checkBattleEnd(character);
                break;
            case 1: // Usar Item
                character.setLife(Math.min(character.getLife() + 20, 100));
                battleMessage = "Você usou um item e recuperou vida! Sua vida: " + character.getLife();
                break;
            case 2: // Fugir
                battleMessage = "Você fugiu da batalha!";
                break;
        }
    }

    private void checkBattleEnd(Character character) {
        if (enemyHealth <= 0) {
            battleMessage = "Você venceu a batalha!";
        } else if (character.getLife() <= 0) {
            battleMessage = "Você perdeu a batalha!";
        }
    }


    public void dispose() {
        batch.dispose();
        renderer.dispose();
        background.dispose();
        playerSprite.dispose();
        enemySprite.dispose();
        font.dispose();
    }
}
