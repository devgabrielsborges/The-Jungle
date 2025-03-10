package io.github.com.ranie_borges.thejungle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class MainMenuScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    // Declara a textura do fundo para poder descartá-la depois
    private Texture backgroundTexture;

    public MainMenuScreen(Main game) {
        this.game = game;

        // Cria o Stage com um viewport responsivo
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Carrega a skin
        skin = new Skin(Gdx.files.internal("mainMenu/uiskin.json"));

        // Carrega a textura do fundo e cria o Image
        backgroundTexture = new Texture(Gdx.files.internal("mainMenu/telaMenu.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true); // Faz com que a imagem preencha toda a tela

        // Adiciona o fundo ao stage antes dos outros atores
        stage.addActor(backgroundImage);
        // Opcional: se necessário, garanta que o fundo fique atrás dos outros atores
        backgroundImage.toBack();

        // Cria um Table para organizar os elementos da UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Cria os componentes do menu
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Listener para o botão Play
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenuScreen", "Play button clicked!");
                // Exemplo: game.setScreen(new GameScreen(game)); para trocar para a tela de jogo
            }
        });

        // Listener para o botão Exit
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Organiza os elementos no Table
        table.add(playButton).width(200f).height(60f).pad(10);
        table.row();
        table.add(exitButton).pad(10);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        // Limpa a tela com uma cor de fundo
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza e desenha o Stage
        stage.act(delta);
        stage.draw();
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
        skin.dispose();
        // Libera a textura do fundo para evitar vazamento de memória
        backgroundTexture.dispose();
    }
}
