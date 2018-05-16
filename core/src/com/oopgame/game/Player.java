package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import helpers.GameInfo;

public class Player extends Sprite {
    Body body;
    private Fixture fixture;

    private float health = 100;
    private float maxHealth = 100;
    private float shield = 100;
    private float maxShield = 100;

    private Sprite thruster;
    private float thrusterRadius;

    private Sound thrusterSound;
    private long thrusterSoundId;

    private Vector2 forces;
    private int tippkiirus = 45;
    private BulletManager bulletManager;
    private float bulletDamage = 100;
    private Sound lask;

    public Player(float x, float y, World world, BulletManager bulletManager) {
        super(new Texture(Gdx.files.internal("player_laev.png")));
        this.bulletManager = bulletManager;

        setSize(
                getTexture().getWidth() * GameInfo.SCALING,
                getTexture().getHeight() * GameInfo.SCALING
        );
        setOrigin(getWidth() / 2f, getHeight() / 2f);


        // positsiooni sisendi arvutus (keskpunkt) -> (nurgapunkt)
        setCenter(x, y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Playerile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(getWidth() * 0.15f, getHeight() * 0.45f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        box.dispose();

        thruster = new Sprite(new Texture("player_ship_1b_booster1_t.png"));
        thruster.setSize(
                getWidth() * GameInfo.SCALING * 4,
                0);
        thrusterRadius = getHeight() / 2f - 10 * GameInfo.SCALING;
        updateBooster();

        thrusterSound = Gdx.audio.newSound(Gdx.files.internal("thruster.mp3"));
        thrusterSoundId = thrusterSound.play(0);
        thrusterSound.setLooping(thrusterSoundId, true);

        forces = new Vector2();
        lask = Gdx.audio.newSound(Gdx.files.internal("lask.wav"));
    }

    // testimiseks väga lambine inputi jägimine
    public void inputs(TouchPad touchpad, int laius, int pikkus) {
        // playeri tulistamine
        // TODO: SIIN PROBLEEM MÄNGU RESIZIMISEL
        float lubatudX = 210*laius/GameInfo.WIDTH;
        float lubatudY = pikkus - 210*pikkus/GameInfo.HEIGHT;
        if (Gdx.input.justTouched() && (Gdx.input.getX()>lubatudX || Gdx.input.getY()<lubatudY)) {
            float xKuhu = Gdx.input.getX() - laius/2 + body.getPosition().x;
            float yKuhu = -(Gdx.input.getY() - pikkus/2) + body.getPosition().y;;
            lask.play(0.35f);
            bulletManager.playerShoot(body.getPosition().x, body.getPosition().y, xKuhu, yKuhu, bulletDamage);
        }
        // iseenesest me enam seda ei vaja, aga jätsin igaksjuhuks alles praegu, kui peaks tahtma
        // kusaltki meelde tuletada, kuidas me tegime input key polli
        /*if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            body.applyForceToCenter(
                    10 * GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }*/

        // touchpadi inputist saadud info põhjalt paneme playeri vastava vektori suunas liikuma
        Vector2 touchpadVector = new Vector2(
                touchpad.getTouchpad().getKnobPercentX(),
                touchpad.getTouchpad().getKnobPercentY()
        );
        body.applyForceToCenter(
                touchpadVector.x * GameInfo.FORCE_MULTIPLIER,
                touchpadVector.y * GameInfo.FORCE_MULTIPLIER,
                true
        );

        setBoosterPower(touchpadVector.len());

        if (touchpadVector.len() > 0) {
            setRotation(touchpadVector.angle() - 90);

            // paneb Playeri kehale ka uuesti suuna
            body.setTransform(
                    body.getPosition(),
                    (touchpadVector.angle() - 90) * MathUtils.degRad
            );
        }

        /*System.out.println(body.getLinearVelocity().len());*/
    }

    @Override
    public void draw(Batch batch) {
        thruster.draw(batch);
        super.draw(batch);
    }

    public void update() {
        body.applyForceToCenter(forces, true);

        // kontrollib kas player sõidab lubatust kiiremini
        // kui sõidab siis alandab kiirust
        float speedX = body.getLinearVelocity().x;
        float speedY = body.getLinearVelocity().y;
        float kordaja = tippkiirus*tippkiirus/(speedX*speedX+speedY*speedY);

        if (body.getLinearVelocity().len()>tippkiirus) {
            body.setLinearVelocity(speedX*kordaja, speedY*kordaja);
        }
        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        body.setAngularVelocity(0);
        setCenter(body.getPosition().x, body.getPosition().y);

        updateBooster();
        // väike shield regen
        if (shield<maxShield) shield += 1/30.0;
    }

    public void dispose() {
        // võtab spraidiga seotud assetid mälust maha
        thruster.getTexture().dispose();
        getTexture().dispose();
        thrusterSound.dispose();
        lask.dispose();
    }

    public void updateCam(OrthographicCamera camera) {
        camera.position.set(
                body.getPosition().x + body.getLinearVelocity().x / 12f * GameInfo.CAM_SCALING * 20,
                body.getPosition().y + body.getLinearVelocity().y / 12f * GameInfo.CAM_SCALING * 20,
                0
        );
    }

    private void setBoosterPower(float value) {
        thrusterSound.setVolume(thrusterSoundId, value * 0.3f);
        thruster.setSize(
                thruster.getWidth(),
                10 * value + MathUtils.random(-1, 1));
    }

    private void updateBooster() {
        thruster.setOrigin(
                thruster.getWidth() / 2f,
                thruster.getHeight());

        thruster.setOriginBasedPosition(
                body.getPosition().x - MathUtils.cosDeg(this.getRotation() + 90) * thrusterRadius,
                body.getPosition().y - MathUtils.sinDeg(this.getRotation() + 90) * thrusterRadius);

        thruster.setRotation(this.getRotation());
    }

    public void addForce(Vector2 force) {
        forces.add(force);
    }

    public void subForce(Vector2 force) {
        forces.sub(force);
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getShield() {
        return shield;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }

    public float getMaxShield() {
        return maxShield;
    }
}
