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
import com.oopgame.game.Time;
import com.oopgame.game.enemies.EnemyManager;
import com.oopgame.game.enemies.ai.EnemyAI;
import com.oopgame.game.guns.LaserGun;
import com.oopgame.game.guns.damagers.Damager;
import com.oopgame.game.guns.damagers.DamagerManager;

import helpers.GameInfo;

public class FastShip extends Sprite implements EnemyShip {
    private Body body;
    private Fixture fixture;

    private EnemyManager enemyManager;

    private EnemyAI ai;

    private LaserGun laserGun;

    private float health = 100;
    private float shield = 25;

    private Integer faction = 1;

    private Time time;

    private boolean damaged;
    private int damagedDuration = 200;
    private long timeDamagedExpire;

    public FastShip(Vector2 spawn, World world, Sprite sprite,
                    Time time,
                    BodyDef bodyDef, FixtureDef fixtureDef,
                    EnemyManager enemyManager, DamagerManager damagerManager) {
        super(sprite);

        this.time = time;
        this.enemyManager = enemyManager;


        body = world.createBody(bodyDef);
        body.setTransform(spawn, 0);
        body.setUserData(this);

        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(false);
        fixture.setUserData(this);

        this.laserGun = new LaserGun(damagerManager, body.getPosition(), faction);
    }

    public void update() {
        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());

        long time = this.time.getTime();

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
        // temporary
    }

    @Override
    public void shoot(float angle) {
        laserGun.shoot(angle);
    }

    @Override
    public void deactivate() {
        body.setLinearVelocity(0, 0);
        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);

        setAlpha(0);
    }

    @Override
    public void reset(Vector2 spawn) {
        health = 100;

        body.setTransform(spawn, 0);

        body.setActive(true);
        setAlpha(1);
    }

    @Override
    public boolean isHit(Damager damager) {
        if (damager.getFaction() != 0)
            return false;

        damage(damager.getDamage());
        damager.hit();

        return true;
    }

    private void damage(float damage) {
        timeDamagedExpire = time.getTime() + damagedDuration;
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
