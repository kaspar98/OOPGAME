package com.oopgame.game.old;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.oopgame.game.ExplosionManager;
import com.oopgame.game.GibsManager;
import com.oopgame.game.MusicManager;
import com.oopgame.game.Player;
import com.oopgame.game.enemies.ships.FastShip;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.vfx.VisualEffectsManager;

import helpers.GameInfo;

public class EnemyManagerOld {
    private SpriteBatch batch;
    private World world;
    private Sprite appearance;

    private Array<EnemyOld> alive = new Array<EnemyOld>();
    private Array<EnemyOld> graveyard = new Array<EnemyOld>();

    private Vector2 playerPos;
    private Vector2 playerVektor;

    private UIManager uiManager;
    private DamagerManager damagerManager;
    private MusicManager musicManager;
    private ExplosionManager explosionManager;
    private VisualEffectsManager vfxManager;
    private GibsManager gibsManager;

    private int points;

    public EnemyManagerOld(
            SpriteBatch batch, Player player, World world,
            UIManager uiManager, DamagerManager damagerManager, MusicManager musicManager,
            ExplosionManager explosionManager, GibsManager gibsManager,
            VisualEffectsManager vfxManager) {
        this.batch = batch;
        this.playerPos = player.getPosition();
        this.playerVektor = player.getLinearVelocity();
        this.world = world;
        this.uiManager = uiManager;
        this.damagerManager = damagerManager;
        this.musicManager = musicManager;
        this.explosionManager = explosionManager;
        this.vfxManager = vfxManager;
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

        for (EnemyOld e : alive) {
            float kaugus = e.update();

            if (kaugus != -1 && (kaugus < lähimKaugus || lähimKaugus == -1))
                lähimKaugus = kaugus;
        }

        musicManager.setClosestEnemyDistance(lähimKaugus);
    }

    public void render() {
        for (EnemyOld e : alive)
            e.draw(batch);
    }

    public void dispose() {
        for (EnemyOld e : alive)
            e.kill();

        appearance.getTexture().dispose();
    }

    public void addEnemy() {
        EnemyOld enemy;

        if (graveyard.size > 0) {
            enemy = graveyard.pop();

            enemy.revive();

            uiManager.reviveMarker(enemy.getMarker());
        } else {
            Vector2 suvaline = EnemyOld.uusAsukoht();

            enemy = new EnemyOld(
                    suvaline,
                    world, appearance, FastShip.keyType,
                    playerPos, playerVektor,
                    uiManager, damagerManager,
                    this, gibsManager);
        }
        alive.add(enemy);
    }

    public void killEnemy(EnemyOld e, float x, float y) {
        explosionManager.addExplosion(x, y);
        vfxManager.addExplosion(2, x, y, 1, new Color(1, 0.8f, 0.8f, 1)/*Color.WHITE*/);

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

