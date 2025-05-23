// core/src/main/java/io/github/com/ranie_borges/thejungle/controller/TurnController.java
package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.com.ranie_borges.thejungle.controller.exceptions.turn.TurnControllerException;
import io.github.com.ranie_borges.thejungle.controller.managers.MapManager;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Mountain;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;
import io.github.com.ranie_borges.thejungle.view.ProceduralMapScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TurnController {
    private static final Logger logger = LoggerFactory.getLogger(TurnController.class);
    private final GameState gameState;
    private final AmbientController ambientController; // Game's main AmbientController
    private final EventController eventController;

    private Stage stage;
    private Skin skin;
    private ProceduralMapScreen gameScreenInstance;

    public TurnController(GameState gameState, AmbientController ambientController) {
        this.gameState = gameState;
        this.ambientController = ambientController;
        this.eventController = gameState.getEventController();
        if (this.eventController == null) {
            logger.warn("EventController not found in GameState, creating a new one for TurnController.");
            gameState.setEventController(this.eventController);
        }
    }

    public void setUI(Stage stage, Skin skin, ProceduralMapScreen gameScreenInstance) {
        this.stage = stage;
        this.skin = skin;
        this.gameScreenInstance = gameScreenInstance;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void advanceTurn() {
        logger.info("--- Advancing to new turn (Day {}) ---", gameState.getDaysSurvived() + 1);
        startPhase();
        actionPhase();
    }

    private void startPhase() {
        logger.info("Start Phase: Updating ambient and providing summary.");
        Ambient currentAmbientFromGameState = gameState.getCurrentAmbient();
        if (currentAmbientFromGameState != null && this.ambientController != null) {
            this.ambientController.setGlobalClime(currentAmbientFromGameState.getClimes().stream().findFirst().orElse(null));
            logger.info("StartPhase: Ambient for turn (from GameState): {} (Clime: {})",
                currentAmbientFromGameState.getName(), this.ambientController.getGlobalClime());
        } else if (currentAmbientFromGameState != null) {
            logger.info("StartPhase: Ambient for turn (from GameState): {}", currentAmbientFromGameState.getName());
        } else {
            logger.warn("StartPhase: currentAmbientFromGameState is null.");
        }

        gameState.setDaysSurvived(gameState.getDaysSurvived() + 1);
        gameState.setOffsetDateTime(OffsetDateTime.now());

        if (gameState.getChatController() != null) {
            gameState.getChatController().addMessage("Day " + gameState.getDaysSurvived() + " begins.");
            // Message about current location will be added by chooseAmbient or updateScreenMapAndEntities
        }
    }

    private void actionPhase() {
        logger.info("Action Phase: Player chooses an action.");
        if (this.stage != null && this.skin != null && this.gameScreenInstance != null) {
            showAmbientSelection(this.stage, this.skin);
        } else {
            logger.error("Stage, Skin, or GameScreenInstance not set. Cannot show ambient selection dialog.");
            if (this.gameScreenInstance != null) {
                chooseAmbient(null); // Fallback
            }
        }
    }

    public void chooseAmbient(Ambient chosenAmbientFromDialog) {
        try {
            logger.debug("chooseAmbient: Player choice from dialog: {}", chosenAmbientFromDialog != null ? chosenAmbientFromDialog.getName() : "Stay Here (null)");

            ProceduralMapScreen currentScreen = this.gameScreenInstance;
            if (currentScreen == null) {
                logger.error("chooseAmbient: GameScreenInstance is null!");
                throw new TurnControllerException("ProceduralMapScreen instance not available.");
            }

            MapManager mapManager = currentScreen.getMapManager();
            if (mapManager == null) {
                logger.error("chooseAmbient: MapManager is null!");
                throw new TurnControllerException("MapManager not available from current screen.");
            }
            gameState.setMapManager(mapManager); // Ensure GameState has this mapManager reference

            Ambient ambientToGenerateMapFor;

            if (chosenAmbientFromDialog == null) { // "Stay Here" was chosen
                // "Stay Here" means continue in the ambient type that *just completed its cycle*.
                // This was stored by MapManager in ambientBeforeRotation and reflected in GameState by handleDoorTraversal.
                ambientToGenerateMapFor = mapManager.getAmbientBeforeRotation(); // This is the key
                if(ambientToGenerateMapFor == null){ // Safety if somehow not set
                    logger.warn("ambientBeforeRotation was null in MapManager, defaulting to GameState's current or Jungle for 'Stay Here'");
                    ambientToGenerateMapFor = gameState.getCurrentAmbient() != null ? gameState.getCurrentAmbient() : new Jungle();
                }
                logger.info("Player chose to 'Stay Here' in ambient type: {}. Resetting its usage count.", ambientToGenerateMapFor.getName());
                mapManager.forceSetCurrentAmbient(ambientToGenerateMapFor, true); // true to reset usage count
            } else { // A new ambient type was chosen from the dialog options
                logger.info("Player chose to travel to new ambient type: {}", chosenAmbientFromDialog.getName());
                Character player = gameState.getPlayerCharacter();
                if (player != null) {
                    float energyCost = chosenAmbientFromDialog.getDifficulty() * 5; // Example cost
                    player.setEnergy(player.getEnergy() - energyCost);
                    logger.debug("Energy reduced by {}. New energy: {}", energyCost, player.getEnergy());
                }
                mapManager.forceSetCurrentAmbient(chosenAmbientFromDialog, true); // true to reset usage count
                // ambientToGenerateMapFor = chosenAmbientFromDialog; // Not needed as mapManager is now set
            }

            // mapManager.getCurrentAmbient() is now correctly set to the chosen or "stayed" ambient.
            logger.debug("TurnController: Instructing MapManager to generate map for: {}", mapManager.getCurrentAmbient().getName());
            mapManager.generateMapForCurrentAmbient();

            // Sync GameState with what MapManager has now set as current
            gameState.setCurrentAmbient(mapManager.getCurrentAmbient());
            gameState.setCurrentMap(mapManager.getMap());
            logger.debug("GameState updated. Current Ambient: {}, Map set.", gameState.getCurrentAmbient().getName());

            currentScreen.updateScreenMapAndEntities(); // This will use the updated GameState/MapManager
            logger.debug("Screen updated.");

            randomEventPhase(gameState.getCurrentAmbient());
            maintenancePhase();
            logger.debug("chooseAmbient method completed successfully.");

            Gdx.input.setInputProcessor(stage); // Ensure input processor is reset to the stage
            if (this.gameScreenInstance != null) {
                this.gameScreenInstance.setMapTransitionTriggered(false);
            }

        } catch (Exception e) {
            logger.error("chooseAmbient: CRITICAL ERROR during ambient choice processing: {}", e.getMessage(), e);
            if (this.gameScreenInstance != null) { // Attempt to unblock game even on error
                this.gameScreenInstance.setMapTransitionTriggered(false);
            }
            throw new TurnControllerException("Failed to choose ambient", e);
        }
    }

    private void randomEventPhase(Ambient currentAmbientForEvent) {
        logger.info("Random Event Phase: Checking for random events in {}.", currentAmbientForEvent != null ? currentAmbientForEvent.getName() : "null ambient");
        if (eventController != null && currentAmbientForEvent != null) {
            eventController.generateRandomEvent(currentAmbientForEvent);
        } else {
            logger.warn("EventController is null or currentAmbientForEvent is null, cannot generate random events.");
        }
    }

    private void maintenancePhase() {
        logger.info("Maintenance Phase: Adjusting character attributes and resources.");
        Character player = gameState.getPlayerCharacter();
        if (player != null) {
            player.setHunger(Math.max(0, player.getHunger() - 10));
            player.setThirsty(Math.max(0, player.getThirsty() - 15));
            player.setSanity(Math.max(0, player.getSanity() - 5)); // Example value
            player.setEnergy(Math.max(0, player.getEnergy() - 8));   // Example value

            if (player.getHunger() <= 0 || player.getThirsty() <= 0 || player.getSanity() <= 0) {
                player.setLife(Math.max(0, player.getLife() - 5)); // Penalty for neglect
                if (gameState.getChatController() != null) {
                    gameState.getChatController().addMessage("You are suffering from neglect!");
                }
            }
            logger.info("Character stats after maintenance: Life={}, Hunger={}, Thirsty={}, Energy={}, Sanity={}",
                player.getLife(), player.getHunger(), player.getThirsty(), player.getEnergy(), player.getSanity());
        }

        Ambient currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient != null) {
            // Resource regeneration/depletion logic would go here if any
            logger.info("Resources in {} after maintenance: {}", currentAmbient.getName(), currentAmbient.getResources().size());
        }
    }

    public void showAmbientSelection(Stage stage, Skin skin) {
        final Dialog ambientChoiceDialog = new Dialog("Choose Your Next Path", skin) {
            @Override
            protected void result(Object object) {
                chooseAmbient((Ambient) object); // object can be null for "Stay Here"
            }
        };
        ambientChoiceDialog.text("Where do you want to go next? Each path has its own challenges and rewards.");

        List<Ambient> allPossibleAmbients = Arrays.asList(
            new Cave(), new Jungle(), new LakeRiver(), new Mountain(), new Ruins()
        );

        // This is the ambient type that just completed its 3-map cycle.
        // It was set in gameState by handleDoorTraversal before advanceTurn was called.
        // Or more robustly, get it from mapManager.getAmbientBeforeRotation() via gameState's mapManager
        Ambient ambientCycleCompleted = null;
        if(gameState.getMapManager() != null){
            ambientCycleCompleted = gameState.getMapManager().getAmbientBeforeRotation();
        }
        if (ambientCycleCompleted == null) { // Fallback if somehow not set
            logger.warn("ambientBeforeRotation from MapManager was null in showAmbientSelection. Using GameState's current ambient.");
            ambientCycleCompleted = gameState.getCurrentAmbient();
        }
        if (ambientCycleCompleted == null) { // Absolute fallback
            logger.error("Critical: No ambient context for 'Stay Here' label. Defaulting to Jungle.");
            ambientCycleCompleted = new Jungle();
        }


        logger.debug("showAmbientSelection: 'Stay Here' button will refer to ambient type: {}", ambientCycleCompleted.getName());

        Ambient finalAmbientCycleCompleted = ambientCycleCompleted;
        List<Ambient> choices = allPossibleAmbients.stream()
            .filter(a -> !a.getName().equals(finalAmbientCycleCompleted.getName())) // Offer different ambients
            .collect(Collectors.toList());

        if (choices.size() > 3) {
            java.util.Collections.shuffle(choices, new Random());
            choices = choices.subList(0, 3);
        } else if (choices.isEmpty()) {
            for(Ambient defaultChoice : allPossibleAmbients){
                if(!defaultChoice.getName().equals(ambientCycleCompleted.getName())){
                    choices.add(defaultChoice);
                    if(choices.size() >=1) break;
                }
            }
            if(choices.isEmpty() && !allPossibleAmbients.isEmpty()) {
                choices.add(allPossibleAmbients.get(0)); // Add any if all were same as current
            }
            if(choices.isEmpty()) choices.add(new Jungle()); // Absolute fallback
        }
        if (choices.size() > 3) {
            java.util.Collections.shuffle(choices, new Random());
            choices = choices.subList(0, 3);
        }

        for (Ambient ambient : choices) {
            ambientChoiceDialog.button(ambient.getName() + " (Difficulty: " + ambient.getDifficulty() + ")", ambient);
        }

        ambientChoiceDialog.button("Stay Here (in " + ambientCycleCompleted.getName() + ")", null);

        ambientChoiceDialog.show(stage);
    }
}
