package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.BackgroundManager;
import com.oopgame.game.Bullet;
import com.oopgame.game.BulletManager;
import com.oopgame.game.DustParticleManager;
import com.oopgame.game.Enemy;
import com.oopgame.game.EnemyManager;
import com.oopgame.game.MusicManager;
import com.oopgame.game.OOPGame;
import com.oopgame.game.Player;
import com.oopgame.game.Sein;
import com.oopgame.game.Seinad;
import com.oopgame.game.TouchPad;
import com.oopgame.game.UIManager;

import java.util.HashSet;
import java.util.Set;

import helpers.GameInfo;

public class GameScreen implements Screen, ContactListener {
    private final OOPGame game;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Player player;

    private BackgroundManager bgManager;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Seinad walls;
    private TouchPad touchpad;
    private Stage stage;
    private int score = 0;
    private int highscore;
    private Label currentScore;
    private Label wave;
    // muutuja mida suurendame iga update ühe võrra et arvutada hetke millal uut wave vaenlaseid teha
    private int enemyTicker = 0;
    // kaunter et lugeda palju vaenlaseid antud waves;
    private int enemyAmount = 0;

    private UIManager uiManager;

    private DustParticleManager tolm;
    private BulletManager bulletManager;
    private EnemyManager enemyManager;
    // et säilitada update tsükli lõpuni objekte
    private Set<Bullet> bulletsToKill;
    // isendiväljad et anda trackida kui suur on ekraan (resizemine)
    // vajalik et playeri tulistamine töötaks
    private int laius = GameInfo.WIDTH;
    private int pikkus = GameInfo.HEIGHT;

    private MusicManager musicManager;
    private Sound hitmarker;

    public GameScreen(final OOPGame game, int highscore) {
        this.game = game;
        this.highscore = highscore;

        Box2D.init();

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(this);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        camera.zoom = GameInfo.CAM_SCALING;

        // Windowi suurust muutes püsib ascept ratio sama (lisab black bar'id kui vaja)
        // https://youtu.be/D7u5B2Oh9r0?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=420
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, camera);

        // loome Playeri tausta keskele
        bulletManager = new BulletManager(game.batch, world);
        player = new Player(
                GameInfo.W_WIDTH / 2f,
                GameInfo.W_HEIGHT / 2f,
                world,
                bulletManager
        );

        uiManager = new UIManager(game.batch, camera, player);

        musicManager = new MusicManager();

        // tolmuefekti jaoks DustParticleManager
        tolm = new DustParticleManager(game.batch, player);

        // tausta jaoks BackgroundManageri:
        bgManager = new BackgroundManager(game.batch, camera);

        // seinad mänguvälja ümber
        walls = new Seinad(world);

        // debug renderer
        debugRenderer = new Box2DDebugRenderer();

        // tüüpi 1 vaenlaste jaoks
        enemyManager = new EnemyManager(game.batch, player, world, uiManager, bulletManager, musicManager);
        bulletsToKill = new HashSet<Bullet>();

