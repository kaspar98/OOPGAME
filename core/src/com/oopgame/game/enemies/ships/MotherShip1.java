package com.oopgame.game.enemies.ships;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.oopgame.game.GibsManager;
import com.oopgame.game.Time;
import com.oopgame.game.enemies.EnemyManager;
import com.oopgame.game.enemies.ai.EnemyAI;
import com.oopgame.game.guns.Gun;
import com.oopgame.game.guns.LaserGun;
import com.oopgame.game.guns.damagers.Damager;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.ui.UIMarker;
import com.oopgame.game.vfx.VisualEffectsManager;

import java.util.List;

import helpers.GameInfo;

public class MotherShip1 extends Sprite implements EnemyCarrier {
    public static String keyType = "motherShip1";
    private static int points = 100;

    private Vector2 spawnPos = new Vector2();

    private Vector2 pos;
    private Body body;
    private Fixture fixture;

    private float topSpeed = GameInfo.PLAYER_MAXSPEED;

    private EnemyManager enemyManager;
    private UIManager uiManager;
    private VisualEffectsManager vfxManager;
    private GibsManager gibsManager;

    private UIMarker uiMarker;

    private EnemyAI ai;
    private String state = "spawning";
    private boolean doneSpawning = false;

    private LaserGun laserGun;

    private static float maxHealth = 300;
    private float health = maxHealth;
    private static float maxShield = 200;
    private float shield = maxShield;

    private Integer faction = 1;

    private Time time;

    private boolean damaged;
    private long timeDamagedExpire;

    private int millisSpawnInterval;
    private long timeNextSpawn;
    private int[] enemies = new int[1];

    private Vector2 enemySpawnPos = new Vector2();

    private Vector2 movementVector = new Vector2();
    private static float turnModifier = 5f;
    private static int maxSpeed = 40;

    private Vector2 spareVector = new Vector2();

    public MotherShip1(float x, float y, float angle, World world,
                       Time time, int millisSpawnInterval,
                       int fastShips,
                       List<Sprite> graphics, BodyDef bodyDef, FixtureDef fixtureDef,
                       EnemyManager enemyManager, UIManager uiManager,
                       DamagerManager damagerManager, VisualEffectsManager vfxManager,
                       GibsManager gibsManager, EnemyAI ai) {
        super(graphics.get(0));

        this.time = time;
        this.enemyManager = enemyManager;
        this.uiManager = uiManager;
        this.vfxManager = vfxManager;
        this.gibsManager = gibsManager;

        body = world.createBody(bodyDef);

        reconfigure(x, y, angle, ai, millisSpawnInterval, fastShips);

        body.setUserData(this);

        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);
        fixture.setUserData(this);

        this.laserGun = new LaserGun(damagerManager, body.getPosition(), faction);
    }

    public void reconfigure(float x, float y, float angle, EnemyAI ai,
                            int millisSpawnInterval,
                            int fastShips) {

        spawnPos.set(x, y);

        body.setTransform(x, y, angle * MathUtils.degreesToRadians);
        this.pos = body.getPosition();

        this.ai = ai;

        this.millisSpawnInterval = millisSpawnInterval;
        this.timeNextSpawn = time.getTime() + millisSpawnInterval;

        enemies[0] = fastShips;
        doneSpawning = false;

        if (uiMarker != null) {
            uiMarker.disable();
            uiMarker = null;
        }

        uiMarker = uiManager.addMarker(body.getPosition());

        state = "spawning";
    }

    @Override
    public void update() {
        spareVector.set(body.getLinearVelocity());

        if (spareVector.len() > topSpeed)
            body.setLinearVelocity(spareVector.setLength(topSpeed));

        laserGun.update();

        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());

        long time = this.time.getTime();

        boolean enemySpawned = false;

        if (time > timeNextSpawn && !doneSpawning) {
            if (enemies[0] > 0) {
                float spawnAngle = body.getAngle() * MathUtils.radiansToDegrees +
                        (MathUtils.random() >= 0.5f ? 90 : -90);
                enemySpawnPos
                        .set(getWidth() * 0.5f * GameInfo.SCALING, 0)
                        .setAngle(spawnAngle)
                        .add(pos);


                vfxManager.addBloom(2, enemySpawnPos.x, enemySpawnPos.y, 10,
                        Color.WHITE, 10, 0.5f, 0.5f, 0,
                        0, 0);

                enemyManager.addFastShip(enemySpawnPos.x, enemySpawnPos.y, spawnAngle);

                enemies[0]--;
                enemySpawned = true;
            }

            if (enemySpawned)
                timeNextSpawn = time + millisSpawnInterval;
            else {
                state = "started";
                doneSpawning = true;
            }
        }

        state = ai.getCommands(this, state);

        if (damaged && time > timeDamagedExpire) {
            setColor(Color.WHITE);
            damaged = false;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public void movement(Vector2 movementVector) {

    }

    @Override
    public void turnTowards(float targetAngle) {
        float current = body.getAngle() * MathUtils.radiansToDegrees;

        float difference = targetAngle - current;

        if (difference > 180)
            difference -= 360;
        else if (difference < -180)
            difference += 360;

        body.setAngularVelocity(MathUtils.degreesToRadians * turnModifier * difference);
    }

    @Override
    public void slowDown() {

    }

    @Override
    public void shoot(float angle) {
        laserGun.shoot(angle, body.getLinearVelocity());
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    @Override
    public void deathGraphics() {
        gibsManager.createGibs(keyType, pos.x, pos.y, body.getLinearVelocity());

        vfxManager.addExplosion(
                2, pos.x, pos.y,
                4, Color.WHITE);
    }

    @Override
    public void deactivate() {
        body.setLinearVelocity(0, 0);
        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);

        setAlpha(0);

        uiMarker.disable();
        uiMarker = null;
    }

    @Override
    public void reset() {
        health = maxHealth;
        shield = maxShield;

        body.setActive(true);
        setAlpha(1);

        laserGun.resetAmmo();
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public Vector2 getSpawnPos() {
        return spawnPos;
    }

    @Override
    public Gun getGun() {
        return laserGun;
    }

    @Override
    public float getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public boolean isHit(Damager damager) {
        if (damager.getFaction() != 0)
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
        timeDamagedExpire = time.getTime() + GameInfo.ENEMY_DAMAGED_DURATION;
        damaged = true;

        if (shield < damage) {
            setColor(Color.RED);

            float overflow = damage - shield;

            shield = 0;
            health -= overflow;

            if (health <= 0) {
                enemyManager.poolEnemyShip(this);
            }
        } else {
            setColor(Color.YELLOW);
            shield -= damage;
        }
    }
}
