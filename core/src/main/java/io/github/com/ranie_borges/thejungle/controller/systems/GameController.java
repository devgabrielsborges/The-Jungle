package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.model.stats.GameState;

public class GameController {
    private SaveManager saveManager;
    private GameState gameState;

    public GameController(SaveManager saveManager, GameState gameState) {
        this.saveManager = new SaveManager();
    }

    public void saveGame(String saveName) {
        saveManager.saveGame(gameState, saveName);
    }

    public boolean loadGame(String fileName) {
        GameState loadedState = saveManager.loadGame(fileName);

        if (loadedState != null) {
            gameState = loadedState;
            return true;
        }
        return false;
    }

    // TODO
}
