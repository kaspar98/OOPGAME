package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.backgrounds.BackgroundManager;
import com.oopgame.game.Bullet;
import com.oopgame.game.BulletManager;
import com.oopgame.game.DustParticleManager;
import com.oopgame.game.enemies.Enemy;
import com.oopgame.game.ExplosionManager;
import com.oopgame.game.GibsManager;
import com.oopgame.game.MusicManager;
import com.oopgame.game.OOPGame;
import com.oopgame.game.Player;
import com.oopgame.game.Sein;
import com.oopgame.game.inputs.GameControls;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.WaveManager;

import helpers.GameInfo;

public class GameScreen implements Screen, ContactListener {
    private final OOPGame game;
    private SpriteBatch batch;

    private Viewport viewport;
    private OrthographicCamera camera;
    private Stage stage;

    private Player player;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private ExplosionManager explosionManager;
    private BulletManager bulletManager;
    private GibsManager gibsManager;

    private UIManager uiManager;
    private BackgroundManager bgManager;
    private DustParticleManager tolm;
    private WaveManager waveManager;

    private MusicManager musicManager;
    private Sound hitmarker;

    private GameControls gameControls;

    public GameScreen(final OOPGame game) {
        this.game = game;
        this.batch = game.getBatch();

        Box2D.init();

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(this);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        camera.zoom = GameInfo.CAM_SCALING;

        // Windowi suurust muutes püsib ascept ratio sama (lisab black bar'id kui vaja)
        // https://youtu.be/D7u5B2Oh9r0?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=420
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        // loome lava, millele touchpadi paigutada + loome touchpadi inpute töötleva protsessori
        stage = new Stage(new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT), batch);

        bulletManager = new BulletManager(batch, world);

        gibsManager = new GibsManager(world, batch);

        explosionManager = new ExplosionManager(batch);

        // loome Playeri mänguvälja keskele
        player = new Player(
                GameInfo.W_WIDTH * 0.5f,
                GameInfo.W_HEIGHT * 0.5f,
                world, stage, camera,
                bulletManager, gibsManager, explosionManager);

        uiManager = new UIManager(batch, camera, player);

        // taustamuusika jaoks MusicManager()
        musicManager = new MusicManager();

        // tausta jaoks BackgroundManageri:
        bgManager = new BackgroundManager(batch, camera);

        // tolmuefekti jaoks DustParticleManager
        tolm = new DustParticleManager(batch, player);

        // seinad mänguvälja ümber
        looSeinad();

        waveManager = new WaveManager(batch, player, world, stage,
                uiManager, bulletManager, musicManager,
                explosionManager, gibsManager, game.getFont());

        hitmarker = Gdx.audio.newSound(Gdx.files.internal("hitmarker.wav"));

        // debug renderer
        debugRenderer = new Box2DDebugRenderer();

