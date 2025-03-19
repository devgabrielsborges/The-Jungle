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
import io.github.com.ranie_borges.thejungle.Main;

public class MainMenuScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Music backgroundMusic;
    private AnimatedBackground animatedBackground;

    public MainMenuScreen(Main game) {
        this.game = game;

        // Cria o Stage com um viewport responsivo e define o input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Carrega a skin da interface
        skin = new Skin(Gdx.files.internal("mainMenu/lgdxs-ui.json"));

        // Configura o fundo animado utilizando a spritesheet
        // Parâmetros: caminho da imagem, duração de cada frame, largura e altura de cada frame
        animatedBackground = new AnimatedBackground("mainMenu/telaMenu.png", 0.1f, 1736, 894);
        animatedBackground.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(animatedBackground);
        animatedBackground.toBack();

        // Configura e inicia a música de fundo
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("mainMenu/mainMenubackgroundMusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Cria um Table para organizar os elementos da interface
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        // Cria a imagem do título (ex.: "titulo.png" deve conter o título "The Jungle")
        Image titleImage = new Image(new Texture(Gdx.files.internal("mainMenu/titulo.png")));
        // Define a opacidade inicial como 0 para que o fade in funcione
        titleImage.getColor().a = 0f;
        // Adiciona uma ação: após um delay de 3 segundos, a imagem faz fade in em 2 segundos
        titleImage.addAction(sequence(delay(3f), fadeIn(2f)));
        // Adiciona a imagem do título à tabela, centralizada, com um espaçamento inferior
        table.add(titleImage).center().padBottom(20);
        table.row();

        // Cria os botões do menu
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Para a música antes de mudar de tela
                backgroundMusic.stop();
                game.setScreen(new LoadingScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backgroundMusic.stop();
                Gdx.app.exit();
            }
        });

        // Adiciona os botões à tabela, centralizados e com espaçamento uniforme
        table.add(playButton).width(200f).height(60f).center().padBottom(20);
        table.row();
        table.add(exitButton).width(200f).height(60f).center();
    }

    @Override
    public void show() {
        // Aplica fade in no ator raiz do Stage (toda a interface) em 5 segundos
        stage.getRoot().getColor().a = 0f;
        stage.getRoot().addAction(fadeIn(5f));
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
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        // Para a música ao sair da tela
        backgroundMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundMusic.dispose();
        animatedBackground.dispose();
    }
}
