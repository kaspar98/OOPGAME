package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class BulletManager {
    private Texture bulletType1 = new Texture(Gdx.files.internal("test_bullet.png"));
    private Texture bulletType2 = new Texture(Gdx.files.internal("test_bullet2.png"));
    private Batch batch;
    private World world;
    private Array<Bullet> lasud = new Array<Bullet>();

    public BulletManager(SpriteBatch batch, World world){
        this.batch = batch;
        this.world = world;
    }

    public void render(){
        for (Bullet b:lasud) {
            b.draw(batch);
        }
    }

    public void update(){
        for (Bullet b:lasud) {
            b.update();
        }
    }

    public void dispose(){
        for (Bullet b:lasud) {
            b.dispose();
        }
    }

    public void enemyShoot(float xKust, float yKust, float xKuhu, float yKuhu, float damage) {
        lasud.add(new Bullet(xKust, yKust, xKuhu, yKuhu , damage, bulletType1, world, this, false));
    }

    public void playerShoot(float xKust, float yKust, float xKuhu, float yKuhu, float damage) {
        lasud.add(new Bullet(xKust, yKust, xKuhu, yKuhu, damage, bulletType2, world, this, true));
    }

    public void removeBullet(Bullet bullet) {
        lasud.removeValue(bullet, false);
    }
}
