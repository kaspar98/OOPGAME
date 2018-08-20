package com.oopgame.game.enemies;

import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.TimeUtils;
import com.oopgame.game.BulletManager;
import com.oopgame.game.GibsManager;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.ui.UIMarker;

import helpers.GameInfo;

public class Enemy extends Sprite {
    private Body body;
    private Fixture fixture;

    private BulletManager bulletManager;
    private EnemyManager enemyManager;

    private Vector2 playerPos;
    private Vector2 playerVektor;

    private com.oopgame.game.ui.UIMarker uiMarker;
    private GibsManager gibsManager;

    private boolean alive;
    private float health = 100;
    private float shield = 25;

    private boolean damaged = false;
    private long timeDamagedExpire = 0;

    private int tippkiirus = 25;

    private long timeNextShot = 0;

    private float lasuDamage = 25;
    private float optKaugus = GameInfo.INNER_RADIUS * GameInfo.SCALING * 0.75f;
    private float tulistamisKaugus = optKaugus + 10;

    private int scoreValue = 10;

    private String gibsKey;

    public Enemy(Vector2 start,
                 World world, Sprite appearance, String gibsKey,
                 Vector2 playerPos, Vector2 playerVektor,
                 UIManager uiManager, BulletManager bulletManager,
                 EnemyManager enemyManager, GibsManager gibsManager) {
        super(appearance);

        this.bulletManager = bulletManager;
        this.playerPos = playerPos;
        this.playerVektor = playerVektor;
        this.enemyManager = enemyManager;
        this.gibsManager = gibsManager;
        this.gibsKey = gibsKey;

        setCenter(start.x, start.y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(start.x, start.y);

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

        uiMarker = uiManager.addMarker(body.getPosition());

        alive = true;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public float update() {
        if (!alive) {
            kill();
            return -1;
        }

        // tulistamise handlemine
        long time = TimeUtils.millis();

        // leiame vektori playeri poole et panna vaenlast õigele poole vaatama
        Vector2 playeriPoole = vektorPlayerist().scl(-1);
        Vector2 vaheVektor = vektorPlayerist().add(playerVektor);

        if (optKaugus + 5 < playeriPoole.len()) {
            // jõud mida rakendada, et liikuda playerile lähemale
            body.applyForceToCenter(
                    playeriPoole.cpy().setLength(GameInfo.FORCE_MULTIPLIER), true);
        } else if (optKaugus > playeriPoole.len()) {
            // jõud mida rakendada, kui playerile on piisavalt lähedal
            body.applyForceToCenter(
                    playerVektor.cpy().setLength(GameInfo.FORCE_MULTIPLIER * 0.9f), true);
        }

        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        body.setAngularVelocity(0);
        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(playeriPoole.angle());

        // kontrollib kas enemy sõidab lubatust kiiremini, kui sõidab siis alandab kiirust
        if (body.getLinearVelocity().len() > tippkiirus) {
            body.setLinearVelocity(body.getLinearVelocity().setLength(tippkiirus));
        }

        if (time > timeNextShot && playeriPoole.len() < tulistamisKaugus) {
            /*if (playeriPoole.len() > 1) {*/
            bulletManager.enemyShoot(
                    body.getPosition(),
                    playeriPoole.cpy().add(playerVektor
                            .cpy()
                            .sub(body.getLinearVelocity())
                            .scl(playeriPoole.len() > 1 ? 1f - 1 / playeriPoole.len() : 1f)),
                    lasuDamage);
            /*} else {
                bulletManager.enemyShoot(
                        body.getPosition(), playeriPoole, lasuDamage);
            }*/
            timeNextShot = time + GameInfo.ENEMY_SHOOTING_INTERVAL;
        }

        if (damaged && timeDamagedExpire < time) {
            setColor(Color.WHITE);
            damaged = false;
        }

        return vaheVektor.len();
    }

    private Vector2 vektorPlayerist() {
        return new Vector2(
                body.getPosition().x - playerPos.x,
                body.getPosition().y - playerPos.y);
    }

    public void kill() {
        float x = body.getPosition().x;
        float y = body.getPosition().y;

        gibsManager.createGibs(gibsKey, x, y, body.getLinearVelocity());

        enemyManager.killEnemy(this, x, y);

        body.setLinearVelocity(0, 0);
        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);
    }

    public void revive() {
        if (!alive) {
            Vector2 suvaline = uusAsukoht();
            health = 100;
            body.setActive(true);
            body.setTransform(suvaline, 0);

            alive = true;
        }
    }

    public static Vector2 uusAsukoht() {
        float radius = new Vector2(GameInfo.W_WIDTH, GameInfo.W_HEIGHT).len() * 0.5f;
        float angle = MathUtils.random(360f);

        return new Vector2(
                GameInfo.W_WIDTH * 0.5f + MathUtils.cosDeg(angle) * radius,
                GameInfo.W_HEIGHT * 0.5f + MathUtils.sinDeg(angle) * radius);
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public UIMarker getMarker() {
        return uiMarker;
    }

    public void damage(float damage) {
        timeDamagedExpire = TimeUtils.millis() + GameInfo.ENEMY_DAMAGED_DURATION;
        damaged = true;

        if (shield < damage) {
            setColor(Color.RED);

            float overflow = damage - shield;

            shield = 0;
            health -= overflow;

            if (health <= 0) {
                alive = false;
            }
        } else {
            setColor(Color.YELLOW);
            shield -= damage;
        }
    }
}
