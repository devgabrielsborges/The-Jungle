package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.com.ranie_borges.thejungle.core.Main;

public class GameScreen implements Screen {

    private Main game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    private String nomePersonagem;
    private String profissaoPersonagem;

    public GameScreen(Main game, String nome, String profissao) {
        this.game = game;
        this.nomePersonagem = nome;
        this.profissaoPersonagem = profissao;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        skin = new Skin(Gdx.files.internal("mainMenu/uiskin.json"));

        Label titulo = new Label("Bem-vindo ao jogo!", skin);
        titulo.setFontScale(2f);

        Label nomeLabel = new Label("Nome: " + nomePersonagem, skin);
        nomeLabel.setFontScale(1.8f);

        Label profissaoLabel = new Label("Profissão: " + profissaoPersonagem, skin);
        profissaoLabel.setFontScale(1.8f);

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(100);
        table.add(titulo).padBottom(30).row();
        table.add(nomeLabel).padBottom(10).row();
        table.add(profissaoLabel);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // fundo preto
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
    }
}
