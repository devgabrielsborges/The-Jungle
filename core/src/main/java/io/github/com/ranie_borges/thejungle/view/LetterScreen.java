package io.github.com.ranie_borges.thejungle.view;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.com.ranie_borges.thejungle.core.Main;

public class LetterScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Texture backgroundTexture;
    private Music backgroundMusic;
    private Skin skin;

    public LetterScreen(Main game) {
        this.game = game;

        // Cria o Stage com um viewport responsivo e define o input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Carrega a skin (verifique se o caminho está correto)
        skin = new Skin(Gdx.files.internal("mainMenu/lgdxs-ui.json"));

        // Configura o background utilizando uma imagem estática
        backgroundTexture = new Texture(Gdx.files.internal("letter/letterScreen.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Configura e inicia a música de fundo
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("letter/waves.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Cria um Label que exibirá o texto "press backspace to skip..."
        Label infoLabel = new Label("press backspace to skip...", skin);
        infoLabel.getColor().a = 0f; // Inicia invisível
        // Após 5 segundos, o Label aparece com fade in em 2 segundos
        infoLabel.addAction(sequence(delay(5f), fadeIn(2f)));

        // Cria uma Table para posicionar o Label no canto inferior direito
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().right().pad(10); // margem de 10 pixels
        table.add(infoLabel);
        stage.addActor(table);
    }

    @Override
    public void show() {
        // Garante que o stage esteja totalmente visível
        stage.getRoot().getColor().a = 1f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        // Se o player pressionar a tecla espaço, muda para GameScreen
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            backgroundMusic.stop();
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        backgroundMusic.dispose();
        skin.dispose();
    }
}
