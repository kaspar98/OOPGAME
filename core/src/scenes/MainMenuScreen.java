package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.OOPGame;



import helpers.GameInfo;

public class MainMenuScreen implements Screen {
    final OOPGame game;

    private int highscore;
    private OrthographicCamera camera;
    private Label info;
    private Stage stage;
    private Viewport viewport;

    public MainMenuScreen(final OOPGame game, int highscore) {
        this.game = game;
        this.highscore = highscore;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        stage = new Stage(new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT), game.batch);
        info = new Label("OOPGAME \n" +
                "Highscore " + highscore + "\n" +
                "TAP ANYWHERE TO BEGIN", new Label.LabelStyle(new BitmapFont(), Color.ORANGE));
        info.setFontScale(2);
        info.setPosition(200, 200);
        stage.addActor(info);
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
//        game.font.draw(game.batch, "OOPGAME ", 350, 300);
//        game.font.draw(game.batch, "Tap anywhere to begin!", 350, 200);
//        game.font.draw(game.batch, "Highscore: " + highscore, 350, 240);
        game.batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game, highscore));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
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
    }
}