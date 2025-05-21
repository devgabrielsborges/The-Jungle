package io.github.com.ranie_borges.thejungle.view;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.core.Main;

import java.io.File;

public class MainMenuScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Music backgroundMusic;
    private final AnimatedBackground animatedBackground;

    public MainMenuScreen(Main game) {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("mainMenu/lgdxs-ui.json"));

        float frameDuration = 0.1f;
        int frameWidth = 1736;
        int frameHeight = 894;
        String spriteSheetPath = "mainMenu/telaMenu.png";

        animatedBackground = new AnimatedBackground(spriteSheetPath, frameDuration, frameWidth, frameHeight);
        animatedBackground.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(animatedBackground);
        animatedBackground.toBack();

        String musicPath = "mainMenu/mainMenubackgroundMusic.mp3";
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        Table table = new Table(); // for handling the interface elements
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        String titleImagePath = "mainMenu/titulo.png";
        float titleDelayDuration = 1f;
        float titleFadeInDuration = 2f;

        Image titleImage = new Image(new Texture(Gdx.files.internal(titleImagePath)));

        titleImage.getColor().a = 0f; // initial opacity for fade in transition
        titleImage.addAction(sequence(delay(titleDelayDuration), fadeIn(titleFadeInDuration)));
        table.add(titleImage).center().padBottom(20);
        table.row();

        TextButton newGameButton = new TextButton("New Game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backgroundMusic.stop();
                game.getScenarioController().startNewGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backgroundMusic.stop();
                Gdx.app.exit();
            }
        });

        SaveManager saveManager = new SaveManager();

        if (saveManager.getSaveFiles().length > 0) {
            TextButton continueButton = new TextButton("Continue", skin);
            table.add(continueButton).width(200f).height(60f).center().padBottom(20);
            table.row();

            continueButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    backgroundMusic.stop();

                    File latestSave = getLatestSaveFile(saveManager);
                    if (latestSave != null) {
                        game.getScenarioController().loadSpecificSaveGame(latestSave.getName());
                    } else {
                        // Fallback to regular load behavior if we couldn't determine latest save
                        game.getScenarioController().loadSavedGame();
                    }
                }
            });
        }

        table.add(newGameButton).width(200f).height(60f).center().padBottom(20);
        table.row();
        table.add(exitButton).width(200f).height(60f).center();

    }

    @Override
    public void show() {
        stage.getRoot().getColor().a = 0f;
        float loadingDuration = 1.5f;
        stage.getRoot().addAction(fadeIn(loadingDuration));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        animatedBackground.setSize(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void pause() throws UnsupportedOperationException {
    }

    @Override
    public void resume() throws UnsupportedOperationException {
    }

    @Override
    public void hide() {
        backgroundMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundMusic.dispose();
        animatedBackground.dispose();
    }

    /**
     * Finds the most recently modified save file, prioritizing autosave.json if it
     * exists
     *
     * @param saveManager The SaveManager instance
     * @return The most recent save file or null if no saves exist
     */
    private File getLatestSaveFile(SaveManager saveManager) {
        File[] saveFiles = saveManager.getSaveFiles();

        if (saveFiles == null || saveFiles.length == 0) {
            return null;
        }

        for (File file : saveFiles) {
            if (file.getName().equals("autosave.json")) {
                return file;
            }
        }

        // If no autosave found, get the most recently modified file
        File mostRecent = saveFiles[0];
        for (int i = 1; i < saveFiles.length; i++) {
            if (saveFiles[i].lastModified() > mostRecent.lastModified()) {
                mostRecent = saveFiles[i];
            }
        }

        return mostRecent;
    }
}
