package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import helpers.GameInfo;

public class WaveManager {
    private EnemyManager enemyManager;

    private boolean waveActive = false;
    private long timeNextWave;
    private long timeWaitWave = 5000;

    private int waveNumber = 0;
    private int enemyAmount = 2;

    private int score;

    private BitmapFont font;
    private Label currentScore;
    private Label wave;


    public WaveManager(SpriteBatch batch, Player player, World world, Stage stage,
                       UIManager uiManager, BulletManager bulletManager,
                       MusicManager musicManager, ExplosionManager explosionManager,
                       GibsManager gibsManager) {
        this.timeNextWave = TimeUtils.millis() + timeWaitWave;

        enemyManager = new EnemyManager(batch, player, world,
                uiManager, bulletManager, musicManager, explosionManager, gibsManager);

        font = new BitmapFont();
        font.getData().setScale(2);

        currentScore = new Label("", new Label.LabelStyle(font, Color.WHITE));
        currentScore.setFontScale(2);
        currentScore.setAlignment(Align.topLeft);


        wave = new Label("", new Label.LabelStyle(font, Color.WHITE));
        wave.setFontScale(2);
        wave.setAlignment(Align.center);

        stage.addActor(currentScore);
        stage.addActor(wave);
        Gdx.input.setInputProcessor(stage);
    }

    public void update() {
        enemyManager.update();
        score += enemyManager.getNewPoints();
        enemyManager.resetPoints();

        long time = TimeUtils.millis();

        if (!waveActive) {
            if (timeNextWave < time) {
                waveActive = true;
                score = score + waveNumber++;

                for (int i = 0; i < enemyAmount; i++)
                    enemyManager.addEnemy();
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
                GameInfo.WIDTH * 0.5f - wave.getWidth() * 0.5f,
                GameInfo.HEIGHT - 40);
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
        return "" + (timeNextWave - TimeUtils.millis()) / 100 / 10f;
    }

    public int getScore() {
        return score;
    }

    public void dispose() {
        enemyManager.dispose();
        font.dispose();
    }
}
