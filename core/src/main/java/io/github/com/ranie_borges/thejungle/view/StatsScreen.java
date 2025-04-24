package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Doctor;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Hunter;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Lumberjack;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Survivor;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class StatsScreen implements Screen {
    private static final Logger logger = LoggerFactory.getLogger(StatsScreen.class);

    // Profession constants
    private static final String SURVIVOR_PROFESSION = "Survivor";
    private static final String HUNTER_PROFESSION = "Hunter";
    private static final String LUMBERJACK_PROFESSION = "Lumberjack";
    private static final String DOCTOR_PROFESSION = "Doctor";

    private Main game;
    private Stage stage;
    private Skin skin;
    private TextField nameTextField;
    private String selectedProfession = "";

    private ImageButton survivorBtn, hunterBtn, lumberjackBtn, doctorBtn;
    private Label professionDescriptionLabel;
    private Image characterImage;
    private Table formTable;
    private boolean movedToLeft = false;

    private Sound professionClickSound;
    private SaveManager saveManager;

    public StatsScreen(Main game) {
        this.game = game;
        this.saveManager = new SaveManager();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("mainMenu/uiskin.json"));

        BitmapFont font = skin.getFont("default-font");
        font.getData().setScale(2f);

        Texture backgroundTexture = new Texture(Gdx.files.internal("StatsScreen/backgroundStats.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        characterImage = new Image();
        characterImage.setScaling(Scaling.fit);
        characterImage.getColor().a = 0f;

        professionClickSound = Gdx.audio.newSound(Gdx.files.internal("StatsScreen/pagina.mp3"));

        Label titleLabel = new Label("Select your profession", skin);
        titleLabel.setFontScale(2f);

        survivorBtn = createProfessionButton(SURVIVOR_PROFESSION, "sprites/profissoes/desempregado.png");
        hunterBtn = createProfessionButton(HUNTER_PROFESSION, "sprites/profissoes/profissao_cacador.png");
        lumberjackBtn = createProfessionButton(LUMBERJACK_PROFESSION, "sprites/profissoes/profissao_lenhador.png");
        doctorBtn = createProfessionButton(DOCTOR_PROFESSION, "sprites/profissoes/profissao_medico.png");

        Label survivorLabel = new Label(SURVIVOR_PROFESSION, skin);
        Label hunterLabel = new Label(HUNTER_PROFESSION, skin);
        Label lumberjackLabel = new Label(LUMBERJACK_PROFESSION, skin);
        Label doctorLabel = new Label(DOCTOR_PROFESSION, skin);

        professionDescriptionLabel = new Label("", skin);
        professionDescriptionLabel.setWrap(true);
        professionDescriptionLabel.setAlignment(Align.center);
        professionDescriptionLabel.setFontScale(1.8f);

        TextButton confirmBtn = new TextButton("Confirm", skin);
        confirmBtn.getLabel().setFontScale(2f);
        confirmBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!selectedProfession.isEmpty() && !nameTextField.getText().isEmpty()) {
                    String characterName = nameTextField.getText().trim();
                    Character character = createCharacter(selectedProfession, characterName);

                    if (character != null) {
                        // Create a new GameState with the character
                        GameState gameState = new GameState();
                        gameState.setPlayerCharacter(character);
                        gameState.setDaysSurvived(0);
                        gameState.setOffsetDateTime(OffsetDateTime.now());
                        gameState.setActiveEvents(new ArrayList<>());

                        // Save the game
                        String saveName = "save_" + characterName;
                        boolean saved = saveManager.saveGame(gameState, saveName);

                        if (saved) {
                            logger.info("Character {} created and game saved successfully", characterName);
                            // Navigate to game screen
                            game.setScreen(new ProceduralMapScreen());
                        } else {
                            logger.error("Failed to save game for character {}", characterName);
                        }
                    } else {
                        logger.warn("Character creation failed: name or profession not selected");
                    }
                }
            }
        });

        Label nameLabel = new Label("Character Name:", skin);
        nameLabel.setFontScale(1.8f);
        nameTextField = new TextField("", skin);
        nameTextField.getStyle().font.getData().setScale(2f);

        formTable = new Table(skin);
        formTable.top().padTop(20);
        formTable.setFillParent(false);

        formTable.add(titleLabel).colspan(4).padBottom(30);
        formTable.row();
        formTable.add(survivorBtn).size(100).pad(6);
        formTable.add(hunterBtn).size(100).pad(6);
        formTable.add(lumberjackBtn).size(100).pad(6);
        formTable.add(doctorBtn).size(100).pad(6);
        formTable.row();
        formTable.add(survivorLabel).pad(11);
        formTable.add(hunterLabel).pad(11);
        formTable.add(lumberjackLabel).pad(11);
        formTable.add(doctorLabel).pad(11);
        formTable.row();
        formTable.add().colspan(4).padBottom(20).row();
        formTable.add(nameLabel).colspan(4).padBottom(10);
        formTable.row();
        formTable.add(nameTextField).width(250).colspan(4).padBottom(20);
        formTable.row();
        formTable.add(professionDescriptionLabel).width(300).colspan(4).padBottom(20);
        formTable.row();
        formTable.add(confirmBtn).colspan(4).padTop(20);

        Group container = new Group();
        container.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        formTable.pack();
        formTable.setPosition((Gdx.graphics.getWidth() - formTable.getWidth()) / 2f,
                              (Gdx.graphics.getHeight() - formTable.getHeight()) / 2f);
        container.addActor(formTable);

        characterImage.setSize(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight());
        characterImage.setPosition(Gdx.graphics.getWidth(), 0);
        container.addActor(characterImage);

        stage.addActor(container);
    }

    private Character createCharacter(String profession, String name) {
        // Default initial position
        float initialX = 100f;
        float initialY = 100f;

        switch (profession) {
            case SURVIVOR_PROFESSION:
                return new Survivor(name, initialX, initialY);
            case HUNTER_PROFESSION:
                return new Hunter(name, initialX, initialY);
            case LUMBERJACK_PROFESSION:
                return new Lumberjack(name, initialX, initialY);
            case DOCTOR_PROFESSION:
                return new Doctor(name, initialX, initialY);
            default:
                logger.error("Unknown profession: {}", profession);
                return null;
        }
    }

    private void updateDescription(String profession) {
        switch (profession) {
            case SURVIVOR_PROFESSION:
                professionDescriptionLabel.setText("A well-rounded character with basic survival skills.");
                break;
            case HUNTER_PROFESSION:
                professionDescriptionLabel.setText("Skilled in tracking and combat. Higher attack damage.");
                break;
            case LUMBERJACK_PROFESSION:
                professionDescriptionLabel.setText("Strong and resilient. Higher health and attack power.");
                break;
            case DOCTOR_PROFESSION:
                professionDescriptionLabel.setText("Medical expertise. Better healing abilities and sanity.");
                break;
            default:
                professionDescriptionLabel.setText("");
        }
    }

    private ImageButton createProfessionButton(final String name, String imagePath) {
        Texture texture = new Texture(Gdx.files.internal(imagePath));
        TextureRegion region = new TextureRegion(texture);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(region);
        style.imageDown = new TextureRegionDrawable(region);

        ImageButton button = new ImageButton(style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                professionClickSound.play();
                selectedProfession = name;
                updateVisualSelection();
                updateDescription(name);
                updateCharacterImage(name);

                if (!movedToLeft) {
                    moveFormToLeft();
                }
            }
        });
        return button;
    }

    private void moveFormToLeft() {
        formTable.addAction(Actions.moveTo(
            formTable.getX() - Gdx.graphics.getWidth() / 4f,
            formTable.getY(), 0.5f));

        characterImage.addAction(Actions.sequence(
            Actions.moveTo(Gdx.graphics.getWidth() / 2f, 0, 0.5f),
            Actions.alpha(1f, 0.3f)));

        movedToLeft = true;
    }

    private void updateCharacterImage(String profession) {
        Texture texture = null;
        switch (profession) {
            case SURVIVOR_PROFESSION:
                texture = new Texture("StatsScreen/desempregado_grande.png");
                break;
            case HUNTER_PROFESSION:
                texture = new Texture("StatsScreen/cacador_grande.png");
                break;
            case LUMBERJACK_PROFESSION:
                texture = new Texture("StatsScreen/lenhador_grande.png");
                break;
            case DOCTOR_PROFESSION:
                texture = new Texture("StatsScreen/medico_grande.png");
                break;
        }

        if (texture != null) {
            characterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
    }

    private void updateVisualSelection() {
        animateSelection(survivorBtn, selectedProfession.equals(SURVIVOR_PROFESSION));
        animateSelection(hunterBtn, selectedProfession.equals(HUNTER_PROFESSION));
        animateSelection(lumberjackBtn, selectedProfession.equals(LUMBERJACK_PROFESSION));
        animateSelection(doctorBtn, selectedProfession.equals(DOCTOR_PROFESSION));
    }

    private void animateSelection(ImageButton button, boolean selected) {
        button.setScale(selected ? 1.2f : 1f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        professionClickSound.dispose();
    }
}
