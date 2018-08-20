package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.oopgame.game.inputs.Ekraan;
import com.oopgame.game.inputs.Hiir;
import com.oopgame.game.inputs.Klaviatuur;
import com.oopgame.game.inputs.Pult;

import helpers.GameInfo;

public class Player extends Sprite {
    private Body body;
    private Fixture fixture;

    private boolean done = false;

    private float health = 100;
    private float maxHealth = 100;
    private float shield = 100;
    private float maxShield = 100;

    private boolean damaged = false;
    private long timeDamagedExpire;
    private Sound damagedSound;

    private Sprite thruster;
    private float thrusterRadius;

    private Sound thrusterSound;
    private long thrusterSoundId;

    private Array<TouchPad> touchPads = new Array<TouchPad>();
    private Texture touchpadTextureBg;
    private Texture touchpadTextureKnob;
    private Touchpad touchpadL;
    private Touchpad touchpadR;

    private Vector2 forces;
    private float tippkiirus = GameInfo.PLAYER_MAXSPEED;

    private BulletManager bulletManager;
    private float bulletDamage = 25;
    private long timeNextShot = 0;

    private GibsManager gibsManager;
    private String gibsKey = "player_ship_1b";
    private boolean wasJustKilled = false;

    private ExplosionManager explosionManager;
    private Sound explosionSound;
    private float scaler = 1;
    private long timeNextExplosion = 0;

    private long timeDeath;

    private Sprite pointer;
    private Vector3 cameraPos;

