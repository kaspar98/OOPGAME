package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.FontManager;
import com.oopgame.game.OOPGame;
import com.oopgame.game.inputs.MainMenuControls;
import com.oopgame.game.main_menu_bg.MainMenuBackground;

import helpers.GameInfo;

public class MainMenuScreen implements Screen {
    private OOPGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private MainMenuControls controls = new MainMenuControls(this);

    private Viewport viewport;

    private Sprite title;

    private Label info;

    private Label credits;

    private MainMenuBackground bgManager;

    public MainMenuScreen(final OOPGame game, int highscore) {
        this.game = game;
        this.batch = game.getBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        stage = new Stage(new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT), batch);

        title = new Sprite(new Texture(Gdx.files.internal("title_OOPGame.png")));
        title.setCenter(GameInfo.WIDTH * 0.5f, GameInfo.HEIGHT * 0.75f);

        FontManager fontManager = game.getFontManager();

        info = new Label(
                "Highscore " + highscore + "\n" +
                        "PRESS ANY KEY TO BEGIN!",
                new Label.LabelStyle(fontManager.getFont("main"), Color.ORANGE));

        info.setAlignment(Align.center);
        info.setPosition(
                GameInfo.WIDTH * 0.5f,
                GameInfo.HEIGHT * 0.3f,
                Align.center);

        stage.addActor(info);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;

        fontManager.generateFont("rational-integer", parameter, "credits");

        credits = new Label("Game made by Kaspar and Oliver. Music by The Hamster Alliance.",
                new Label.LabelStyle(
                        fontManager.getFont("credits"),
                        new Color(1,1,1,0.5f)));

        credits.setAlignment(Align.bottomLeft);
        credits.setPosition(10,10,Align.bottomLeft);
        stage.addActor(credits);

        bgManager = new MainMenuBackground(batch);

        Gdx.input.setInputProcessor(controls);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        bgManager.update();

        camera.update();

        batch.setProjectionMatrix(camera.combined);


        batch.begin();

        bgManager.render();
        title.draw(batch);

        batch.end();


        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height);
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
        stage.dispose();
        title.getTexture().dispose();
        bgManager.dispose();
    }

    public void proceed() {
        game.setScreen(new GameScreen(game));
        dispose();
    }
}