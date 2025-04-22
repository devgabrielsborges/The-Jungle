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

public class TurnController<C extends Character<?>, A extends Ambient> {
    private static final Logger logger = LoggerFactory.getLogger(TurnController.class);

    private static final int AUTO_SAVE_INTERVAL = 5;
    private final GameState<C, A> gameState;
    private final SaveManager saveManager;
    private final List<TurnSummary> turnHistory;
    private int currentTurn = 0;

    private static final float HUNGER_INCREASE_PER_TURN = 2.0f;
    private static final float THIRST_INCREASE_PER_TURN = 3.0f;
    private static final float ENERGY_DECREASE_PER_TURN = 1.0f;
    private static final float SANITY_DECREASE_PER_TURN = 0.5f;
    private final EventController<C> eventController;

    public TurnController(GameState<C, A> gameState, EventController<C> eventController, SaveManager saveManager) {
        this.gameState = gameState;
        this.eventController = eventController;
        this.saveManager = saveManager;
        this.turnHistory = new ArrayList<>();
    }

    public TurnSummary executeTurn(PlayerAction<C> playerAction) {
        if (gameState.getPlayerCharacter() == null || gameState.getCurrentAmbient() == null) {
            logger.error("Cannot execute turn: Player character or ambient not set");
            return null;
        }

        TurnSummary summary = new TurnSummary(currentTurn + 1);

        try {
            startPhase(summary);
            actionPhase(playerAction, summary);
            randomEventPhase(summary);
            maintenancePhase(summary);

            turnHistory.add(summary);
            currentTurn++;

            return summary;
        } catch (Exception e) {
            logger.error("Error during turn execution", e);
            return null;
        }
    }

    private void startPhase(TurnSummary summary) {
        logger.info("Starting turn {}", currentTurn + 1);
        advanceGameTime();
        updateClimateConditions();

        C player = gameState.getPlayerCharacter();
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

    private void actionPhase(PlayerAction<C> action, TurnSummary summary) {
        logger.info("Action phase: {}", action.getName());

        C player = gameState.getPlayerCharacter();
        action.execute(player);
        summary.setPlayerAction(action);

        logger.info("Player performed action: {}", action.getName());
    }

    private void randomEventPhase(TurnSummary summary) {
        logger.info("Random event phase");

        A currentAmbient = gameState.getCurrentAmbient();
        Event event = eventController.drawEvent(currentAmbient);

        if (event != null) {
            C player = gameState.getPlayerCharacter();
            eventController.applyEvent(event, player);
            summary.setTriggeredEvent(event);
            logger.info("Random event triggered: {}", event.getName());
        } else {
            logger.info("No random event occurred this turn");
        }
    }

    private void maintenancePhase(TurnSummary summary) {
        logger.info("Maintenance phase");

        C player = gameState.getPlayerCharacter();
        updateCharacterAttributes(player);
        checkCriticalConditions(player);
        updateEnvironmentResources();

        summary.setFinalStatus(new CharacterStatus(
            player.getLife(),
            player.getHunger(),
            player.getThirsty(),
            player.getEnergy(),
            player.getSanity()
        ));

        if (currentTurn > 0 && currentTurn % AUTO_SAVE_INTERVAL == 0) {
            autoSave();
        }

        logger.info("Maintenance phase complete");
    }

    private void advanceGameTime() {
        OffsetDateTime currentTime = gameState.getOffsetDateTime();
        OffsetDateTime newTime = currentTime.plusHours(1);
        gameState.setOffsetDateTime(newTime);

        if (currentTime.getDayOfYear() != newTime.getDayOfYear()) {
            gameState.setDaysSurvived(gameState.getDaysSurvived() + 1);
            logger.info("A new day has dawned. Days survived: {}", gameState.getDaysSurvived());
        }
    }

    private void updateClimateConditions() {

    }

    private void updateCharacterAttributes(C player) {
        player.setHunger(Math.min(100, player.getHunger() + HUNGER_INCREASE_PER_TURN));
        player.setThirsty(Math.min(100, player.getThirsty() + THIRST_INCREASE_PER_TURN));
        player.setEnergy(Math.max(0, player.getEnergy() - ENERGY_DECREASE_PER_TURN));
        player.setSanity(Math.max(0, player.getSanity() - SANITY_DECREASE_PER_TURN));
    }

    private void checkCriticalConditions(C player) {
        if (player.getHunger() >= 80) {
            player.setLife(player.getLife() - 1);
            logger.warn("Player is starving! Life decreased to {}", player.getLife());
        }

        if (player.getThirsty() >= 90) {
            player.setLife(player.getLife() - 2);
            logger.warn("Player is severely dehydrated! Life decreased to {}", player.getLife());
        }

        if (player.getSanity() <= 10) {
            logger.warn("Player's sanity is critically low!");
        }

        if (player.getLife() <= 0) {
            logger.info("Player has died!");
        }
    }

    private void updateEnvironmentResources() {
        A currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient == null) {
            return;
        }

        OffsetDateTime currentTime = gameState.getOffsetDateTime();
        boolean isDaytime = currentTime.getHour() >= 6 && currentTime.getHour() <= 18;

        if (currentTurn % 3 == 0) {
            logger.info("Resources in {} are regenerating", currentAmbient.getName());

            AmbientController<A, C> ambientController = gameState.getAmbientController();
            if (ambientController != null) {
                Set<Clime> currentClimes = currentAmbient.getClimes();
                boolean hasHarshClimate = currentClimes.stream()
                    .anyMatch(clime -> clime.toString().contains("DROUGHT") ||
                        clime.toString().contains("STORM"));

                int resourceCount = hasHarshClimate ? 1 : 2;
                ambientController.regenerateResources(currentAmbient, resourceCount, isDaytime);

                logger.info("Resource regeneration complete in {}", currentAmbient.getName());
            }
        }

        if (currentAmbient.getClimes().stream().anyMatch(clime ->
            clime.toString().contains("DROUGHT") ||
                clime.toString().contains("STORM"))) {
            logger.info("Harsh conditions in {} are causing resource depletion", currentAmbient.getName());
        }
    }

