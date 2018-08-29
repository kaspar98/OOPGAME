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
import com.oopgame.game.guns.GunList;
import com.oopgame.game.guns.damagers.Damager;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.inputs.Pult;

import helpers.GameInfo;

public class Player extends Sprite implements Hittable {
    private Body body;
    private Fixture fixture;

    private boolean done = false;

    // TODO: health ja shield tagasi korda panna!!!
    private float maxHealth = 10000;
    private float health = maxHealth;
    private float maxShield = 10000;
    private float shield = maxShield;

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

    private GibsManager gibsManager;
    private String gibsKey = "player_ship_1b";
    private boolean wasJustKilled = false;

    private ExplosionManager explosionManager;
    private Sound explosionSound;
    private float scaler = 1;
    private long timeNextExplosion = 0;

    private long timeDeath;

    private Vector2 aiming = new Vector2(0, 0);
    private Sprite pointer;
    private float pointerAlpha;
    private Vector3 cameraPos;

    private Vector2 movementVector = new Vector2();

    private GunList gunList;

    private Integer faction = 0;

    private static float turnModifier = 5f;

    public Player(float x, float y, World world, Stage stage, OrthographicCamera camera,
                  DamagerManager damagerManager, GibsManager gibsManager,
                  ExplosionManager explosionManager) {
        super(new Texture(Gdx.files.internal("player_laev_t.png")));

        this.gibsManager = gibsManager;
        this.explosionManager = explosionManager;
        this.cameraPos = camera.position;

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

        gunList = new GunList(damagerManager, body.getPosition(), faction);

        pointer = new Sprite(new Texture(Gdx.files.internal("pointerTest.png")));
        pointer.setSize(pointer.getWidth() * GameInfo.CAM_SCALING, pointer.getHeight() * GameInfo.CAM_SCALING);
        pointer.setOrigin(0, pointer.getHeight() * 0.5f);
        pointer.setAlpha(0);
    }