        // teeme touchpadi
        touchpad = new TouchPad();
        // ja määrame selle asukoha
        touchpad.getTouchpad().setBounds(15, 15, 200, 200);
        // loome lava, millele touchpadi paigutada + loome touchpadi inpute töötleva protsessori
        stage = new Stage(new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT), game.batch);
        stage.addActor(touchpad.getTouchpad());

        currentScore = new Label(score + "", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        currentScore.setFontScale(2);
        currentScore.setPosition(10, GameInfo.HEIGHT - 40);

        wave = new Label("WAVE " + enemyAmount, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        wave.setFontScale(2);
        wave.setPosition(GameInfo.WIDTH / 2 - wave.getWidth(), GameInfo.HEIGHT - 40);

        stage.addActor(currentScore);
        stage.addActor(wave);
        Gdx.input.setInputProcessor(stage);

        hitmarker = Gdx.audio.newSound(Gdx.files.internal("hitmarker.wav"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        enemyTicker++;
        if (enemyTicker > 600) {
            enemyTicker = 0;
            enemyAmount++;
            score += enemyAmount;
            for (int i = 0; i < enemyAmount; i++) {
                enemyManager.addEnemy();
            }
        }
        // sellega saab testida muusika üleminekut:
        float volume = musicManager.getActionVolume();
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            volume += 0.01f;
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            volume -= 0.01f;

        // lihtsalt testimiseks
        /*if (Gdx.input.isKeyPressed(Input.Keys.R))
            camera.zoom = GameInfo.CAM_SCALING;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            camera.zoom += camera.zoom * GameInfo.CAM_SCALING;
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            camera.zoom -= camera.zoom * GameInfo.CAM_SCALING;*/

        musicManager.setActionVolume(volume);

        // box2d world steps
        world.step(1 / 60f, 6, 2);

        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // see osa on väga temporary, see katab hetkel ainult Playeri keha värskendamist,
        // teistel objektidel võivad olla hoopis teised asjad, mide värskendada.
        // Variant oleks teha igale värskendatavale objektile enda meetod, mille alusel
        // ta ennast värskendab
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        player.update();
        // käib for-iga läbi need värskendatavad kehad
        /*for (Body b : bodies) {
            BodiedSprite e = (BodiedSprite) b.getUserData();

            if (e != null) {
                // siia paneks hoopis mingi meetodi kutse
                e.bodyUpdate();
            }
        }*/

        tolm.update();

        enemyManager.update();
        bulletManager.update();
        // liigutab kaamerat playeri positsiooni järgi
        player.updateCam(camera);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // tausta lisamine
        bgManager.update();
        bgManager.render();

        tolm.render();
        bulletManager.render();
        enemyManager.render();

        // kutsub Playeris playeri renderimise välja
        player.draw(game.batch);

        uiManager.update();
        uiManager.render();
        currentScore.setText(score + "");
        wave.setText("WAVE " + enemyAmount);

        game.batch.end();


        // debug camera render
        debugRenderer.render(world, camera.combined);

        // input checks koos touchpadiga
        player.inputs(touchpad, laius, pikkus);

        // stage loodud touchpadi jaoks
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // iga ticki lõpus häväitame kõik objektid mida enam tarvis pole
        // bulletid mis on liiga kaugel playerist
        for (Bullet b : bulletManager.getLasud()) {
            if (b.getDistance(player.getX(), player.getY()) > GameInfo.WIDTH) {
                b.die();
                world.destroyBody(b.getBody());
            }
        }
        // bulletid mis collidisid
        for (Bullet b : bulletsToKill) {
            b.die();
            world.destroyBody(b.getBody());
        }
        // vaenlased mis tapeti
        for (Enemy e : enemyManager.getVaenlased()) {
            if (e.getHealth() <= 0) {
                e.die();
                score += e.getScoreValue();
                world.destroyBody(e.getBody());
            }
        }
        bulletsToKill.clear();
        if (player.getHealth() <= 0) {
            if (score > highscore) {
                highscore = score;
                FileHandle handle = Gdx.files.local("highscore.txt");
                handle.writeString(highscore + "", false);
            }
            game.setScreen(new MainMenuScreen(game, highscore));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height);
        laius = width;
        pikkus = height;
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
        bgManager.dispose();
        tolm.dispose();
        enemyManager.dispose();
        bulletManager.dispose();
        stage.dispose();

        uiManager.dispose();

        musicManager.dispose();
        hitmarker.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof Sein) {
            if (contact.getFixtureB().getUserData() instanceof Player) {
                Player player = (Player) contact.getFixtureB().getUserData();
                Sein sein = (Sein) contact.getFixtureA().getUserData();

                player.addForce(sein.getForce());
            }
        } else if (contact.getFixtureB().getUserData() instanceof Sein) {
            if (contact.getFixtureA().getUserData() instanceof Player) {
                Player player = (Player) contact.getFixtureA().getUserData();
                Sein sein = (Sein) contact.getFixtureB().getUserData();

                player.addForce(sein.getForce());
            }
        }
        if (contact.getFixtureA().getUserData() instanceof Bullet) {
            if (contact.getFixtureB().getUserData() instanceof Player) {
                Bullet lask = (Bullet) contact.getFixtureA().getUserData();
                if (!lask.isPlayerShot()) {
                    bulletsToKill.add(lask);
                    float damage = lask.getDamage();
                    if (player.getShield() > 0) {
                        float kilpi = player.getShield() - damage;
                        player.setShield(kilpi);
                        if (kilpi < 0) {
                            player.setHealth(player.getHealth() + kilpi);
                        }
                    } else {
                        player.setHealth(player.getHealth() - damage);

                    }
                }
            }
        } else if (contact.getFixtureB().getUserData() instanceof Bullet) {
            if (contact.getFixtureA().getUserData() instanceof Player) {
                Bullet lask = (Bullet) contact.getFixtureB().getUserData();
                if (!lask.isPlayerShot()) {
                    bulletsToKill.add(lask);
                    float damage = lask.getDamage();
                    if (player.getShield() > 0) {
                        float kilpi = player.getShield() - damage;
                        player.setShield(kilpi);
                        if (kilpi < 0) {
                            player.setHealth(player.getHealth() + kilpi);
                        }
                    } else {
                        player.setHealth(player.getHealth() - damage);

                    }
                }
            }
        }
        if (contact.getFixtureA().getUserData() instanceof Bullet) {
            if (contact.getFixtureB().getUserData() instanceof Enemy) {
                Bullet lask = (Bullet) contact.getFixtureA().getUserData();
                Enemy enemy = (Enemy) contact.getFixtureB().getUserData();
                if (lask.isPlayerShot()) {
                    hitmarker.play(0.5f);
                    bulletsToKill.add(lask);
                    float damage = lask.getDamage();
                    if (enemy.getShield() > 0) {
                        float kilpi = enemy.getShield() - damage;
                        enemy.setShield(kilpi);
                        if (kilpi < 0) {
                            enemy.setHealth(enemy.getHealth() + kilpi);
                        }
                    } else {
                        enemy.setHealth(enemy.getHealth() - damage);

                    }
                }
            }
        } else if (contact.getFixtureB().getUserData() instanceof Bullet) {
            if (contact.getFixtureA().getUserData() instanceof Enemy) {
                Bullet lask = (Bullet) contact.getFixtureB().getUserData();
                Enemy enemy = (Enemy) contact.getFixtureB().getUserData();
                if (lask.isPlayerShot()) {
                    hitmarker.play(0.5f);
                    bulletsToKill.add(lask);
                    float damage = lask.getDamage();
                    if (enemy.getShield() > 0) {
                        float kilpi = enemy.getShield() - damage;
                        enemy.setShield(kilpi);
                        if (kilpi < 0) {
                            enemy.setHealth(enemy.getHealth() + kilpi);
                        }
                    } else {
                        enemy.setHealth(enemy.getHealth() - damage);
                    }
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof Sein) {
            if (contact.getFixtureB().getUserData() instanceof Player) {
                /*Player player = (Player) contact.getFixtureB().getUserData();*/
                Sein sein = (Sein) contact.getFixtureA().getUserData();

                player.subForce(sein.getForce());
            }
        } else if (contact.getFixtureB().getUserData() instanceof Sein) {
            if (contact.getFixtureA().getUserData() instanceof Player) {
                /*Player player = (Player) contact.getFixtureA().getUserData();*/
                Sein sein = (Sein) contact.getFixtureB().getUserData();

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
}
