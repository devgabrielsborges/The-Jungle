package io.github.com.ranie_borges.thejungle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.com.ranie_borges.thejungle.view.MainMenuScreen;

public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Define a tela inicial do jogo: o menu principal
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // Delegar o render para a tela atual
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
