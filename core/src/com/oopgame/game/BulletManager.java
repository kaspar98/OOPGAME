package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class BulletManager {
    private Texture bulletType1 = new Texture(
            Gdx.files.internal("laser_red.png"));
    private Texture bulletType2 = new Texture(
            Gdx.files.internal("laser_blue.png"));

    private Batch batch;
    private World world;

    private Array<Bullet> lasud = new Array<Bullet>();

    public BulletManager(SpriteBatch batch, World world) {
        this.batch = batch;
        this.world = world;
    }

    public void render() {
        for (Bullet b : lasud) {
            b.draw(batch);
        }
    }

    public void update() {
        for (Bullet b : lasud) {
            b.update();
        }
    }

    public void dispose() {
        bulletType1.dispose();
        bulletType2.dispose();
    }

    public void enemyShoot(Vector2 algpunkt,
                           Vector2 vektor,
                           float damage) {
        lasud.add(new Bullet(
                algpunkt,
                new Vector2(vektor.x - algpunkt.x, vektor.y - algpunkt.y),
                damage, bulletType1, world, this, false));
    }

    public void playerShoot(Vector2 algpunkt,
                            Vector2 vektor,
                            float damage) {
        lasud.add(new Bullet(
                algpunkt, vektor,
                damage, bulletType2, world, this, true));
    }

    public void removeBullet(Bullet bullet) {
        lasud.removeValue(bullet, false);
    }

    public Array<Bullet> getLasud() {
        return lasud;
    }
}
