package com.oopgame.game.guns.damagers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.oopgame.game.Hittable;
import com.oopgame.game.Time;

import java.util.ArrayList;
import java.util.List;

import helpers.GameInfo;

public class LaserBeam implements DamagerRepeat {
    public static String keyType = "laserBeam";

    private List<Sprite> sprites = new ArrayList<Sprite>();
    private Color color = Color.WHITE;

    private Vector2 pos = new Vector2(1, 0);
    private float[] widths = new float[3];

    private Body body;
    private Fixture fixture;

    private Time time;

    private Vector2 bodyPos;

    private Integer damage;
    private Integer faction;

    private boolean shot;
    private boolean active;

    private List<Hittable> toDamage = new ArrayList<Hittable>();

    private DamagerManager damagerManager;

    public LaserBeam(Vector2 source, float angle,
                     World world, Time time, Integer damage, Integer faction,
                     BodyDef bodyDef, Shape shape,
                     List<Sprite> graphics, DamagerManager damagerManager) {

        this.damagerManager = damagerManager;
        this.time = time;

        for (int i = 0; i < graphics.size(); i++) {
            Sprite sprite = graphics.get(i);

            sprite.setOrigin(0, sprite.getHeight() * 0.5f);



            widths[i + 1] = widths[i] + sprite.getWidth();

            sprites.add(sprite);
        }

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setBullet(true);

        fixture = body.createFixture(shape, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);

        reconfigure(source, angle, damage, faction);
    }

    public void reconfigure(Vector2 source, float angle,
                            Integer damage, Integer faction) {
        this.bodyPos = source;
        this.damage = damage;
        this.faction = faction;

        pos.setAngle(angle);

        for (Sprite sprite : sprites)
            sprite.setAlpha(1);

        setPos(angle);

        body.setActive(true);

        shot = true;
        active = true;
    }

    public void setPos(float angle) {
        shot = true;

        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.get(i);

            sprite.setRotation(angle);

            pos.set(1, 0).setAngle(angle).setLength(widths[i]).add(bodyPos);
            sprite.setOriginBasedPosition(pos.x, pos.y);
        }

        pos.set(1, 0).setAngle(angle).setLength(widths[widths.length - 1] * 0.5f).add(bodyPos);
        body.setTransform(pos, angle * MathUtils.degreesToRadians);
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void update() {
        if (!shot && active) {
            active = false;
            damagerManager.poolDamager(this);
            return;
        } else if (shot) {
            shot = false;
        }

        for (Hittable hittable : toDamage) {
            hittable.isHit(this);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (Sprite sprite : sprites)
            sprite.draw(batch);
    }

    @Override
    public void deactivate() {
        for (Sprite sprite : sprites) {
            sprite.setAlpha(0);
        }

        body.setTransform(-GameInfo.W_WIDTH, 0, 0);
        body.setActive(false);
    }

    @Override
    public void hit() {
        /*damagerManager.impactEffect(this);*/
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
    public Body getBody() {
        return body;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public float getAngle() {
        return body.getAngle() * MathUtils.radiansToDegrees;
    }

    @Override
    public boolean didHit() {
        return false;
    }

    @Override
    public void addHittable(Hittable hittable) {
        toDamage.add(hittable);
    }

    @Override
    public void removeHittable(Hittable hittable) {
        toDamage.remove(hittable);
    }
}
