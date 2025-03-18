package io.github.com.ranie_borges.thejungle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    public GameScreen(Main game) {
        this.game = game;

        // Cria o Stage com um viewport responsivo
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Carrega a skin
        skin = new Skin(Gdx.files.internal("mainMenu/lgdxs-ui.json"));

        // Carrega a textura do fundo e cria o Image
        backgroundTexture = new Texture(Gdx.files.internal("MainScreen/telaTest.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        // Adiciona o fundo ao stage e garante que ele fique atrás dos outros atores
        stage.addActor(backgroundImage);
        backgroundImage.toBack();

        // Cria um Table para organizar os elementos da UI e alinha-o no canto superior direito
        Table table = new Table();
        table.setFillParent(true);
        table.center(); // Alinha os elementos no topo e à direita
        stage.addActor(table);

        // Cria os componentes do menu
        //TextButton playButton = new TextButton("PLAY", skin);
        //TextButton exitButton = new TextButton("Exit", skin);

        // Listener para o botão Play
        //playButton.addListener(new ClickListener() {
       //     @Override
        //    public void clicked(InputEvent event, float x, float y) {
        //        game.setScreen(new GameScreen(game)); // Adicione a tela do jogo aqui
        //    }
       // });

        // Listener para o botão Exit
        //exitButton.addListener(new ClickListener() {
        //    @Override
        //    public void clicked(InputEvent event, float x, float y) {
        //        Gdx.app.exit();
        //    }
       // });

        // Adiciona o botão PLAY na tabela alinhado à direita
        //table.add(playButton).width(200f).height(60f).padTop(500).pad(10);
        //table.row();
        //table.add(exitButton).padTop(200).pad(10);
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
        backgroundTexture.dispose();
    }
}
