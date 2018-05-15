package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oopgame.game.BackgroundManager;
import com.oopgame.game.Bullet;
import com.oopgame.game.BulletManager;
import com.oopgame.game.DustParticleManager;
import com.oopgame.game.Enemy;
import com.oopgame.game.EnemyManager;
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

    private UIManager uiManager;

    private DustParticleManager tolm;
    private BulletManager bulletManager;
    private EnemyManager enemies;
    // et säilitada update tsükli lõpuni objekte
    private Set<Bullet> bulletsToKill;

    private Music musicA;
    private Music musicB;

    public GameScreen(final OOPGame game) {
        this.game = game;

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
                /*GameInfo.W_WIDTH / 2f*//*GameInfo.W_WIDTH*/100,
                /*GameInfo.W_WIDTH / 2f*//*GameInfo.W_HEIGHT*/100,
                world,
                bulletManager
        );

        uiManager = new UIManager(game.batch, camera, player);

        // tolmuefekti jaoks DustParticleManager
        tolm = new DustParticleManager(game.batch, player);

        // tausta jaoks BackgroundManageri:
        bgManager = new BackgroundManager(game.batch, camera);

        // seinad mänguvälja ümber
        walls = new Seinad(world);

        // debug renderer
        debugRenderer = new Box2DDebugRenderer();

        // tüüpi 1 vaenlaste jaoks
        enemies = new EnemyManager(game.batch, player, world, uiManager, bulletManager);
        bulletsToKill = new HashSet<Bullet>();

        // teeme touchpadi
        touchpad = new TouchPad();
        // ja määrame selle asukoha
        touchpad.getTouchpad().setBounds(15, 15, 200, 200);
        // loome lava, millele touchpadi paigutada + loome touchpadi inpute töötleva protsessori
        stage = new Stage(new FitViewport(800, 480), game.batch);
        stage.addActor(touchpad.getTouchpad());
        Gdx.input.setInputProcessor(stage);

        // muusikaga jamamine
        // musicA on veits intense'im versioon b-st
        // idee oleks seda valjumaks keerata, kui mingi vastased lähedal
        // numbriga 1 on intro
        // numbriga 2 oleks nö tavaline soundtrack
        // numbriga 3 (seda veel hetkel pole valmis kompileerinud) oleks mingi boss leveli versioon, või kui vastaseid väga palju, või kui elusid vähe on
        musicA = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_a1.mp3"));
        musicA.setVolume(0.1f);
        musicA.play();
        musicA.setLooping(false);
        musicA.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                float volume = musicB.getVolume();
                musicA = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_a2.mp3"));
                musicA.setVolume(volume);
                musicA.setLooping(true);
                musicA.play();
            }
        });

        musicB = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_b1.mp3"));
        musicB.play();
        musicB.setLooping(false);
        musicB.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                float volume = musicB.getVolume();
                musicB = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_b2.mp3"));
                musicB.setVolume(volume);
                musicB.setLooping(true);
                musicB.play();
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // TODO: eraldi muusika manageri klass, kuhu saab teada anda, kui valju muusika olema peaks ja millist osa muusikast mängida
        // sellega saab testida muusika üleminekut:
        float volume = musicA.getVolume();
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            volume += 0.01f;
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            volume -= 0.01f;

        if (volume > 1)
            volume = 1;
        else if (volume < 0)
            volume = 0;

        // lihtsalt testimiseks
        /*if (Gdx.input.isKeyPressed(Input.Keys.R))
            camera.zoom = GameInfo.CAM_SCALING;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            camera.zoom += camera.zoom * GameInfo.CAM_SCALING;
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            camera.zoom -= camera.zoom * GameInfo.CAM_SCALING;*/

        musicA.setVolume(volume);

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

        enemies.update(player);
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
        enemies.render();

        // kutsub Playeris playeri renderimise välja
        player.draw(game.batch);

        uiManager.update();
        uiManager.render();

        game.batch.end();


        // debug camera render
        debugRenderer.render(world, camera.combined);

        // input checks koos touchpadiga
        player.inputs(touchpad);

        // stage loodud touchpadi jaoks
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        for (Bullet b : bulletsToKill) {
            b.die();
            world.destroyBody(b.getBody());
        }
        bulletsToKill.clear();
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
        // võtame playeri tekstuuri ja tausta maha mälust
        player.dispose();
        bgManager.dispose();
        tolm.dispose();
        enemies.dispose();

        touchpad.dispose();

        uiManager.dispose();

        musicA.dispose();
        musicB.dispose();
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
