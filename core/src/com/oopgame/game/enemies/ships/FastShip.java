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

public class FastShip extends Sprite implements EnemyShip {
    public static String keyType = "fastShip";
    private static int points = 10;

    private Vector2 spawnPos = new Vector2();

    private Body body;
    private Fixture fixture;

    private float topSpeed = 25;

    private EnemyManager enemyManager;
    private UIManager uiManager;
    private GibsManager gibsManager;
    private VisualEffectsManager vfxManager;

    private UIMarker uiMarker;

    private EnemyAI ai;
    private String state = "started";

    private LaserGun laserGun;

    private static float maxHealth = 100;
    private float health = maxHealth;
    private static float maxShield = 25;
    private float shield = maxShield;

    private Integer faction = 1;

    private Time time;

    private boolean damaged;
    private long timeDamagedExpire;

    private Vector2 movementVector = new Vector2();
    private static float turnModifier = 5f;
    private static int maxSpeed = 40;

    private Vector2 spareVector = new Vector2();

    public FastShip(float x, float y, float angle, World world, Time time,
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

        reconfigure(x, y, angle, ai);

        body.setUserData(this);

        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(false);
        fixture.setUserData(this);

        this.laserGun = new LaserGun(damagerManager, body.getPosition(), faction);
    }

    public void reconfigure(float x, float y, float angle, EnemyAI ai) {
        spawnPos.set(x, y);

        body.setTransform(x, y, MathUtils.degreesToRadians * angle);

        this.ai = ai;

        if (uiMarker != null) {
            uiMarker.disable();
            uiMarker = null;
        }

        uiMarker = uiManager.addMarker(body.getPosition());
    }

    public void update() {
        spareVector.set(body.getLinearVelocity());

        if (spareVector.len() > topSpeed)
            body.setLinearVelocity(spareVector.setLength(topSpeed));

        laserGun.update();

        setCenter(body.getPosition().x, body.getPosition().y);
        setRotation(MathUtils.radiansToDegrees * body.getAngle());

        long time = this.time.getTime();

        // siin peaks AI küsitlus toimuma
        state = ai.getCommands(this, state);

        /*handleMovement();*/

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
        /*this.movementVector.add(movementVector);*/

        spareVector.set(movementVector);

        if (spareVector.len() > topSpeed)
            spareVector.setLength(topSpeed);

        body.applyForceToCenter(spareVector, true);

        turnTowards(spareVector.angle());
    }

    public void turnTowards(float targetAngle) {
        float current = body.getAngle() * MathUtils.radiansToDegrees;

        if (current - 1 <= targetAngle && targetAngle <= current + 1)
            body.setAngularVelocity(0);

        float difference = targetAngle - current;

        if (difference > 180)
            difference -= 360;
        else if (difference < -180)
            difference += 360;

        body.setAngularVelocity(MathUtils.degreesToRadians * turnModifier * difference);
    }

    @Override
    public void slowDown() {
        Vector2 current = body.getLinearVelocity();
        float length = current.len();

        // et lõpmatult aeglustama ei jääks
        if (length < 0.1)
            body.setLinearVelocity(0, 0);
        else {
            movement(spareVector.set(current).scl(-1).setLength(GameInfo.FORCE_MULTIPLIER * 0.25f));
            /*movementVector.add(current.cpy().scl(-1).setLength((length > 1 ? 1 : length)));*/
            /*movement(spareVector.set(current).setLength(maxSpeed).scl(-1));*/
            /*movement(spareVector.set(current).scl(-5));*/
        }
    }

    private void handleMovement() {
        // returnib kas body-il on sama angle, mis movementVectoril
        // TODO: pole otseselt vajalik vist isegi, aga saaks kraadidest radiaanidesse optimiseerida
        /*System.out.println(movementVector.angle() + ", " + movementVector.len());*/

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
                            .scl(GameInfo.PLAYER_ACCELERATION),
                    true);
        }

        Vector2 linearVelocity = body.getLinearVelocity();

        if (linearVelocity.len() > maxSpeed)
            body.setLinearVelocity(linearVelocity.setLength(maxSpeed));
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
        Vector2 pos = body.getPosition();
        float x = pos.x;
        float y = pos.y;

        gibsManager.createGibs(keyType, x, y, body.getLinearVelocity());

        vfxManager.addExplosion(2, x, y, 1, Color.WHITE);
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
