package com.oopgame.game;

import com.badlogic.gdx.utils.TimeUtils;

public class WaveManager {
    private EnemyManager enemyManager;

    private boolean waveActive = false;
    private long waveEnd;
    private long waveWait = 5000;

    private int waveNumber = 0;
    private int enemyAmount = 2;

    private int score;

    public WaveManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        this.waveEnd = TimeUtils.millis();
    }

    public void update() {
        long time = TimeUtils.millis();

        if (!waveActive) {
            if (waveEnd + waveWait < time) {
                waveActive = true;
                score = score + waveNumber++;

                for (int i = 0; i < enemyAmount; i++)
                    enemyManager.addEnemy();
            }
        } else if (enemyManager.getEnemyCount() == 0) {
            waveActive = false;
            waveEnd = time;

            enemyAmount = enemyAmount * 2;
        }
    }

    public String getWaveInfo() {
        if (!waveActive) {
            if (waveNumber == 0)
                return "Warmup";
            else
                return "Next wave begins in: " + getWaitLeftText() + "s";
        }

        return "Wave: " + waveNumber;
    }

    public String getScoreInfo() {
        return "" + score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    private String getWaitLeftText() {
        return "" + (waveEnd + waveWait - TimeUtils.millis()) / 100 / 10f;
    }

    public int getScore() {
        return score;
    }
}
