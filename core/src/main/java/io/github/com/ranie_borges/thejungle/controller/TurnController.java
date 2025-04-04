package io.github.com.ranie_borges.thejungle.controller;

import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TurnController {
    private static final Logger logger = LoggerFactory.getLogger(TurnController.class);

    // References to other controllers
    private final GameState gameState;
    private final EventController<?> eventController;
    private final SaveManager saveManager;

    // Turn history for summarizing
    private final List<TurnSummary> turnHistory;
    private int currentTurn = 0;

    // Constants for attribute changes per turn
    private static final float HUNGER_INCREASE_PER_TURN = 2.0f;
    private static final float THIRST_INCREASE_PER_TURN = 3.0f;
    private static final float ENERGY_DECREASE_PER_TURN = 1.0f;
    private static final float SANITY_DECREASE_PER_TURN = 0.5f;

    // For auto-saving
    private static final int AUTO_SAVE_INTERVAL = 5; // Save every 5 turns

    public TurnController(GameState gameState,
                          EventController<?> eventController, SaveManager saveManager) {
        this.gameState = gameState;
        this.eventController = eventController;
        this.saveManager = saveManager;
        this.turnHistory = new ArrayList<>();
    }

    /**
     * Executes a complete turn with all four phases
     * @param playerAction The action chosen by the player
     * @return Summary of the turn's events
     */
    public TurnSummary executeTurn(PlayerAction playerAction) {
        if (gameState.getPlayerCharacter() == null || gameState.getCurrentAmbient() == null) {
            logger.error("Cannot execute turn: Player character or ambient not set");
            return null;
        }

        TurnSummary summary = new TurnSummary(currentTurn + 1);

        try {
            // Phase 1: Start Phase
            startPhase(summary);

            // Phase 2: Action Phase
            actionPhase(playerAction, summary);

            // Phase 3: Random Event Phase
            randomEventPhase(summary);

            // Phase 4: Maintenance Phase
            maintenancePhase(summary);

            // Add to history and return
            turnHistory.add(summary);
            currentTurn++;

            return summary;
        } catch (Exception e) {
            logger.error("Error during turn execution", e);
            return null;
        }
    }

    /**
     * Phase 1: Start Phase - Update time, environment, and show status
     */
    private void startPhase(TurnSummary summary) {
        logger.info("Starting turn {}", currentTurn + 1);

        // Update game time
        advanceGameTime();

        // Update climate conditions if needed
        updateClimateConditions();

        // Record initial status
        Character<?> player = gameState.getPlayerCharacter();
        summary.setInitialStatus(new CharacterStatus(
            player.getLife(),
            player.getHunger(),
            player.getThirsty(),
            player.getEnergy(),
            player.getSanity()
        ));

        summary.setCurrentAmbient(gameState.getCurrentAmbient());
        summary.setCurrentDate(gameState.getOffsetDateTime());

        logger.info("Start phase complete");
    }

    /**
     * Phase 2: Action Phase - Player performs their chosen action
     */
    private void actionPhase(PlayerAction action, TurnSummary summary) {
        logger.info("Action phase: {}", action.getName());

        Character<?> player = gameState.getPlayerCharacter();

        // Apply action effects
        action.execute(player);
        summary.setPlayerAction(action);

        logger.info("Player performed action: {}", action.getName());
    }

    /**
     * Phase 3: Random Event Phase - Check for random events
     */
    private void randomEventPhase(TurnSummary summary) {
        logger.info("Random event phase");

        Ambient currentAmbient = gameState.getCurrentAmbient();
        Event event = eventController.drawEvent(currentAmbient);

        if (event != null) {
            Character<?> player = gameState.getPlayerCharacter();

            eventController.applyEvent(event, player);

            summary.setTriggeredEvent(event);
            logger.info("Random event triggered: {}", event.getName());
        } else {
            logger.info("No random event occurred this turn");
        }
    }
    /**
     * Phase 4: Maintenance Phase - Update attributes, check conditions, manage resources
     */
    private void maintenancePhase(TurnSummary summary) {
        logger.info("Maintenance phase");

        Character<?> player = gameState.getPlayerCharacter();

        // Update character attributes
        updateCharacterAttributes(player);

        // Check for critical conditions and apply effects
        checkCriticalConditions(player);

        // Update environment resources
        updateEnvironmentResources();

        // Record final status after all changes
        summary.setFinalStatus(new CharacterStatus(
            player.getLife(),
            player.getHunger(),
            player.getThirsty(),
            player.getEnergy(),
            player.getSanity()
        ));

        // Auto-save at intervals
        if (currentTurn > 0 && currentTurn % AUTO_SAVE_INTERVAL == 0) {
            autoSave();
        }

        logger.info("Maintenance phase complete");
    }

    /**
     * Advances the game time by a fixed amount
     */
    private void advanceGameTime() {
        OffsetDateTime currentTime = gameState.getOffsetDateTime();
        OffsetDateTime newTime = currentTime.plusHours(1);
        gameState.setOffsetDateTime(newTime);

        // Check if day changed
        if (currentTime.getDayOfYear() != newTime.getDayOfYear()) {
            gameState.setDaysSurvived(gameState.getDaysSurvived() + 1);
            logger.info("A new day has dawned. Days survived: {}", gameState.getDaysSurvived());
        }
    }

    /**
     * Updates climate conditions based on time and other factors
     */
    private void updateClimateConditions() {
        // Climate might change based on time of day, season, etc.
        // This would connect to the AmbientController
    }

    /**
     * Updates character attributes each turn (hunger, thirst, etc.)
     */
    private void updateCharacterAttributes(Character<?> player) {
        // Increase hunger and thirst
        player.setHunger(Math.min(100, player.getHunger() + HUNGER_INCREASE_PER_TURN));
        player.setThirsty(Math.min(100, player.getThirsty() + THIRST_INCREASE_PER_TURN));

        // Decrease energy and sanity
        player.setEnergy(Math.max(0, player.getEnergy() - ENERGY_DECREASE_PER_TURN));
        player.setSanity(Math.max(0, player.getSanity() - SANITY_DECREASE_PER_TURN));
    }

    /**
     * Checks for critical conditions and applies effects
     */
    private void checkCriticalConditions(Character<?> player) {
        // Check for critical hunger
        if (player.getHunger() >= 80) {
            player.setLife(player.getLife() - 1);
            logger.warn("Player is starving! Life decreased to {}", player.getLife());
        }

        // Check for critical thirst
        if (player.getThirsty() >= 90) {
            player.setLife(player.getLife() - 2);
            logger.warn("Player is severely dehydrated! Life decreased to {}", player.getLife());
        }

        // Check for critical sanity
        if (player.getSanity() <= 10) {
            // Apply sanity effects
            logger.warn("Player's sanity is critically low!");
        }

        // Check for death
        if (player.getLife() <= 0) {
            logger.info("Player has died!");
            // Trigger game over state
        }
    }

    private void updateEnvironmentResources() {
        Ambient currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient == null) {
            return;
        }

        // Get time-of-day factors
        OffsetDateTime currentTime = gameState.getOffsetDateTime();
        boolean isDaytime = currentTime.getHour() >= 6 && currentTime.getHour() <= 18;

        // Only regenerate resources every few turns to avoid constant resource flooding
        if (currentTurn % 3 == 0) {  // Every 3 turns
            logger.info("Resources in {} are regenerating", currentAmbient.getName());

            // Generate appropriate resources based on ambient type
            @SuppressWarnings("unchecked")
            AmbientController<Ambient> ambientController = (AmbientController<Ambient>) gameState.getAmbientController();

            if (ambientController != null) {
                Set<Clime> currentClimes = currentAmbient.getClimes();
                boolean hasHarshClimate = currentClimes.stream()
                    .anyMatch(clime -> clime.toString().contains("DROUGHT") ||
                        clime.toString().contains("STORM"));

                // Regenerate fewer resources during harsh weather
                int resourceCount = hasHarshClimate ? 1 : 2;

                // Call a resource generation method in AmbientController
                ambientController.regenerateResources(currentAmbient, resourceCount, isDaytime);

                logger.info("Resource regeneration complete in {}", currentAmbient.getName());
            }
        }

        // Check for resource depletion due to harsh climate
        if (currentAmbient.getClimes().stream().anyMatch(clime ->
            clime.toString().contains("DROUGHT") ||
                clime.toString().contains("STORM"))) {
            logger.info("Harsh conditions in {} are causing resource depletion", currentAmbient.getName());
        }
    }

    /**
     * Performs an auto-save of the game
     */
    private void autoSave() {
        logger.info("Auto-saving game...");
        saveManager.saveGame(gameState, "autosave_turn_" + currentTurn);
    }

    /**
     * Gets the turn history
     */
    public List<TurnSummary> getTurnHistory() {
        return new ArrayList<>(turnHistory);
    }

    /**
     * Gets the current turn number
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Interface for player actions
     */
    public interface PlayerAction {
        String getName();
        String getDescription();
        void execute(Character<?> character);
    }

    /**
     * Class to store character status for comparison
     */
    public static class CharacterStatus {
        private final float life;
        private final float hunger;
        private final float thirst;
        private final float energy;
        private final float sanity;

        public CharacterStatus(float life, float hunger, float thirst, float energy, float sanity) {
            this.life = life;
            this.hunger = hunger;
            this.thirst = thirst;
            this.energy = energy;
            this.sanity = sanity;
        }

        public float getLife() { return life; }
        public float getHunger() { return hunger; }
        public float getThirst() { return thirst; }
        public float getEnergy() { return energy; }
        public float getSanity() { return sanity; }
    }

    /**
     * Class to store turn summary information
     */
    public static class TurnSummary {
        private final int turnNumber;
        private OffsetDateTime currentDate;
        private Ambient currentAmbient;
        private CharacterStatus initialStatus;
        private CharacterStatus finalStatus;
        private PlayerAction playerAction;
        private Event triggeredEvent;

        public TurnSummary(int turnNumber) {
            this.turnNumber = turnNumber;
        }

        // Getters and setters
        public int getTurnNumber() { return turnNumber; }

        public OffsetDateTime getCurrentDate() { return currentDate; }
        public void setCurrentDate(OffsetDateTime currentDate) { this.currentDate = currentDate; }

        public Ambient getCurrentAmbient() { return currentAmbient; }
        public void setCurrentAmbient(Ambient currentAmbient) { this.currentAmbient = currentAmbient; }

        public CharacterStatus getInitialStatus() { return initialStatus; }
        public void setInitialStatus(CharacterStatus initialStatus) { this.initialStatus = initialStatus; }

        public CharacterStatus getFinalStatus() { return finalStatus; }
        public void setFinalStatus(CharacterStatus finalStatus) { this.finalStatus = finalStatus; }

        public PlayerAction getPlayerAction() { return playerAction; }
        public void setPlayerAction(PlayerAction playerAction) { this.playerAction = playerAction; }

        public Event getTriggeredEvent() { return triggeredEvent; }
        public void setTriggeredEvent(Event triggeredEvent) { this.triggeredEvent = triggeredEvent; }
    }
}
