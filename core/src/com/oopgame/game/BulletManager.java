package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class BulletManager {
    private Sprite bulletSprite1;
    private Sprite bulletSprite2;

    private Batch batch;
    private World world;

    private Array<Bullet> bullets = new Array<Bullet>();
    private Array<Bullet> corpses = new Array<Bullet>();
    private Array<Bullet> surnuaed = new Array<Bullet>();

    private Sound laserSound;


    public BulletManager(SpriteBatch batch, World world) {
        this.batch = batch;
        this.world = world;


        bulletSprite1 = new Sprite(new Texture(Gdx.files.internal("laser_red.png")));

        bulletSprite1.setSize(
                bulletSprite1.getTexture().getWidth() * GameInfo.SCALING * 1.5f,
                bulletSprite1.getTexture().getHeight() * GameInfo.SCALING * 2f);

        bulletSprite1.setOrigin(
                bulletSprite1.getWidth() / 2f,
                bulletSprite1.getHeight() / 2f);


        bulletSprite2 = new Sprite(new Texture(Gdx.files.internal("laser_blue.png")));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("lask.wav"));

        bulletSprite2.setSize(
                bulletSprite2.getTexture().getWidth() * GameInfo.SCALING * 1.5f,
                bulletSprite2.getTexture().getHeight() * GameInfo.SCALING * 2f);

        bulletSprite2.setOrigin(
                bulletSprite2.getWidth() / 2f,
                bulletSprite2.getHeight() / 2f);
    }

    public void render() {
        for (Bullet b : bullets) {
            b.draw(batch);
        }
    }

    public void update() {
        for (Bullet b : bullets) {
            b.update();
        }
    }

    public void dispose() {
        for (Bullet bullet : bullets)
            world.destroyBody(bullet.getBody());

        for (Bullet bullet : surnuaed)
            world.destroyBody(bullet.getBody());

        bulletSprite1.getTexture().dispose();
        bulletSprite2.getTexture().dispose();
        laserSound.dispose();
    }

    public void enemyShoot(Vector2 algpunkt, Vector2 vektor, float damage) {
        if (surnuaed.size > 0) {
            Bullet bullet = surnuaed.pop();
            bullet.revive(algpunkt, vektor, damage, bulletSprite1, false);
            bullets.add(bullet);
        } else {
            bullets.add(new Bullet(algpunkt, vektor,
                    damage, bulletSprite1, world, this, false));
        }
    }

    public void playerShoot(Vector2 algpunkt, Vector2 vektor, float damage) {
        if (surnuaed.size > 0) {
            Bullet bullet = surnuaed.pop();
            bullet.revive(algpunkt, vektor, damage, bulletSprite2, true);
            bullets.add(bullet);
        } else {
            bullets.add(new Bullet(algpunkt, vektor,
                    damage, bulletSprite2, world, this, true));
        }
        laserSound.play(0.35f);
    }

    public void removeBullet(Bullet bullet) {
        bullets.removeValue(bullet, false);
        surnuaed.add(bullet);
    }

    public Array<Bullet> getCorpses() {
        return corpses;
    }
}
