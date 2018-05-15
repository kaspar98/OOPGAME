package com.oopgame.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import helpers.GameInfo;

public class Enemy extends Sprite {
    private Body body;
    private Fixture fixture;
    private BulletManager bulletManager;
    private Vector2 playerPos;

    private float health = 100;
    private float maxHealth = 100;
    private float shield = 100;
    private float maxShield = 100;
    private int tippkiirus = 25;
    private float lasuCooldown = 2;
    private float viimatiTulistatud = 3;
    private float lasuDamage = 1;
    private float tulistamisKaugus = 55;

    public Enemy(float x, float y, World world, Texture texture, BulletManager bulletManager, Vector2 playerPos) {
        super(texture);
        this.bulletManager = bulletManager;
        this.playerPos = playerPos;

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

        // loome Enemyle keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setRadius(getHeight()/2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);
        fixture.setUserData(this);

        circle.dispose();
    }
    public void draw(Batch batch) {
        super.draw(batch);
    }

    // muutujad x ja y on Playeri keskpunkti koordinaadid
    public void update(float x, float y) {
        // võtame 4 punkti playeri ümbert ning leiame neist vaenlase keskpunktile lähima mille poole vaenlane liigub
        // vaenlase liikumist saaks oluliselt parandada kui punkte võtta rohkem (või teha teine süsteem liikumiseks)
        Vector2 punkt1 = new Vector2(x+35-getX(), y+12-getY());
        Vector2 punkt2 = new Vector2(x+35-getX(), y-12-getY());
        Vector2 punkt3 = new Vector2(x-35-getX(), y+12-getY());
        Vector2 punkt4 = new Vector2(x-35-getX(), y-12-getY());
        List<Vector2> punktid = Arrays.asList(punkt1, punkt2, punkt3, punkt4);
        Vector2 vähim = punkt1;
        for (Vector2 p:punktid) {
            if (Float.compare(vähim.len(), p.len())>0) vähim = p;
        }
        vähim = new Vector2(vähim.x * GameInfo.FORCE_MULTIPLIER/3, vähim.y * GameInfo.FORCE_MULTIPLIER/3);

        body.applyForceToCenter(vähim, true);

        // leiame vektori playeri poole et panna vaenlast õigele poole vaatama
        Vector2 playeriPoole = new Vector2(x-getX(), y-getY());

        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        body.setAngularVelocity(0);
        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(playeriPoole.angle()+90);

        // kontrollib kas enemy sõidab lubatust kiiremini
        // kui sõidab siis alandab kiirust
        float speedX = body.getLinearVelocity().x;
        float speedY = body.getLinearVelocity().y;
        float kordaja = tippkiirus*tippkiirus/(speedX*speedX+speedY*speedY);
        if (body.getLinearVelocity().len()>tippkiirus) {
            body.setLinearVelocity(speedX*kordaja, speedY*kordaja);
        }

        // tulistamise handlemine
        double kaugus = playeriPoole.len();
        if (kaugus < tulistamisKaugus && viimatiTulistatud>lasuCooldown) {
            viimatiTulistatud = 0;
            bulletManager.enemyShoot(body.getPosition().x, body.getPosition().y, playerPos.x, playerPos.y, lasuDamage);
        }
        viimatiTulistatud += 1/20.0;
    }

    public void dispose() {
        // võtab spraidiga seotud assetid mälust maha
        getTexture().dispose();
    }

    public Body getBody() {
        return body;
    }

    // tagastab distantsi etteantud punktist
    public float getDistance(float x, float y){
        return new Vector2(x-getX(), y-getY()).len();
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getShield() {
        return shield;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }
}
