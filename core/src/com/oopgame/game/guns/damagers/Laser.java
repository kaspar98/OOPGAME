package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Laser extends Sprite implements Damager {
    private DamagerManager damagerManager;

    private Body body;
    private Fixture fixture;

    private Vector2 bodyPos;

    private Integer damage;
    private Integer faction;

    private Vector2 movementVector;


    public Laser(DamagerManager damagerManager, World world, Sprite sprite,
                 Integer damage, Integer faction,
                 Vector2 source,
                 Float speed, float angle,
                 BodyDef bodyDef, Shape shape) {
        super(sprite);

        this.damagerManager = damagerManager;
        this.damage = damage;
        this.faction = faction;

        // TODO: Äkki annab seda ka optimiseerida: delegeerib kõik eelneva body loomisele

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setBullet(true);

        fixture = body.createFixture(shape, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);

        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        setupLocation(source, speed, angle);

        setFactionColor();
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
    public void hit() {
        // TODO: tabamise efektid!!! PLAHVATUSED!!!

        damagerManager.poolLaser(this);
    }

    private void setupLocation(Vector2 source, Float speed, float angle) {
        movementVector = new Vector2(speed, 0).setAngle(angle);

        body.setLinearVelocity(movementVector.cpy().setLength(GameInfo.FORCE_MULTIPLIER * 1000));

        setRotation(movementVector.angle());
        body.setTransform(source, movementVector.angleRad());

        bodyPos = body.getPosition();

        setCenter(bodyPos.x, bodyPos.y);
    }

    public void reset(Integer damage, Integer faction, Vector2 source, Float speed, float angle) {
        this.damage = damage;
        this.faction = faction;

        setupLocation(source, speed, angle);

        body.setActive(true);
        setAlpha(1);
    }

    public void deactivate() {
        body.setLinearVelocity(0, 0);
        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);

        setAlpha(0);
    }
}
