package com.oopgame.game.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.oopgame.game.Time;
import com.oopgame.game.enemies.ships.MotherShip1;
import com.oopgame.game.vfx.VisualEffectsManager;

import helpers.GameInfo;

public class EnemyPlacer {
    private Vector2 pos = new Vector2();
    private Vector2 playerPos;

    private Time time;
    private long spawnTime;
    private int spawnInterval;

    private String carrierKey;

    private int[] enemyCounts = new int[1];
    // 0 : fastEnemies

    private EnemyManager enemyManager;
    private VisualEffectsManager vfxManager;


    public EnemyPlacer(float x, float y, Vector2 playerPos, String carrierKey,
                       Time time, int millisTillSpawn, int millisSpawnInterval,
                       int fastEnemies,
                       EnemyManager enemyManager, VisualEffectsManager vfxManager) {
        this.time = time;

        this.enemyManager = enemyManager;
        this.vfxManager = vfxManager;

        reconfigure(x, y, playerPos, carrierKey,
                millisTillSpawn, millisSpawnInterval,
                fastEnemies);
    }

    public void reconfigure(float x, float y, Vector2 playerPos, String carrierKey,
                            int millisTillSpawn, int millisSpawnInterval,
                            int fastEnemies) {
        pos.set(x, y);
        this.playerPos = playerPos;

        this.carrierKey = carrierKey;

        spawnTime = time.getTime() + millisTillSpawn;
        this.spawnInterval = millisSpawnInterval;

        enemyCounts[0] = fastEnemies;

        int framesTillSpawn = MathUtils.round(
                millisTillSpawn / (1000f / GameInfo.FPS));

        float midpoint = (framesTillSpawn > 0 ?
                (float) framesTillSpawn / (framesTillSpawn + 300) :
                0);

        vfxManager.addPortal(1, x, y, 10, framesTillSpawn + 300,
                midpoint, 0.5f);
    }

    public void update() {
        if (time.getTime() > spawnTime) {
            if (MotherShip1.keyType.equals(carrierKey))
                enemyManager.addMotherShip1(
                        pos.x, pos.y, new Vector2().set(playerPos).sub(pos).angle(),
                        spawnInterval, enemyCounts[0]);

            vfxManager.addBloom(2, pos.x, pos.y, 10,
                    Color.WHITE, 10, 0.5f, 0.5f, 0,
                    0, 0);

            enemyManager.poolEnemyPlacer(this);
        }
    }
}
