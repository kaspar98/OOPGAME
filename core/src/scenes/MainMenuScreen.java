package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.OOPGame;


import helpers.GameInfo;

public class MainMenuScreen implements Screen {
    private OOPGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private Viewport viewport;

    private Sprite title;

    private Label info;

    private Sprite background;

    public MainMenuScreen(final OOPGame game, int highscore) {
        this.game = game;
        this.batch = game.getBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        stage = new Stage(new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT), batch);

        title = new Sprite(new Texture(Gdx.files.internal("title_OOPGame.png")));
        title.setCenter(GameInfo.WIDTH * 0.5f, GameInfo.HEIGHT * 0.75f);

        info = new Label(
                "Highscore " + highscore + "\n" +
                        "TAP ANYWHERE TO BEGIN",
                new Label.LabelStyle(game.getFont(), Color.ORANGE));

        info.setAlignment(Align.center);
        info.setPosition(
                GameInfo.WIDTH * 0.5f,
                GameInfo.HEIGHT * 0.3f,
                Align.center);

        stage.addActor(info);
        Gdx.input.setInputProcessor(stage);

        background = new Sprite(new Texture(Gdx.files.internal("title_space.png")));
        background.setCenter(
                GameInfo.WIDTH * 0.5f,
                GameInfo.HEIGHT * 0.5f);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        background.setCenter(
                GameInfo.WIDTH * 0.5f + -Gdx.input.getAccelerometerY() * 10f,
                GameInfo.HEIGHT * 0.5f + -Gdx.input.getAccelerometerX() * 10f);

        camera.update();

        batch.setProjectionMatrix(camera.combined);


        batch.begin();

        background.draw(batch);
        title.draw(batch);

        batch.end();


        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
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
        background.getTexture().dispose();
    }
}