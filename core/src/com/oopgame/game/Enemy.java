package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
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
    private World world;
    private Body body;
    private Fixture fixture;

    private BulletManager bulletManager;
    private EnemyManager enemyManager;

    private Vector2 playerPos;
    private Vector2 playerVektor;

    private float health = 100;
    private float shield = 0;

    private int tippkiirus = 25;

    private float lasuCooldown = 2;
    private float viimatiTulistatud = 3;
    private float lasuDamage = 25;
    private float tulistamisKaugus = 55;

    private int scoreValue = 10;

    public Enemy(float x, float y,
                 World world, Texture texture,
                 Vector2 playerPos, Vector2 playerVektor,
                 BulletManager bulletManager,
                 EnemyManager enemyManager) {
        super(texture);
        this.world = world;
        this.bulletManager = bulletManager;
        this.playerPos = playerPos;
        this.playerVektor = playerVektor;
        this.enemyManager = enemyManager;

        setSize(
                getTexture().getWidth() * GameInfo.SCALING,
                getTexture().getHeight() * GameInfo.SCALING);

        setOrigin(getWidth() / 2f, getHeight() / 2f);

        setCenter(x, y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Enemy'le keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setRadius(getHeight() * 0.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(false);
        fixture.setUserData(this);

        circle.dispose();
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public float update() {
        float optKaugus = GameInfo.INNER_RADIUS * GameInfo.SCALING * 0.5f;


        // leiame vektori playeri poole et panna vaenlast õigele poole vaatama
        Vector2 playeriPoole = vektorPlayerist().scl(-1);
        Vector2 vaheVektor = vektorPlayerist().add(playerVektor);

        if (optKaugus + 5 < playeriPoole.len()) {
            // jõud mida rakendada, et liikuda playerile lähemale
            body.applyForceToCenter(
                    playeriPoole.cpy().setLength(GameInfo.FORCE_MULTIPLIER), true);
        } else if (optKaugus - 5 > playeriPoole.len()) {
            // jõud mida rakendada, et tagasi õigele kaugusele jõuda,
            body.applyForceToCenter(
                    playeriPoole.cpy().setLength(GameInfo.FORCE_MULTIPLIER).scl(-1), true);
        }

        /*// võtame 8 punkti playeri ümbert ning leiame neist vaenlase keskpunktile lähima mille poole vaenlane liigub
        // vaenlase liikumist saaks oluliselt parandada kui punkte võtta rohkem (või teha teine süsteem liikumiseks)
        float x, y = playerPos.x + playerVektor.x, playerPos.y + playerVektor.y;
        Vector2 punkt1 = new Vector2(x + 35 - getX(), y + 12 - getY());
        Vector2 punkt2 = new Vector2(x + 35 - getX(), y - 12 - getY());
        Vector2 punkt3 = new Vector2(x - 35 - getX(), y + 12 - getY());
        Vector2 punkt4 = new Vector2(x - 35 - getX(), y - 12 - getY());
        Vector2 punkt5 = new Vector2(x + 22 - getX(), y + 22 - getY());
        Vector2 punkt6 = new Vector2(x + 22 - getX(), y - 22 - getY());
        Vector2 punkt7 = new Vector2(x - 22 - getX(), y + 22 - getY());
        Vector2 punkt8 = new Vector2(x - 22 - getX(), y - 22 - getY());
        List<Vector2> punktid = Arrays.asList(punkt1, punkt2, punkt3, punkt4, punkt5, punkt6, punkt7, punkt8);
        Vector2 vähim = punkt1;
        for (Vector2 p : punktid) {
            if (Float.compare(vähim.len(), p.len()) > 0) vähim = p;
        }
        vähim = new Vector2(vähim.x * GameInfo.FORCE_MULTIPLIER / 3f, vähim.y * GameInfo.FORCE_MULTIPLIER / 3f);

        body.applyForceToCenter(vähim, true);*/

        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        body.setAngularVelocity(0);
        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(playeriPoole.angle());

        // kontrollib kas enemy sõidab lubatust kiiremini, kui sõidab siis alandab kiirust
        if (body.getLinearVelocity().len() > tippkiirus) {
            body.setLinearVelocity(body.getLinearVelocity().setLength(tippkiirus));
        }

        // tulistamise handlemine
        double kaugus = playeriPoole.len();
        if (kaugus < tulistamisKaugus && viimatiTulistatud > lasuCooldown) {
            viimatiTulistatud = 0;
            bulletManager.enemyShoot(
                    body.getPosition(),
                    new Vector2(
                            playerPos.x + playerVektor.x * 0.5f,
                            playerPos.y + playerVektor.y * 0.5f),
                    lasuDamage);
        }
        viimatiTulistatud += 1 / 20.0;

        return vaheVektor.len();
    }

    private Vector2 vektorPlayerist() {
        return new Vector2(
                body.getPosition().x - playerPos.x,
                body.getPosition().y - playerPos.y);
    }

    public Body getBody() {
        return body;
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

    public void die() {
        enemyManager.removeEnemy(this);
    }

    public void ärata(Vector2 pos) {
        setHealth(100);
        setShield(100);
        setCenter(pos.x, pos.y);
    }

    public static Vector2 suvalineAsukoht() {
        float xKoord = -20;
        float yKoord = -20;

        if (MathUtils.random(2) == 0)
            xKoord = GameInfo.W_WIDTH + 20;

        if (MathUtils.random(2) == 0)
            yKoord = GameInfo.W_HEIGHT + 20;

        return new Vector2(
                MathUtils.random(xKoord),
                MathUtils.random(yKoord));
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean isKill() {
        if (health <= 0) {
            body.setLinearVelocity(0,0);
            body.setTransform(-GameInfo.W_WIDTH, 0, 0);
            die();
            return true;
        }
        return false;
    }
}
