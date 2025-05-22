// core/src/main/java/io/github/com/ranie_borges/thejungle/controller/TurnController.java
package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TurnController {
    private static final Logger logger = LoggerFactory.getLogger(TurnController.class);
    private final GameState gameState;
    private final AmbientController ambientController;
    private final EventController eventController;

    private Stage stage;
    private Skin skin;
    private ProceduralMapScreen gameScreenInstance; // New field to hold reference to ProceduralMapScreen

    public TurnController(GameState gameState, AmbientController ambientController) {
        this.gameState = gameState;
        this.ambientController = ambientController;
        this.eventController = gameState.getEventController();
        if (this.eventController == null) {
            logger.warn("EventController not found in GameState, creating a new one.");
            gameState.setEventController(new EventController(gameState));
        }
    }

    /**
     * Sets the UI components (Stage and Skin) and the game screen instance for the TurnController.
     * This method should be called once the UI components and screen are initialized.
     * @param stage The Stage instance for UI.
     * @param skin The Skin instance for UI styling.
     * @param gameScreenInstance The active ProceduralMapScreen instance.
     */
    public void setUI(Stage stage, Skin skin, ProceduralMapScreen gameScreenInstance) {
        this.stage = stage;
        this.skin = skin;
        this.gameScreenInstance = gameScreenInstance;
    }

    /**
     * Advances the game to the next turn. This method is called when an ambient cycle is complete.
     */
    public void advanceTurn() {
        logger.info("--- Advancing to new turn (Day {}) ---", gameState.getDaysSurvived() + 1);
        startPhase();
        actionPhase(); // This will show the ambient selection UI
    }

    /**
     * Placeholder method - maps are now generated based on door traversal or choice.
     * This method is no longer used for triggering turn advancement logic.
     */
    public void mapGenerated() {
        // This method is intentionally left empty or can be removed if not called from anywhere
        // Its logic is now integrated into ProceduralMapScreen.handleDoorTraversal and MapManager.checkAndRotateAmbient
    }

    /**
     * Phase 1: Start Phase.
     * Updates ambient conditions and provides a summary of the previous turn (via chat messages).
     */
    private void startPhase() {
        logger.info("Start Phase: Updating ambient and providing summary.");
        Ambient currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient != null) {
            ambientController.setGlobalClime(currentAmbient.getClimes().stream().findFirst().orElse(null));
            logger.info("Current ambient: {} (Clime: {})", currentAmbient.getName(), ambientController.getGlobalClime());
        }

        gameState.setDaysSurvived(gameState.getDaysSurvived() + 1);
        gameState.setOffsetDateTime(OffsetDateTime.now());

        ChatController chatController = new ChatController();
        chatController.addMessage("Day " + gameState.getDaysSurvived() + " begins. You are in " + (currentAmbient != null ? currentAmbient.getName() : "an unknown place") + ".");
        chatController.addMessage("Last turn summary: [To be implemented: show what happened previously]");
    }

    /**
     * Phase 2: Action Phase. Player chooses a main action. This now directly shows the ambient selection UI.
     */
    private void actionPhase() {
        logger.info("Action Phase: Player chooses an action.");
        if (this.stage != null && this.skin != null && this.gameScreenInstance != null) { // Added check for gameScreenInstance
            showAmbientSelection(this.stage, this.skin);
        } else {
            logger.error("Stage, Skin, or GameScreenInstance not set for TurnController. Cannot show ambient selection dialog.");
        }
    }

    /**
     * Handles the player's choice of a new ambient from the dialog.
     * This method is called after the prompt, and now handles the actual map generation for the chosen ambient.
     * @param chosenAmbient The Ambient chosen by the player, or null if "Stay Here".
     */
    public void chooseAmbient(Ambient chosenAmbient) {
        try {
            logger.debug("chooseAmbient: Method called with chosenAmbient: {}", chosenAmbient != null ? chosenAmbient.getName() : "null (Stay Here)");

            ProceduralMapScreen currentScreen = this.gameScreenInstance;
            if (currentScreen == null) {
                logger.error("chooseAmbient: GameScreenInstance is null! This should not happen.");
                throw new TurnControllerException("ProceduralMapScreen instance not available.");
            }

            MapManager mapManager = currentScreen.getMapManager(); // Get MapManager from the current screen
            if (mapManager == null) {
                logger.error("chooseAmbient: MapManager is null! This should not happen from currentScreen.");
                throw new TurnControllerException("MapManager not available from current screen.");
            }

            logger.debug("chooseAmbient: Stage actors size BEFORE map update logic: {}", stage.getActors().size);
            for (Actor actor : stage.getActors()) { // Use Actor for iteration
                logger.debug("chooseAmbient: Actor on stage BEFORE update: {}", actor.getName() != null ? actor.getName() : actor.getClass().getSimpleName());
            }

            if (chosenAmbient == null) { // This means "Stay Here" was chosen or dialog was dismissed
                logger.debug("chooseAmbient: User chose to Stay Here. Regenerating map for current ambient.");
                mapManager.generateMapForCurrentAmbient();
                logger.debug("chooseAmbient: Map generated for current ambient.");
                currentScreen.updateScreenMapAndEntities(); // Update the screen with the new map
                logger.debug("chooseAmbient: Screen updated after Stay Here.");
            } else {
                logger.debug("chooseAmbient: User chose ambient: {}", chosenAmbient.getName());
                // Apply energy cost (example)
                Character player = gameState.getPlayerCharacter();
                if (player != null) {
                    float energyCost = chosenAmbient.getDifficulty() * 5;
                    player.setEnergy(player.getEnergy() - energyCost);
                    logger.debug("chooseAmbient: Energy reduced by {}. New energy: {}", energyCost, player.getEnergy());
                    if (player.getEnergy() <= 0) {
                        logger.warn("chooseAmbient: Player ran out of energy!");
                    }
                }

                logger.debug("chooseAmbient: Setting MapManager's current ambient to chosen: {}", chosenAmbient.getName());
                mapManager.setCurrentAmbient(chosenAmbient);
                logger.debug("chooseAmbient: Generating map for chosen ambient: {}", chosenAmbient.getName());
                mapManager.generateMapForCurrentAmbient();
                logger.debug("chooseAmbient: Map generated for chosen ambient.");
                currentScreen.updateScreenMapAndEntities(); // Update the screen with the new map
                logger.debug("chooseAmbient: Screen updated after choosing new ambient.");
            }

            // Proceed to the next phases regardless of choice (event and maintenance for the new/current state)
            logger.debug("chooseAmbient: Starting random event phase.");
            randomEventPhase(gameState.getCurrentAmbient());
            logger.debug("chooseAmbient: Starting maintenance phase.");
            maintenancePhase();
            logger.debug("chooseAmbient: chooseAmbient method completed successfully.");

            // --- Log stage state after all logic completes ---
            logger.debug("chooseAmbient: Stage actors size AFTER map update logic and phases: {}", stage.getActors().size);
            for (Actor actor : stage.getActors()) { // Use Actor for iteration
                logger.debug("chooseAmbient: Actor on stage AFTER update: {}", actor.getName() != null ? actor.getName() : actor.getClass().getSimpleName());
            }


            // Explicitly set input processor back to the stage after dialog is dismissed
            Gdx.input.setInputProcessor(stage);

        } catch (Exception e) {
            logger.error("chooseAmbient: CRITICAL ERROR during ambient choice processing: {}", e.getMessage(), e);
            throw new TurnControllerException("Failed to choose ambient", e);
        }
    }

    /**
     * Phase 3: Random Event Phase.
     * Checks for and executes a random event.
     * @param currentAmbient The current ambient for event generation.
     */
    private void randomEventPhase(Ambient currentAmbient) {
        logger.info("Random Event Phase: Checking for random events.");
        if (eventController != null) {
            eventController.generateRandomEvent(currentAmbient);
        } else {
            logger.warn("EventController is null, cannot generate random events.");
        }
    }

    /**
     * Phase 4: Maintenance Phase.
     * Adjusts hunger, thirst, sanity, and manages resource regeneration/depletion.
     */
    private void maintenancePhase() {
        logger.info("Maintenance Phase: Adjusting character attributes and resources.");
        Character player = gameState.getPlayerCharacter();
        if (player != null) {
            player.setHunger(Math.max(0, player.getHunger() - 10));
            player.setThirsty(Math.max(0, player.getThirsty() - 15));
            player.setSanity(Math.max(0, player.getSanity() - 5));
            player.setEnergy(Math.max(0, player.getEnergy() - 8));

            if (player.getHunger() <= 0 || player.getThirsty() <= 0 || player.getSanity() <= 0) {
                player.setLife(Math.max(0, player.getLife() - 5));
                ChatController chatController = new ChatController();
                chatController.addMessage("You are suffering from neglect!");
            }
            logger.info("Character stats after maintenance: Life={}, Hunger={}, Thirsty={}, Energy={}, Sanity={}",
                player.getLife(), player.getHunger(), player.getThirsty(), player.getEnergy(), player.getSanity());
        }

        Ambient currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient != null) {
            logger.info("Resources in {} after maintenance: {}", currentAmbient.getName(), currentAmbient.getResources().size());
        }
    }

    /**
     * Presents the player with a choice of 3 random ambients to travel to.
     * @param stage The Stage to add the dialog to.
     * @param skin The Skin for the UI elements.
     */
    public void showAmbientSelection(Stage stage, Skin skin) {
        Dialog ambientChoiceDialog = new Dialog("Choose Your Next Path", skin) {
            @Override
            protected void result(Object object) {
                if (object == null) {
                    chooseAmbient(null);
                } else if (object instanceof Ambient) {
                    chooseAmbient((Ambient) object);
                } else {
                    logger.warn("Invalid object returned from ambient selection dialog: {}", object);
                    chooseAmbient(null);
                }
            }
        };

        ambientChoiceDialog.text("Where do you want to go next? Each path has its own challenges and rewards.");

        List<Ambient> availableAmbients = new ArrayList<>(Arrays.asList(
            new Cave(), new Jungle(), new LakeRiver(), new Mountain(), new Ruins()
        ));

        Ambient currentAmbient = gameState.getCurrentAmbient();
        List<Ambient> choices = availableAmbients.stream()
            .filter(a -> !a.getName().equals(currentAmbient.getName()))
            .collect(Collectors.toList());

        if (choices.size() > 3) {
            java.util.Collections.shuffle(choices, new Random());
            choices = choices.subList(0, 3);
        } else if (choices.isEmpty() && currentAmbient != null) {
            choices.add(currentAmbient);
        } else if (choices.isEmpty()) {
            choices.add(new Jungle());
        }

        for (Ambient ambient : choices) {
            ambientChoiceDialog.button(ambient.getName() + " (Difficulty: " + ambient.getDifficulty() + ")", ambient);
        }

        ambientChoiceDialog.button("Stay Here", null);

        ambientChoiceDialog.show(stage);
    }
}
