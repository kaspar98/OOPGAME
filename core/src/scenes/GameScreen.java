package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.OOPGame;
import com.oopgame.game.Player;

import helpers.GameInfo;

public class GameScreen implements Screen {
    private final OOPGame game;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Player player;
    private Texture bg;

    public GameScreen(final OOPGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        // Windowi suurust muutes püsib ascept ratio sama (lisab black bar'id kui vaja)
        // https://youtu.be/D7u5B2Oh9r0?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=420
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        // loeme sisse tausta faili
        bg = new Texture(Gdx.files.internal("test_taust.png"));
        // loome Playeri tausta keskele
        player = new Player(game,bg.getWidth() / 2f, bg.getHeight() / 2f);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // liigutab kaamerat playeri positsiooni järgi
        camera.position.x = player.bounds.x + player.bounds.width / 2f;
        camera.position.y = player.bounds.y + player.bounds.height / 2f;
        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // tausta lisamine
        game.batch.draw(bg, 0, 0);
        // kutsub Playeris playeri renderimise välja
        player.render(delta);
        game.batch.end();

        // input checks
        player.inputs();
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
        // võtame playeri tekstuuri ja tausta maha mälust
        player.dispose();
        bg.dispose();
    }
}
