package io.github.com.ranie_borges.thejungle.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.com.ranie_borges.thejungle.controller.AmbientController;

public class Main extends Game {
    private SpriteBatch batch;
    private AmbientController ambientController;

    @Override
    public void create() {
        batch = new SpriteBatch();

        ambientController = new AmbientController(this);
        ambientController.initializeGame();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }

    /**
     * Get the scenario controller that manages game screens
     * @return The scenario controller instance
     */
    public AmbientController getScenarioController() {
        return ambientController;
    }
}
