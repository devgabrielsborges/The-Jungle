package com.ranieborges.thejungle.cli.model.stats;

import com.ranieborges.thejungle.cli.controller.AmbientController;
import com.ranieborges.thejungle.cli.controller.EventManager;
import com.ranieborges.thejungle.cli.controller.FactionManager; // Import FactionManager
import com.ranieborges.thejungle.cli.model.entity.Character;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState {
    private Character playerCharacter;
    private int turnCounter;
    private AmbientController ambientController;
    private EventManager eventManager;
    private FactionManager factionManager;

    public GameState(Character playerCharacter, int turnCounter,
                     AmbientController ambientController, EventManager eventManager,
                     FactionManager factionManager) {
        this.playerCharacter = playerCharacter;
        this.turnCounter = turnCounter;
        this.ambientController = ambientController;
        this.eventManager = eventManager;
        this.factionManager = factionManager;
    }
}