    public Player(float x, float y, World world, Stage stage,
                  BulletManager bulletManager, GibsManager gibsManager,
                  ExplosionManager explosionManager) {
        super(new Texture(Gdx.files.internal("player_laev_t.png")));

        this.bulletManager = bulletManager;
        this.gibsManager = gibsManager;
        this.explosionManager = explosionManager;

        setSize(
                getTexture().getWidth() * GameInfo.SCALING,
                getTexture().getHeight() * GameInfo.SCALING);

        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        setCenter(x, y);


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Playerile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(getWidth() * 0.45f, getHeight() * 0.15f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        box.dispose();

        forces = new Vector2();


        thruster = new Sprite(
                new Texture(Gdx.files.internal("player_ship_1b_booster1_t.png")));
        thruster.setSize(
                0,
                getHeight() * GameInfo.SCALING * 4);
        thrusterRadius = getHeight() / 2f - 10 * GameInfo.SCALING;
        updateBooster();

        thrusterSound = Gdx.audio.newSound(Gdx.files.internal("thruster.mp3"));
        thrusterSoundId = thrusterSound.play(0);
        thrusterSound.setLooping(thrusterSoundId, true);

        // teeme Playeri jaoks touchpadid
        touchpadTextureBg = new Texture(Gdx.files.internal("ui1_touchpad2_t.png"));
        touchpadTextureKnob = new Texture(Gdx.files.internal("ui1_touchpad1_stick1_t.png"));

        // vasak - liikumine
        TouchPad touchpad = new TouchPad(touchpadTextureBg, touchpadTextureKnob);
        touchPads.add(touchpad);

        touchpadL = touchpad.getTouchpad();
        touchpadL.setSize(200, 200);
        touchpadL.setOrigin(
                touchpadL.getWidth() * 0.5f,
                touchpadL.getHeight() * 0.5f);
        touchpadL.setPosition(15, 15);

        stage.addActor(touchpadL);


        // parem - tulistamine
        touchpad = new TouchPad(touchpadTextureBg, touchpadTextureKnob);
        touchPads.add(touchpad);

        touchpadR = touchpad.getTouchpad();
        touchpadR.setSize(200, 200);
        touchpadR.setOrigin(
                touchpadR.getWidth() * 0.5f,
                touchpadR.getHeight() * 0.5f);
        touchpadR.setPosition(
                GameInfo.WIDTH - touchpadR.getWidth() - 15,
                15);

        stage.addActor(touchpadR);

        damagedSound = Gdx.audio.newSound(Gdx.files.internal("damaged.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        pointer = new Sprite(new Texture(Gdx.files.internal("pointerTest.png")));
        pointer.setSize(pointer.getWidth() * GameInfo.CAM_SCALING, pointer.getHeight() * GameInfo.CAM_SCALING);
        pointer.setOrigin(0, pointer.getHeight() * 0.5f);
        //pointer.setAlpha(0);
    }

    public void inputs() {
        // hetkel veel väga segane kõik :D

        setBoosterPower(0);

        // keyboard < controller < screen

        if (health > 0) {
            Vector2 moving = new Vector2();
            Vector2 aiming = new Vector2();
            boolean shooting;

            // klaviatuuri inputid
            Klaviatuur.movement(moving);
            shooting = Hiir.aiming(aiming, cameraPos, body.getPosition());


            // puldi inputid
            Pult.movement(moving);

            if (Pult.aiming(aiming))
                shooting = Pult.isShooting();

            if (aiming.len() > 0) {
                pointer.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
                pointer.setRotation(aiming.angle());
            }

            // ekraani inputid
            for (int i = 0; i < 2; i++) {
                if (!Gdx.input.isTouched(i)) continue;

                // touchpadi input
                Ekraan.movement(touchpadL, moving);

                // playeri tulistamine
                if (touchpadR.isTouched()) {
                    shooting = true;

                    Ekraan.aiming(touchpadR, aiming);

                    pointer.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
                    pointer.setRotation(aiming.angle());
                }
            }

            // liikumisvektori rakendamine
            if (moving.len() > 1)
                moving.setLength(1);

            body.applyForceToCenter(
                    moving.cpy()
                            .scl(GameInfo.FORCE_MULTIPLIER * GameInfo.PLAYER_ACCELERATION),
                    true);

            setBoosterPower(moving.len());

            if (moving.len() > 0) {
                setRotation(moving.angle());

                // paneb Playeri kehale ka uuesti suuna
                body.setTransform(
                        body.getPosition(),
                        moving.angleRad());
            }

            // tegeleme tulistamisega
            long time = TimeUtils.millis();

            if (aiming.len() > 0 && time > timeNextShot && shooting) {
                timeNextShot = time + GameInfo.PLAYER_SHOOTING_INTERVAL;

                bulletManager.playerShoot(body.getPosition(), aiming, bulletDamage);
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        if (health >= 0) {
            thruster.draw(batch);
            pointer.draw(batch);
            super.draw(batch);
        }
    }

    public void update() {
        long time = TimeUtils.millis();

        body.applyForceToCenter(forces, true);

        // kontrollib kas player sõidab lubatust kiiremini, kui sõidab siis alandab kiirust
        if (body.getLinearVelocity().len() > tippkiirus)
            body.setLinearVelocity(new Vector2(body.getLinearVelocity().setLength(tippkiirus)));

        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        body.setAngularVelocity(0);
        setCenter(body.getPosition().x, body.getPosition().y);

        updateBooster();

        // väike shield regen
        if (shield < maxShield && health > 0) {
            shield += 1 / 30.0f;
            if (shield > maxShield) shield = maxShield;
        }

        if (damaged && timeDamagedExpire < time) {
            setColor(Color.WHITE);
            damaged = false;
        }

        if (wasJustKilled) {
            wasJustKilled = false;

            gibsManager.createGibs(gibsKey,
                    body.getPosition().x, body.getPosition().y,
                    body.getLinearVelocity());

            explosionSound.play();
            explosionManager.addExplosion(body.getPosition().x, body.getPosition().y);
        }

        if (health <= 0) {
            if (timeNextExplosion < time) {
                timeNextExplosion = time + 200;
                scaler = scaler * 2f;
                explosionSound.play();
                explosionManager.addExplosion(body.getPosition().x, body.getPosition().y, scaler);
            }

            if (timeDeath < time) {
                done = true;
            }
        }
    }

    public void dispose() {
        thruster.getTexture().dispose();
        getTexture().dispose();
        thrusterSound.dispose();
        explosionSound.dispose();
        damagedSound.dispose();

        pointer.getTexture().dispose();

        for (TouchPad touchPad : touchPads)
            touchPad.dispose();
    }

    public void updateCam(OrthographicCamera camera) {
        float x = body.getPosition().x + body.getLinearVelocity().x / 12f * GameInfo.CAM_SCALING * 20;
        float y = body.getPosition().y + body.getLinearVelocity().y / 12f * GameInfo.CAM_SCALING * 20;

        camera.position.set(x, y, 0);

        cameraPos = camera.position;
    }

    private void setBoosterPower(float value) {
        thrusterSound.setVolume(thrusterSoundId, value * 0.25f);
        thruster.setSize(
                10 * value + MathUtils.random(-1, 1),
                thruster.getHeight());
    }

    private void updateBooster() {
        thruster.setOrigin(
                thruster.getWidth(),
                thruster.getHeight() * 0.5f);

        thruster.setOriginBasedPosition(
                body.getPosition().x - MathUtils.cosDeg(this.getRotation()) * thrusterRadius,
                body.getPosition().y - MathUtils.sinDeg(this.getRotation()) * thrusterRadius);

        thruster.setRotation(this.getRotation());
    }

    public void addForce(Vector2 force) {
        forces.add(force);
    }

    public void subForce(Vector2 force) {
        forces.sub(force);
    }

    // vajalikud UI jaoks
    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getShield() {
        return shield;
    }

    public float getMaxShield() {
        return maxShield;
    }

    public void damage(float damage) {
        long time = TimeUtils.millis();
        timeDamagedExpire = time + GameInfo.PLAYER_DAMAGED_DURATION;
        damaged = true;


        if (health > 0) {
            damagedSound.play();
            if (shield < damage) {
                setColor(Color.RED);

                float overflow = damage - shield;

                shield = 0;
                health -= overflow;

                if (health <= 0) {
                    timeDeath = time + 5000;
                    timeNextExplosion = time + 2000;

                    wasJustKilled = true;
                }
            } else {
                setColor(Color.CYAN);
                shield -= damage;
            }
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Vector2 getVector() {
        return body.getLinearVelocity();
    }

    public boolean done() {
        return done;
    }
}
