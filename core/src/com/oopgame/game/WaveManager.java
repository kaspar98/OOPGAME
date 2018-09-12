package com.oopgame.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.oopgame.game.enemies.EnemyManager;
import com.oopgame.game.enemies.ships.EnemyShip;
import com.oopgame.game.enemies.ships.MotherShip1;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.vfx.VisualEffectsManager;

import helpers.GameInfo;

public class WaveManager {
    private EnemyManager enemyManager;

    private Time time;

    private boolean waveActive = false;
    private long timeNextWave;
    private int timeWaitWave = 5000;

    private int waveNumber = 0;
    private int enemyAmount = 2;

    private int score;

    private Label currentScore;
    private Label wave;

    private Vector2 spareVector = new Vector2();

    public WaveManager(SpriteBatch batch, Player player, World world, Stage stage,
                       UIManager uiManager, DamagerManager damagerManager,
                       MusicManager musicManager, ExplosionManager explosionManager,
                       GibsManager gibsManager, FontManager fontManager, Time time,
                       VisualEffectsManager vfxManager) {
        this.time = time;
        this.timeNextWave = time.getTime() + timeWaitWave;

        enemyManager = new EnemyManager(batch, world, time, player,
                uiManager, damagerManager, vfxManager, gibsManager, this, musicManager);


        Label.LabelStyle style = new Label.LabelStyle(fontManager.getFont("main"),
                Color.WHITE);

        currentScore = new Label("", style);
        currentScore.setAlignment(Align.left);

        wave = new Label("", style);
        wave.setAlignment(Align.center);

        stage.addActor(currentScore);
        stage.addActor(wave);
    }

    public void update() {
        enemyManager.update();

        long time = this.time.getTime();

        if (!waveActive) {
            if (timeNextWave < time) {
                waveActive = true;
                score = score + (waveNumber++ * 100);

                Vector2 spawnPoint = enemyManager.posNearPlayer(
                        GameInfo.OUTER_RADIUS * GameInfo.SCALING,
                        spareVector);

                enemyManager.addEnemyPlacer(spawnPoint.x, spawnPoint.y, MotherShip1.keyType,
                        5000, 5000, enemyAmount);
            }
        } else if (enemyManager.getEnemyCount() == 0) {
            waveActive = false;
            timeNextWave = time + timeWaitWave;

            enemyAmount = enemyAmount * 2;
        }
    }

    public void render() {
        enemyManager.render();

        currentScore.setPosition(10, GameInfo.HEIGHT - 40);
        currentScore.setText("" + score);

        wave.setText(getWaveInfo());
        wave.setPosition(
                GameInfo.WIDTH * 0.5f,
                GameInfo.HEIGHT - 40,
                Align.center);
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

    private String getWaitLeftText() {
        return "" + (timeNextWave - time.getTime()) / 100 / 10f;
    }

    public int getScore() {
        return score;
    }

    public void dispose() {
        enemyManager.dispose();
    }

    public void enemyKilled(EnemyShip ship) {
        score += ship.getPoints();
    }
}
