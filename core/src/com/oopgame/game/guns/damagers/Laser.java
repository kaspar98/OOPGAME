package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.oopgame.game.Time;

import java.util.List;

import helpers.GameInfo;

public class Laser extends Sprite implements Damager {
    public static String keyType = "laser";

    private DamagerManager damagerManager;

    private Body body;
    private Fixture fixture;

    private Time time;

    private final int duration = 2500;
    private final int fadeDuration = 1000;
    private long timeFade;
    private long timeDeactivate;

    private Vector2 bodyPos;

    private Integer damage;
    private Integer faction;

    private Vector2 spareVector = new Vector2();

    private boolean hit;


    public Laser(DamagerManager damagerManager, World world, List<Sprite> graphics, Time time,
                 Integer damage, Integer faction,
                 Vector2 source,
                 Float speed, float angle, Vector2 force,
                 BodyDef bodyDef, Shape shape) {
        super(graphics.get(0));

        this.damagerManager = damagerManager;
        this.time = time;

        // TODO: Äkki annab seda ka optimiseerida: delegeerib kõik eelneva body loomisele

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setBullet(true);

        fixture = body.createFixture(shape, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);

        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        reconfigure(damage, faction, source, speed, angle, force);
    }

    public void reconfigure(Integer damage, Integer faction,
                            Vector2 source, Float speed, float angle, Vector2 force) {
        this.damage = damage;
        this.faction = faction;

        setupLocation(source, speed, angle, force);

        body.setActive(true);
        setAlpha(1);

        setFactionColor();

        setupTimers();

        hit = false;
    }

    private void setupTimers() {
        timeFade = time.getTime() + duration;
        timeDeactivate = timeFade + fadeDuration;
    }

    private void setFactionColor() {
        switch (faction) {
            case 0:
                setColor(0.35f, 0.80f, 1, 1);
                break;
            case 1:
                setColor(1, 0, 0, 1);
        }
    }

    public void update() {
        setCenter(body.getPosition().x, body.getPosition().y);

        // tundub väga ebaoptimaalne atm,
        // sellise väikse asja jaoks teeb minuarust liiga palju arvutusi
        long time = this.time.getTime();

        if (time > timeFade) {
            if (time > timeDeactivate)
                damagerManager.poolDamager(this);
            else
                setAlpha((timeDeactivate - time) / (float) fadeDuration);
        }
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public int getFaction() {
        return faction;
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    @Override
    public void hit() {
        hit = true;

        damagerManager.impactEffect(this);

        damagerManager.poolDamager(this);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    private void setupLocation(Vector2 source, Float speed, float angle, Vector2 force) {
        spareVector.set(speed, 0).setAngle(angle);

        body.setTransform(source, spareVector.angleRad());
        setRotation(spareVector.angle());

        spareVector.scl(5);

        if (force != null)
            spareVector.add(force);

        body.setLinearVelocity(spareVector);

        bodyPos = body.getPosition();

        setCenter(bodyPos.x, bodyPos.y);
    }

    public void deactivate() {
        body.setLinearVelocity(0, 0);
        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);

        setAlpha(0);
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public float getAngle() {
        return body.getAngle() * MathUtils.radiansToDegrees;
    }

    @Override
    public boolean didHit() {
        return hit;
    }
}
