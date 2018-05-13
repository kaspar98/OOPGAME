package com.oopgame.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import helpers.GameInfo;

public class Enemy extends Sprite {
    private Body body;
    private Fixture fixture;

    private float health = 100;
    private float maxHealth = 100;
    private float shield = 100;
    private float maxShield = 100;
    private int tippkiirus = 25;

    public Enemy(float x, float y, World world) {
        super(new Texture(Gdx.files.internal("enemy_alien_fighter_1b_t.png")));

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
    }

    public void dispose() {
        // võtab spraidiga seotud assetid mälust maha
        getTexture().dispose();
    }
}
