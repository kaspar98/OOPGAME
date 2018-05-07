package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.DynamicBodied;
import com.oopgame.game.OOPGame;
import com.oopgame.game.Player;
import com.oopgame.game.TempSein;

import helpers.GameInfo;

public class GameScreen implements Screen {
    private final OOPGame game;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Player player;
    private Texture bg;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private TempSein wall;

    public GameScreen(final OOPGame game) {
        this.game = game;

        Box2D.init();

        world = new World(new Vector2(0, -10), true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        // Windowi suurust muutes püsib ascept ratio sama (lisab black bar'id kui vaja)
        // https://youtu.be/D7u5B2Oh9r0?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=420
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        // loeme sisse tausta faili
        bg = new Texture(Gdx.files.internal("test_taust.png"));
        // loome Playeri tausta keskele
        player = new Player(game, bg.getWidth() / 2f, bg.getHeight() / 2f, world);
        wall = new TempSein(world, 1024 / 2, -100);

        // debug renderer
        debugRenderer = new Box2DDebugRenderer();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // box2d world steps
        world.step(1 / 60f, 6, 2);

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

        // see osa on väga temporary, see katab hetkel ainult Playeri keha värskendamist,
        // teistel objektidel võivad olla hoopis teised asjad, mide värskendada.
        // Variant oleks teha igale värskendatavale objektile enda meetod, mille alusel
        // ta ennast värskendab
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        // käib for-iga läbi need värskendatavad kehad
        for (Body b : bodies) {
            DynamicBodied e = (DynamicBodied) b.getUserData();

            if (e != null) {
                // siia paneks hoopis mingi meetodi kutse
                e.bodyUpdate();
            }
        }

        // tausta lisamine
        game.batch.draw(bg, 0, 0);

        // kutsub Playeris playeri renderimise välja
        player.render(delta);

        game.batch.end();


        // debug camera render
        debugRenderer.render(world, camera.combined);

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
