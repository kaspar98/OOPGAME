package com.oopgame.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.oopgame.game.BulletManager;
import com.oopgame.game.ExplosionManager;
import com.oopgame.game.GibsManager;
import com.oopgame.game.MusicManager;
import com.oopgame.game.Player;
import com.oopgame.game.ui.UIManager;

import helpers.GameInfo;

public class EnemyManager {
    private SpriteBatch batch;
    private World world;
    private Sprite appearance;

    private Array<Enemy> alive = new Array<Enemy>();
    private Array<Enemy> graveyard = new Array<Enemy>();

    private Vector2 playerPos;
    private Vector2 playerVektor;

    private com.oopgame.game.ui.UIManager uiManager;
    private BulletManager bulletManager;
    private MusicManager musicManager;
    private ExplosionManager explosionManager;
    private GibsManager gibsManager;

    private int points;

    public EnemyManager(
            SpriteBatch batch, Player player, World world,
            UIManager uiManager, BulletManager bulletManager, MusicManager musicManager,
            ExplosionManager explosionManager, GibsManager gibsManager) {
        this.batch = batch;
        this.playerPos = player.getPosition();
        this.playerVektor = player.getVector();
        this.world = world;
        this.uiManager = uiManager;
        this.bulletManager = bulletManager;
        this.musicManager = musicManager;
        this.explosionManager = explosionManager;
        this.gibsManager = gibsManager;

        Texture texture = new Texture(
                Gdx.files.internal("enemy_alien_fighter_1b_t.png"));

        appearance = new Sprite(texture);

        appearance.setSize(
                texture.getWidth() * GameInfo.SCALING,
                texture.getHeight() * GameInfo.SCALING);

        appearance.setOrigin(
                appearance.getWidth() * 0.5f,
                appearance.getHeight() * 0.5f);
    }

    public void update() {
        float lähimKaugus = -1;

        for (Enemy e : alive) {
            float kaugus = e.update();

            if (kaugus != -1 && (kaugus < lähimKaugus || lähimKaugus == -1))
                lähimKaugus = kaugus;
        }

        musicManager.setClosestEnemyDistance(lähimKaugus);
    }

    public void render() {
        for (Enemy e : alive)
            e.draw(batch);
    }

    public void dispose() {
        for (Enemy e : alive)
            e.kill();

        appearance.getTexture().dispose();
    }

    public void addEnemy() {
        Enemy enemy;

        if (graveyard.size > 0) {
            enemy = graveyard.pop();

            enemy.revive();

            uiManager.reviveMarker(enemy.getMarker());
        } else {
            Vector2 suvaline = Enemy.uusAsukoht();

            enemy = new Enemy(
                    suvaline,
                    world, appearance, "enemy_alien_fighter_1b",
                    playerPos, playerVektor,
                    uiManager, bulletManager,
                    this, gibsManager);
        }
        alive.add(enemy);
    }

    public void killEnemy(Enemy e, float x, float y) {
        explosionManager.addExplosion(x, y);

        uiManager.removeMarker(e.getMarker());

        points += e.getScoreValue();

        alive.removeValue(e, false);
        graveyard.add(e);
    }

    public int getEnemyCount() {
        // waveManager kasutab seda.
        return alive.size;
    }

    public int getNewPoints() {
        // waveManager kasutab seda.
        return points;
    }

    public void resetPoints() {
        // waveManager kasutab seda.
        points = 0;
    }
}

