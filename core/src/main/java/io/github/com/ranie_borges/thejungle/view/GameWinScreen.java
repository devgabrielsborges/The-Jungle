package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.com.ranie_borges.thejungle.controller.managers.SaveManager;
import io.github.com.ranie_borges.thejungle.core.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameWinScreen implements Screen {
    private static final Logger logger = LoggerFactory.getLogger(GameWinScreen.class);
    private final Main game;
    private final Stage stage;
    private Skin skin;
    private AnimatedBackground animatedBackground;

    public GameWinScreen(Main game, String saveNameToDelete) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        try {
            this.skin = new Skin(Gdx.files.internal("mainMenu/lgdxs-ui.json"));
            logger.info("Successfully loaded 'mainMenu/lgdxs-ui.json' for GameOverScreen.");
        } catch (Exception e) {
            logger.error("Failed to load 'mainMenu/lgdxs-ui.json' for GameOverScreen. Attempting fallback 'uiskin.json'.", e);
            try {
                this.skin = new Skin(Gdx.files.internal("uiskin.json"));
                logger.info("Successfully loaded fallback 'uiskin.json' for GameOverScreen.");
            } catch (Exception e2) {
                logger.error("Failed to load fallback 'uiskin.json' for GameOverScreen. UI might be broken.", e2);
            }
        }

        if (saveNameToDelete != null && !saveNameToDelete.trim().isEmpty()) {
            SaveManager saveManager = new SaveManager();
            boolean deleted = saveManager.deleteSave(saveNameToDelete);
            if (deleted) {
                logger.info("Save file '{}' deleted successfully upon game over.", saveNameToDelete);
            } else {
                logger.warn("Failed to delete save file '{}' upon game over. It might not exist or there was an error.", saveNameToDelete);
            }
        } else {
            logger.warn("No save file name provided to delete for GameOverScreen, or save name was empty.");
        }

        String spriteSheetPath = "mainMenu/telaMenu.png";
        float frameDuration = 0.1f;
        int frameWidth = 1736;
        int frameHeight = 894;

        try {
            animatedBackground = new AnimatedBackground(spriteSheetPath, frameDuration, frameWidth, frameHeight);
        } catch (Exception e) {
            logger.error("Failed to load animated background for GameOverScreen. Path: {}", spriteSheetPath, e);
            animatedBackground = null;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        if (animatedBackground != null) {
            animatedBackground.setSize(stage.getWidth(), stage.getHeight());
            animatedBackground.setPosition(0, 0);
            stage.addActor(animatedBackground);
            animatedBackground.toBack();
        } else {
            logger.warn("Animated background is null, not adding to stage.");
        }

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        if (skin == null) {
            logger.error("Skin is null in GameOverScreen.show(). UI elements cannot be created.");
            return;
        }

        Label gameOverLabel; // Use Label for non-interactive text
        String gameOverStyleName = "title-white"; // Preferred style from lgdxs-ui.json
        if (!skin.has(gameOverStyleName, Label.LabelStyle.class)) {
            gameOverStyleName = "title"; // Fallback from lgdxs-ui.json
            if (!skin.has(gameOverStyleName, Label.LabelStyle.class)) {
                gameOverStyleName = "default"; // Fallback from uiskin.json or lgdxs-ui.json
                logger.warn("Skin style 'title-white' or 'title' not found for Label, using 'default'.");
            }
        }

        try {
            gameOverLabel = new Label("you escaped from the jungle", skin, gameOverStyleName);
            // The font scale is often part of the style itself (e.g., "title" font is larger).
            // If you need to override or ensure a specific scale:
            // gameOverLabel.setFontScale(2.8f); // Adjust as needed if style doesn't provide enough size
            gameOverLabel.setAlignment(Align.center);
        } catch (Exception e) {
            logger.error("Error creating GameOverLabel. Style '{}' might be missing or skin is problematic.", gameOverStyleName, e);
            try {
                gameOverLabel = new Label("you escaped from the jungle", skin); // Use first available LabelStyle
                // gameOverLabel.setFontScale(2.8f);
                gameOverLabel.setAlignment(Align.center);
            } catch (Exception e2){
                logger.error("Could not create GameOverLabel even with default skin constructor for Label.", e2);
                return;
            }
        }


        TextButton mainMenuButton;
        String buttonStyleName = "default"; // From lgdxs-ui.json, which might have a ninepatch background
        try {
            mainMenuButton = new TextButton("Return to Main Menu", skin, buttonStyleName);
            mainMenuButton.getLabel().setAlignment(Align.center);

        } catch (Exception e) {
            logger.error("Error creating MainMenuButton with '{}' style.", buttonStyleName, e);
            try {
                mainMenuButton = new TextButton("Return to Main Menu", skin); // Fallback to first available style
                mainMenuButton.getLabel().setFontScale(1.5f);
                mainMenuButton.getLabel().setAlignment(Align.center);
            } catch (Exception e2) {
                logger.error("Could not create MainMenuButton even with default skin constructor for TextButton.", e2);
                mainMenuButton = null;
            }
        }

        table.add(gameOverLabel).expandX().padBottom(Value.percentHeight(0.1f, table)).row(); // Pad based on table height
        if (mainMenuButton != null) {
            table.add(mainMenuButton).prefWidth(Gdx.graphics.getWidth() * 0.1f).prefHeight(Gdx.graphics.getHeight() * 0.08f).padTop(Value.percentHeight(0.05f, table));


            mainMenuButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (game != null && game.getScenarioController() != null) {
                        game.getScenarioController().initializeGame();
                    } else {
                        logger.error("Cannot return to main menu: game or scenarioController is null.");
                    }
                }
            });
        }

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (animatedBackground != null) {
            animatedBackground.setSize(width, height);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        logger.debug("Disposing GameOverScreen");
        if (stage != null) {
            stage.dispose();
        }
        if (animatedBackground != null) {
            animatedBackground.dispose();
        }
    }
}
