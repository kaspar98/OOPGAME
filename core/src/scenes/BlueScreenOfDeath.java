package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.OOPGame;

import helpers.GameInfo;

public class BlueScreenOfDeath implements Screen {
    private OOPGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Viewport viewport;

    private Sprite bsod = new Sprite(new Texture(Gdx.files.internal("bsod.png")));

    private int highscore;

    private long timeFinished;

    public BlueScreenOfDeath(OOPGame game, int highscore) {
        this.game = game;
        this.batch = game.getBatch();
        this.highscore = highscore;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        bsod.setSize(GameInfo.WIDTH, GameInfo.HEIGHT);
        bsod.setOrigin(bsod.getWidth() * 0.5f, bsod.getHeight() * 0.5f);
        bsod.setOriginBasedPosition(GameInfo.WIDTH * 0.5f, GameInfo.HEIGHT * 0.5f);

        timeFinished = TimeUtils.millis() + 5000;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        bsod.draw(batch);

        batch.end();

        if (TimeUtils.millis() > timeFinished) {
            game.setScreen(new MainMenuScreen(game, highscore));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        bsod.getTexture().dispose();
    }
}
