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

import helpers.GameInfo;

public class Player extends Sprite {
    Body body;
    Fixture fixture;

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

    public Player(float x, float y, World world) {
        super(new Texture(Gdx.files.internal("player_laev.png")));

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

        health = 100;
        shield = 100;
    }

    // testimiseks väga lambine inputi jälgimine
    public void inputs(TouchPad touchpad) {
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
    }

    public void dispose() {
        // võtab spraidiga seotud assetid mälust maha
        thruster.getTexture().dispose();
        getTexture().dispose();
        thrusterSound.dispose();
    }

    public void updateCam(OrthographicCamera camera) {
        camera.position.set(
                body.getPosition().x + body.getLinearVelocity().x / 24f,
                body.getPosition().y + body.getLinearVelocity().y / 24f,
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

    public float getMaxShield() {
        return maxShield;
    }
}