        gameControls = new GameControls(player);
        Gdx.input.setInputProcessor(gameControls);
    }

    @Override
    public void show() {

    }

    public void update(float delta) {
        waveManager.update();

        world.step(1 / 60f, 6, 2);

        player.update();

        tolm.update();
        gibsManager.update();
        musicManager.update(delta);
        bulletManager.update();
        explosionManager.update();
        waveManager.update();

        gameControls.holdPressed();

        // liigutab kaamerat playeri positsiooni järgi
        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tausta lisamine
        bgManager.update();
        bgManager.render();

        tolm.render();
        bulletManager.render();

        // kutsub Playeris playeri renderimise välja
        player.draw(batch);

        waveManager.render();

        gibsManager.render();

        explosionManager.render();

        uiManager.update();
        uiManager.render();

        batch.end();

        // debug camera render
        //debugRenderer.render(world, camera.combined);

        // input checks koos touchpadiga
        player.inputs();

        // stage loodud touchpadi jaoks
        stage.act(/*Gdx.graphics.getDeltaTime()*/delta);
        stage.draw();

        if (player.isDone()) {
            int score = waveManager.getScore();
            // loeb igakord uuesti sisse failis oleva highscore'i, sest muidu äkki sama ajal kui
            // mäng lahti on, kirjutab teine sama mängu instance faili kõrgema highscore'i
            FileHandle handle = Gdx.files.local("highscore.txt");
            int highscore = Integer.parseInt(handle.readString("UTF-8"));

            if (score > highscore) {
                handle.writeString("" + score, false, "UTF-8");
                highscore = score;
            }

            /*game.setScreen(new MainMenuScreen(game, highscore));*/
            game.setScreen(new BlueScreenOfDeath(game, highscore));
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
        player.dispose();

        bgManager.dispose();
        tolm.dispose();
        explosionManager.dispose();
        bulletManager.dispose();
        waveManager.dispose();
        uiManager.dispose();

        musicManager.dispose();
        hitmarker.dispose();

        stage.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
        Object first = contact.getFixtureA().getUserData();
        Object second = contact.getFixtureB().getUserData();

        if (first instanceof Sein) {
            if (second instanceof Player) {
                Player player = (Player) second;
                Sein sein = (Sein) first;

                player.addForce(sein.getForce());
            }
        } else if (second instanceof Sein) {
            if (first instanceof Player) {
                Player player = (Player) first;
                Sein sein = (Sein) second;

                player.addForce(sein.getForce());
            }
        }

        if (first instanceof Bullet)
            bulletCheck(first, second);
        else if (second instanceof Bullet)
            bulletCheck(second, first);
    }


    @Override
    public void endContact(Contact contact) {
        Object first = contact.getFixtureA().getUserData();
        Object second = contact.getFixtureB().getUserData();

        if (first instanceof Sein) {
            if (second instanceof Player) {
                Sein sein = (Sein) first;

                player.subForce(sein.getForce());
            }
        } else if (second instanceof Sein) {
            if (first instanceof Player) {
                Sein sein = (Sein) second;

                player.subForce(sein.getForce());
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void bulletCheck(Object bulletObject, Object object) {
        Bullet bullet = (Bullet) bulletObject;

        if (object instanceof Player && !bullet.isPlayerShot()) {
            bullet.hasHit();

            player.damage(bullet.getDamage());
        } else if (object instanceof Enemy && bullet.isPlayerShot()) {
            Enemy enemy = (Enemy) object;

            bullet.hasHit();

            hitmarker.play(0.5f);

            enemy.damage(bullet.getDamage());
        }
    }

    private void looSeinad() {
        float seinaPaksusPool = 120;

        float[][] coords = new float[][]{
                {GameInfo.W_WIDTH / 2f, -seinaPaksusPool},
                {GameInfo.W_WIDTH / 2f, GameInfo.W_HEIGHT + seinaPaksusPool},
                {-seinaPaksusPool, GameInfo.W_HEIGHT / 2f},
                {GameInfo.W_WIDTH + seinaPaksusPool, GameInfo.W_HEIGHT / 2f}
        };

        float[][] suurused = new float[][]{
                {GameInfo.W_WIDTH / 2f + seinaPaksusPool * 2, seinaPaksusPool},
                {seinaPaksusPool, GameInfo.W_HEIGHT / 2f + seinaPaksusPool * 2}
        };

        float lüke = GameInfo.PLAYER_ACCELERATION * 1.15f * GameInfo.FORCE_MULTIPLIER;

        Vector2[] lükked = new Vector2[]{
                new Vector2(0, lüke),
                new Vector2(0, -lüke),
                new Vector2(lüke, 0),
                new Vector2(-lüke, 0)
        };

        for (int i = 0; i < 4; i++)
            new Sein(
                    world,
                    coords[i][0],
                    coords[i][1],
                    suurused[i / 2][0],
                    suurused[i / 2][1],
                    lükked[i]
            );
    }
}
