package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import helpers.GameInfo;

public class EnemyManager {
    private SpriteBatch batch;

    private Array<Enemy> vaenlased = new Array<Enemy>();
    private Vector2 playerPos;
    private World world;
    private Texture texture;
    // asukohtade genereerimiseks
    private Random r = new Random();

    private UIManager uiManager;
    private BulletManager bulletManager;
    private MusicManager musicManager;

    public EnemyManager(
            SpriteBatch batch, Player player, World world,
            UIManager uiManager, BulletManager bulletManager, MusicManager musicManager) {
        this.batch = batch;
        this.playerPos = player.body.getPosition();
        this.world = world;
        this.uiManager = uiManager;
        this.bulletManager = bulletManager;
        this.musicManager = musicManager;

        texture = new Texture(Gdx.files.internal("enemy_alien_fighter_1b_t.png"));

        addEnemy();
    }

    public void update() {
        float lähimKaugus = -1;

        for (Enemy e : vaenlased) {
            float kaugus = e.update();
            if (kaugus < lähimKaugus || lähimKaugus == -1)
                lähimKaugus = kaugus;
        }

        musicManager.setClosestEnemyDistance(lähimKaugus);
    }

    public Array<Enemy> getVaenlased() {
        return vaenlased;
    }

    public void render() {
        for (Enemy e : vaenlased) {
            e.draw(batch);
        }
    }

    public void dispose() {
        texture.dispose();

        for (Enemy e : vaenlased)
            e.die();
    }

    public void addEnemy() {
        float xKoord = -20;
        float yKoord = -20;

        if (r.nextInt(2) == 0)
            xKoord = GameInfo.W_WIDTH + 20;

        if (r.nextInt(2) == 0)
            yKoord = GameInfo.W_HEIGHT + 20;

        Enemy enemy = new Enemy(
                MathUtils.random(xKoord),
                MathUtils.random(yKoord),
                world,
                texture,
                playerPos,
                bulletManager,
                this);

        vaenlased.add(enemy);

        uiManager.addMarker(enemy.getBody().getPosition());
    }

    public BulletManager getBulletManager() {
        return bulletManager;
    }

    public void removeEnemy(Enemy e) {
        vaenlased.removeValue(e, false);
        uiManager.removeMarker(e.getBody().getPosition());
    }
}

