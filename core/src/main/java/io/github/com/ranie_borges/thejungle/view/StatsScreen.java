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
import io.github.com.ranie_borges.thejungle.core.Main;

public class StatsScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private TextField nomeTextField;
    private String profissaoSelecionada = "";

    private ImageButton desempregadoBtn, cacadorBtn, lenhadorBtn, medicoBtn;
    private Label descricaoProfissaoLabel;
    private Image imagemPersonagem;
    private Table formulario;
    private boolean movidoParaEsquerda = false;

    private Sound somCliqueClasse;

    public StatsScreen(Main game) {
        this.game = game;
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

        imagemPersonagem = new Image();
        imagemPersonagem.setScaling(Scaling.fit);
        imagemPersonagem.getColor().a = 0f;

        somCliqueClasse = Gdx.audio.newSound(Gdx.files.internal("StatsScreen/pagina.mp3"));

        Label tituloLabel = new Label("Selecione sua profissão", skin);
        tituloLabel.setFontScale(2f);

        desempregadoBtn = criarBotaoProfissao("Desempregado", "sprites/profissoes/desempregado.png");
        cacadorBtn = criarBotaoProfissao("Cacador", "sprites/profissoes/profissao_cacador.png");
        lenhadorBtn = criarBotaoProfissao("Lenhador", "sprites/profissoes/profissao_lenhador.png");
        medicoBtn = criarBotaoProfissao("Medico", "sprites/profissoes/profissao_medico.png");

        Label desempregadoLabel = new Label("Desempregado", skin);
        Label cacadorLabel = new Label("Cacador", skin);
        Label lenhadorLabel = new Label("Lenhador", skin);
        Label medicoLabel = new Label("Medico", skin);

        descricaoProfissaoLabel = new Label("", skin);
        descricaoProfissaoLabel.setWrap(true);
        descricaoProfissaoLabel.setAlignment(Align.center);
        descricaoProfissaoLabel.setFontScale(1.8f);

        TextButton confirmarBtn = new TextButton("Confirmar", skin);
        confirmarBtn.getLabel().setFontScale(2f);
        confirmarBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String nome = nomeTextField.getText();
                if (!nome.isEmpty() && !profissaoSelecionada.isEmpty()) {
                    game.setScreen(new ProceduralMapScreen());
                } else {
                    System.out.println("Escolha um nome e uma profissão.");
                }
            }
        });

        Label nomeLabel = new Label("Nome do Personagem:", skin);
        nomeLabel.setFontScale(1.8f);
        nomeTextField = new TextField("", skin);
        nomeTextField.getStyle().font.getData().setScale(2f);

        formulario = new Table(skin);
        formulario.top().padTop(20);
        formulario.setFillParent(false);

        formulario.add(tituloLabel).colspan(4).padBottom(30);
        formulario.row();
        formulario.add(desempregadoBtn).size(100).pad(6);
        formulario.add(cacadorBtn).size(100).pad(6);
        formulario.add(lenhadorBtn).size(100).pad(6);
        formulario.add(medicoBtn).size(100).pad(6);
        formulario.row();
        formulario.add(desempregadoLabel).pad(11);
        formulario.add(cacadorLabel).pad(11);
        formulario.add(lenhadorLabel).pad(11);
        formulario.add(medicoLabel).pad(11);
        formulario.row();
        formulario.add().colspan(4).padBottom(20).row();
        formulario.add(nomeLabel).colspan(4).padBottom(10);
        formulario.row();
        formulario.add(nomeTextField).width(250).colspan(4).padBottom(20);
        formulario.row();
        formulario.add(descricaoProfissaoLabel).width(300).colspan(4).padBottom(20);
        formulario.row();
        formulario.add(confirmarBtn).colspan(4).padTop(20);

        Group container = new Group();
        container.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        formulario.pack();
        formulario.setPosition((Gdx.graphics.getWidth() - formulario.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - formulario.getHeight()) / 2f);
        container.addActor(formulario);

        imagemPersonagem.setSize(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight());
        imagemPersonagem.setPosition(Gdx.graphics.getWidth(), 0);
        container.addActor(imagemPersonagem);

        stage.addActor(container);
    }

    private ImageButton criarBotaoProfissao(final String nome, String caminhoImagem) {
        Texture texture = new Texture(Gdx.files.internal(caminhoImagem));
        ImageButton.ImageButtonStyle estilo = new ImageButton.ImageButtonStyle();
        estilo.imageUp = new TextureRegionDrawable(texture);
        estilo.imageDown = new TextureRegionDrawable(texture);

        final ImageButton botao = new ImageButton(estilo);
        botao.setTransform(true);
        botao.setScale(1f);

        botao.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profissaoSelecionada = nome;
                somCliqueClasse.play(); // 🔊 Som ao clicar
                atualizarSelecaoVisual();
                atualizarDescricao(nome);
                atualizarImagemPersonagem(nome);
                moverFormularioParaEsquerda();
            }
        });

        return botao;
    }

    private void moverFormularioParaEsquerda() {
        if (!movidoParaEsquerda) {
            float destinoX = 50;
            formulario.addAction(Actions.moveTo(destinoX, formulario.getY(), 0.5f));
            imagemPersonagem.addAction(Actions.sequence(Actions.fadeIn(0.4f)));
            imagemPersonagem.setPosition(Gdx.graphics.getWidth() / 2f, 0);
            movidoParaEsquerda = true;
        }
    }

    private void atualizarImagemPersonagem(String profissao) {
        String caminho = null;

        if (profissao.equals("Desempregado")) caminho = "StatsScreen/desempregadoFundo.png";
        else if (profissao.equals("Cacador")) caminho = "StatsScreen/cacadorFundo.png";
        else if (profissao.equals("Lenhador")) caminho = "StatsScreen/lenhadorFundo.png";
        else if (profissao.equals("Medico")) caminho = "StatsScreen/medicoFundo.png";

        if (caminho != null) {
            Texture texture = new Texture(Gdx.files.internal(caminho));
            imagemPersonagem.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));

            if (movidoParaEsquerda) {
                imagemPersonagem.getColor().a = 0f;
                imagemPersonagem.addAction(Actions.fadeIn(0.4f));
            } else {
                imagemPersonagem.getColor().a = 0f;
            }
        }
    }

    private void atualizarSelecaoVisual() {
        animarSelecao(desempregadoBtn, profissaoSelecionada.equals("Desempregado"));
        animarSelecao(cacadorBtn, profissaoSelecionada.equals("Cacador"));
        animarSelecao(lenhadorBtn, profissaoSelecionada.equals("Lenhador"));
        animarSelecao(medicoBtn, profissaoSelecionada.equals("Medico"));
    }

    private void animarSelecao(ImageButton botao, boolean selecionado) {
        botao.clearActions();
        if (selecionado) {
            botao.getImage().setColor(1, 1, 1, 1f);
            botao.addAction(Actions.sequence(
                Actions.scaleTo(1.15f, 1.15f, 0.15f),
                Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.18f, 1.18f, 0.4f),
                    Actions.scaleTo(1.15f, 1.15f, 0.4f)
                ))
            ));
        } else {
            botao.getImage().setColor(1, 1, 1, 0.4f);
            botao.addAction(Actions.scaleTo(1f, 1f, 0.15f));
        }
    }

    private void atualizarDescricao(String profissao) {
        if (profissao.equals("Desempregado")) {
            descricaoProfissaoLabel.setText("Sem ocupação atual, mas com potencial para aprender qualquer coisa.");
        } else if (profissao.equals("Cacador")) {
            descricaoProfissaoLabel.setText("Especialista em rastrear e capturar animais. Ótimo com armadilhas.");
        } else if (profissao.equals("Lenhador")) {
            descricaoProfissaoLabel.setText("Forte e resistente, coleta madeira com eficiência. Útil na construção.");
        } else if (profissao.equals("Medico")) {
            descricaoProfissaoLabel.setText("Capaz de curar aliados e manter o grupo vivo durante emergências.");
        } else {
            descricaoProfissaoLabel.setText("");
        }
    }

    @Override public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
        somCliqueClasse.dispose();
    }
}