    private void autoSave() {
        logger.info("Auto-saving game...");
        saveManager.saveGame(gameState, "autosave_turn_" + currentTurn);
    }

    public List<TurnSummary> getTurnHistory() {
        return new ArrayList<>(turnHistory);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public interface PlayerAction<C extends Character<?>> {
        String getName();
        String getDescription();

        void execute(C character);
    }

    public class CharacterStatus {
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

    public class TurnSummary {
        private final int turnNumber;
        private OffsetDateTime currentDate;
        private A currentAmbient;
        private CharacterStatus initialStatus;
        private CharacterStatus finalStatus;
        private PlayerAction<C> playerAction;
        private Event triggeredEvent;

        public TurnSummary(int turnNumber) {
            this.turnNumber = turnNumber;
        }

        public int getTurnNumber() { return turnNumber; }

        public OffsetDateTime getCurrentDate() { return currentDate; }
        public void setCurrentDate(OffsetDateTime currentDate) { this.currentDate = currentDate; }

        public A getCurrentAmbient() {
            return currentAmbient;
        }

        public void setCurrentAmbient(A currentAmbient) {
            this.currentAmbient = currentAmbient;
        }

        public CharacterStatus getInitialStatus() { return initialStatus; }
        public void setInitialStatus(CharacterStatus initialStatus) { this.initialStatus = initialStatus; }

        public CharacterStatus getFinalStatus() { return finalStatus; }
        public void setFinalStatus(CharacterStatus finalStatus) { this.finalStatus = finalStatus; }

        public PlayerAction<C> getPlayerAction() {
            return playerAction;
        }

        public void setPlayerAction(PlayerAction<C> playerAction) {
            this.playerAction = playerAction;
        }

        public Event getTriggeredEvent() { return triggeredEvent; }
        public void setTriggeredEvent(Event triggeredEvent) { this.triggeredEvent = triggeredEvent; }
    }
}
