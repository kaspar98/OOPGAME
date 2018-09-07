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
import com.oopgame.game.Hittable;
import com.oopgame.game.Time;
import com.oopgame.game.backgrounds.BackgroundManager;
import com.oopgame.game.DustParticleManager;
import com.oopgame.game.ExplosionManager;
import com.oopgame.game.GibsManager;
import com.oopgame.game.MusicManager;
import com.oopgame.game.OOPGame;
import com.oopgame.game.Player;
import com.oopgame.game.Sein;
import com.oopgame.game.guns.damagers.Damager;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.guns.damagers.DamagerRepeat;
import com.oopgame.game.inputs.GameControls;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.WaveManager;
import com.oopgame.game.vfx.VisualEffectsManager;

import helpers.GameInfo;

public class GameScreen implements Screen, ContactListener {
    private final OOPGame game;
    private SpriteBatch batch;

    private Viewport viewport;
    private OrthographicCamera camera;
    private Stage stage;

    private boolean paused; // läheb kasutusse, kui pause menu teeme

    private Player player;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private ExplosionManager explosionManager;
    private VisualEffectsManager vfxManager;
    private DamagerManager damagerManager;
    private GibsManager gibsManager;

    private UIManager uiManager;
    private BackgroundManager bgManager;
    private DustParticleManager tolm;
    private WaveManager waveManager;

    private MusicManager musicManager;
    private Sound hitmarker;

    private GameControls gameControls;

    private Time time;

    public GameScreen(final OOPGame game) {
        this.game = game;
        this.batch = game.getBatch();

        Box2D.init();

        time = new Time();

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

        vfxManager = new VisualEffectsManager(batch, world);

        damagerManager = new DamagerManager(batch, world, time, vfxManager);

        gibsManager = new GibsManager(world, batch);

        explosionManager = new ExplosionManager(batch);

        // loome Playeri mänguvälja keskele
        player = new Player(
                GameInfo.W_WIDTH * 0.5f,
                GameInfo.W_HEIGHT * 0.5f,
                world, stage, camera,
                damagerManager, gibsManager, explosionManager);

        uiManager = new UIManager(batch, camera, stage, player);

        // taustamuusika jaoks MusicManager()
        musicManager = new MusicManager();

        // tausta jaoks BackgroundManageri:
        bgManager = new BackgroundManager(batch, camera);

        // tolmuefekti jaoks DustParticleManager
        tolm = new DustParticleManager(batch, player);

        // seinad mänguvälja ümber
        looSeinad();

        waveManager = new WaveManager(batch, player, world, stage,
                uiManager, damagerManager, musicManager,
                explosionManager, gibsManager, game.getFont(), time,
                vfxManager);

        hitmarker = Gdx.audio.newSound(Gdx.files.internal("hitmarker.wav"));

        // debug renderer
        debugRenderer = new Box2DDebugRenderer();

        gameControls = new GameControls(this, player);
        Gdx.input.setInputProcessor(gameControls);
    }

    @Override
    public void show() {

    }

    public void update(float delta) {
        time.update();

        waveManager.update();

        world.step(1 / 60f, 6, 2);

        player.update();

        tolm.update();
        gibsManager.update();
        musicManager.update(delta);
        damagerManager.update();
        explosionManager.update();
        vfxManager.update();
        waveManager.update();

        gameControls.holdPressed();

        // liigutab kaamerat playeri positsiooni järgi
        camera.update();
    }

    @Override
    public void render(float delta) {
        if (!paused)
            update(delta);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tausta lisamine
        bgManager.update();
        bgManager.render();


        vfxManager.render(0);
        vfxManager.render(1);

        waveManager.render();

        damagerManager.render();

        // kutsub Playeris playeri renderimise välja
        player.draw(batch);

        tolm.render();

        gibsManager.render();

        explosionManager.render();
        vfxManager.render(2);

        uiManager.update();
        uiManager.render();

        batch.end();

        // debug camera render
        /*debugRenderer.render(world, camera.combined);*/

        // input checks koos touchpadiga
        player.inputs();

        // stage loodud touchpadi jaoks
        stage.act(delta);
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

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void pause() {
        paused = true;
        uiManager.placeOverlay(0, 0, 0, 0.5f);
    }

    @Override
    public void resume() {
        paused = false;
        uiManager.removeOverlay();
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
        vfxManager.dispose();
        damagerManager.dispose();
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

        if (first instanceof Damager)
            damagerStart(first, second);
        else if (second instanceof Damager)
            damagerStart(second, first);

        /*if (first instanceof MotherShip1 || second instanceof MotherShip1)
            System.out.println(time.getTime() + " started");*/
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

        if (first instanceof DamagerRepeat)
            damagerEnd(first, second);
        else if (second instanceof DamagerRepeat)
            damagerEnd(second, first);

        /*if (first instanceof MotherShip1 || second instanceof MotherShip1)
            System.out.println(time.getTime() + " ended");*/
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void damagerStart(Object damagerObject, Object object) {
        // siinkohas me veel ühtegi body modifitseerida ei saa, seega pidin damagerManagerile
        // tegema eraldi süsteemi, millega kuule deactivate'ida ja poolida

        if (object instanceof Hittable) {
            Hittable hittable = (Hittable) object;
            Damager damager = (Damager) damagerObject;

            if (hittable.isHit(damager)) {
                if (damager instanceof DamagerRepeat)
                    ((DamagerRepeat) damager).addHittable(hittable);

                if (hittable.getFaction() != 0)
                    // TODO: MOVE TO ENEMYMANAGER SO IT WOULD WORK ON REPEATING DAMAGE!
                    hitmarker.play(0.5f);
            }
        }
    }

    private void damagerEnd(Object damagerObject, Object object) {
        if (object instanceof Hittable) {
            Hittable hittable = (Hittable) object;
            DamagerRepeat damager = (DamagerRepeat) damagerObject;

            damager.removeHittable(hittable);
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
