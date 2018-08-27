package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.oopgame.game.enemies.EnemyManager;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.ui.UIManager;

import helpers.GameInfo;

public class WaveManager {
    private EnemyManagerOld enemyManagerOld;

    private EnemyManager enemyManager;

    private Time time;

    private boolean waveActive = false;
    private long timeNextWave;
    private long timeWaitWave = 5000;

    private int waveNumber = 0;
    private int enemyAmount = 2;

    private int score;

    private Label currentScore;
    private Label wave;

    public WaveManager(SpriteBatch batch, Player player, World world, Stage stage,
                       UIManager uiManager, DamagerManager damagerManager,
                       MusicManager musicManager, ExplosionManager explosionManager,
                       GibsManager gibsManager, BitmapFont font, Time time) {
        this.time = time;
        this.timeNextWave = time.getTime() + timeWaitWave;

        enemyManagerOld = new EnemyManagerOld(batch, player, world,
                uiManager, damagerManager, musicManager, explosionManager, gibsManager);

        enemyManager = new EnemyManager(batch, world, time, player, damagerManager);


        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        currentScore = new Label("", style);
        currentScore.setAlignment(Align.left);

        wave = new Label("", style);
        wave.setAlignment(Align.center);

        stage.addActor(currentScore);
        stage.addActor(wave);
        Gdx.input.setInputProcessor(stage);
    }

    public void update() {
        enemyManagerOld.update();
        score += enemyManagerOld.getNewPoints();
        enemyManagerOld.resetPoints();

        enemyManager.update();

        long time = this.time.getTime();

        if (!waveActive) {
            if (timeNextWave < time) {
                waveActive = true;
                score = score + waveNumber++;

                for (int i = 0; i < enemyAmount; i++)
                    enemyManagerOld.addEnemy();

                enemyManager.addEnemy(2);
            }
        } else if (enemyManagerOld.getEnemyCount() == 0) {
            waveActive = false;
            timeNextWave = time + timeWaitWave;

            enemyAmount = enemyAmount * 2;
        }
    }

    public void render() {
        enemyManagerOld.render();

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
        enemyManagerOld.dispose();

        enemyManager.dispose();
    }
}