    public void inputs() {
        // hetkel veel väga segane kõik :D

        setBoosterPower(0);

        // keyboard < controller < screen

        if (health > 0) {
            Vector2 moving = new Vector2();
            Vector2 aiming = new Vector2();

            // puldi inputid
            Pult.movement(movementVector);

            Pult.aiming(aiming);

            if (aiming.len() > 0) {
                pointer.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
                pointer.setRotation(aiming.angle());
            }

            // ekraani inputid
            /*for (int i = 0; i < 2; i++) {
                if (!Gdx.input.isTouched(i)) continue;

                // touchpadi input
                Ekraan.movement(touchpadL, moving);

                // playeri tulistamine
                if (touchpadR.isTouched()) {
                    Ekraan.aiming(touchpadR, aiming);

                    pointer.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
                    pointer.setRotation(aiming.angle());
                }
            }*/

            // liikumisvektori rakendamine
            if (movementVector.len() > 1)
                movementVector.setLength(1);

            handleMovement();

            // tegeleme tulistamisega
            /*long time = TimeUtils.millis();

            if (aiming.len() > 0 && time > timeNextShot) {
                timeNextShot = time + GameInfo.PLAYER_SHOOTING_INTERVAL;

                bulletManager.playerShoot(body.getPosition(), aiming, bulletDamage);
            }*/
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
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

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

        updateCam();

        pointer.setAlpha(pointerAlpha > 0.5f ? 0.5f : pointerAlpha);

        if (pointerAlpha > 0) {
            pointerAlpha -= 0.05f;

            if (pointerAlpha < 0)
                pointerAlpha = 0;

            pointer.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
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

    private void updateCam() {
        float x = body.getPosition().x + body.getLinearVelocity().x / 12f * GameInfo.CAM_SCALING * 20;
        float y = body.getPosition().y + body.getLinearVelocity().y / 12f * GameInfo.CAM_SCALING * 20;

        cameraPos.set(x, y, 0);
    }

    private void handleMovement() {
        // returnib kas body-il on sama angle, mis movementVectoril
        // TODO: pole otseselt vajalik vist isegi, aga saaks kraadidest radiaanidesse optimiseerida

        float current = body.getAngle() * MathUtils.radiansToDegrees;
        float target = movementVector.angle();

        if (movementVector.len() > 0) {
            float difference = target - current;

            // siin vaatame kumba pidi oleks tegelikult kõige otsem tee liikumissuunda pöörata
            if (difference > 180)
                difference -= 360;
            else if (difference < -180)
                difference += 360;

            if ((difference > 0 ? difference : -difference) < turnModifier /*5*/) {
                body.setTransform(body.getPosition().x, body.getPosition().y,
                        MathUtils.degreesToRadians * target);
                current = target;
            } else {
                // siin pidin natuke pikemalt tegema, et pööramine toimiks õigesti
                body.setAngularVelocity(
                        MathUtils.degreesToRadians * turnModifier * difference);
            }
        }

        if (current == target) {
            // kui if ära kaotada ja niisama handleMovement() jätta,
            // siis saab ka täitsa okei välimusega lendamise,
            // vb selle jätakski playerile esialgu,
            // sest pööramise piirang sai vastaste laevade jaoks tehtud

            // visuaalide pärast oleks ka äkki variant, et mida rohkem liikumise suunas laev on,
            // seda rohkem booster töötaks
            body.applyForceToCenter(
                    movementVector.cpy()
                            .scl(GameInfo.FORCE_MULTIPLIER * GameInfo.PLAYER_ACCELERATION),
                    true);

            setBoosterPower();
        }
    }

    private void setBoosterPower() {
        setBoosterPower(movementVector.len());
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

    // relvade vahetamiseks
    public GunList getGunList() {return gunList;}

    @Override
    public boolean isHit(Damager damager) {
        if (damager.getFaction() != 1)
            return false;

        damage(damager.getDamage());
        damager.hit();

        return true;
    }

    @Override
    public int getFaction() {
        return faction;
    }

    private void damage(float damage) {
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

    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    public boolean isDone() {
        return done;
    }

    public void aimPointer(int x, int y) {
        aiming = new Vector2(
                cameraPos.x + (x - 0.5f * GameInfo.WIDTH) * GameInfo.CAM_SCALING,
                cameraPos.y + (0.5f * GameInfo.HEIGHT - y) * GameInfo.CAM_SCALING);

        aiming.sub(body.getPosition());

        aimPointer(aiming.angle());
    }

    public void aimPointer(float angle) {
        // TODO: tuleks pointerit uuendada ka siis, kui laeva asukoht vähemalt kaamera suhtes muutub
        // märkasin, et kui hiir laskmise ajal paigale jätta ja laeva liikumissuunda
        // näiteks muuta, siis pointer ei muutu, kuulida sihtimise suund ka mitte

        pointer.setRotation(angle);

        pointerAlpha = 1.5f;
    }

    public void shoot() {
        pointerAlpha = 1.5f;

        if (health > 0) {
            if (!gunList.shoot(aiming.angle())) {
                // vb lisab mingi sound effecti, millega saab aru, et tulistamine ei õnnestunud
            }
        }
    }

    public void slowDown() {
        Vector2 current = body.getLinearVelocity();
        float length = current.len();

        // et lõpmatult aeglustama ei jääks
        if (length < 0.1)
            body.setLinearVelocity(0, 0);
        else
            movementVector.add(current.cpy().scl(-1).setLength((length > 1 ? 1 : length)));
    }

    public void movementVector(Vector2 vector) {
        movementVector.set(0, 0).add(vector);
    }

    public boolean isVisible(Vector2 position) {
        float halfWidth = GameInfo.CAM_SCALING * GameInfo.WIDTH * 0.5f;
        float halfHeight = GameInfo.CAM_SCALING * GameInfo.HEIGHT * 0.5f;

        return position.x > cameraPos.x - halfWidth &&
                position.x < cameraPos.x + halfWidth &&
                position.y > cameraPos.y - halfHeight &&
                position.y < cameraPos.y + halfHeight;

    }
}
