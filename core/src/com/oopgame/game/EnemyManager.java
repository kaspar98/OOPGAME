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
    private World world;
    private Texture texture;

    private Array<Enemy> vaenlased = new Array<Enemy>();
    private Array<Enemy> surnuaed = new Array<Enemy>();

    private Vector2 playerPos;
    private Vector2 playerVektor;

    private UIManager uiManager;
    private BulletManager bulletManager;
    private MusicManager musicManager;

    public EnemyManager(
            SpriteBatch batch, Player player, World world,
            UIManager uiManager, BulletManager bulletManager, MusicManager musicManager) {
        this.batch = batch;
        this.playerPos = player.body.getPosition();
        this.playerVektor = player.body.getLinearVelocity();
        this.world = world;
        this.uiManager = uiManager;
        this.bulletManager = bulletManager;
        this.musicManager = musicManager;

        texture = new Texture(Gdx.files.internal("enemy_alien_fighter_1b_t.png"));

        /*addEnemy();*/
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
        for (Enemy e : vaenlased)
            e.die();

        texture.dispose();
    }

    public void addEnemy() {
        Vector2 suvaline = Enemy.suvalineAsukoht();
        Enemy enemy;

        if (surnuaed.size > 0) {
            enemy = surnuaed.pop();
            enemy.ärata(suvaline);
            vaenlased.add(enemy);
        } else {
            enemy = new Enemy(
                    suvaline.x, suvaline.y,
                    world, texture,
                    playerPos, playerVektor,
                    bulletManager,
                    this);

            vaenlased.add(enemy);
        }

        uiManager.addMarker(enemy.getBody().getPosition());
    }

    public BulletManager getBulletManager() {
        return bulletManager;
    }

    public void removeEnemy(Enemy e) {
        vaenlased.removeValue(e, false);
        surnuaed.add(e);
        uiManager.removeMarker(e.getBody().getPosition());
    }
}

