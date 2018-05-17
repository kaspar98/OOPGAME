package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import helpers.GameInfo;

public class MusicManager {
    private Music musicA;
    private Music musicB;

    private float actionDistance = GameInfo.CAM_SCALING * GameInfo.OUTER_RADIUS;
    private float calmDistance = actionDistance * 3;

    public MusicManager() {
        // action
        musicA = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_a1.mp3"));
        musicA.setVolume(0.1f);
        musicA.play();
        musicA.setLooping(false);
        musicA.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                float volume = musicB.getVolume();
                musicA = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_a2.mp3"));
                musicA.setVolume(volume);
                musicA.setLooping(true);
                musicA.play();
            }
        });

        // rahulikum
        musicB = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_b1.mp3"));
        musicB.play();
        musicB.setLooping(false);
        musicB.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                float volume = musicB.getVolume();
                musicB = Gdx.audio.newMusic(Gdx.files.internal("tha_mcis_b2.mp3"));
                musicB.setVolume(volume);
                musicB.setLooping(true);
                musicB.play();
            }
        });
    }

    public float getActionVolume() {
        return musicA.getVolume();
    }

    public void setActionVolume(float volume) {
        if (volume > 1f)
            volume = 1f;
        else if (volume < 0)
            volume = 0;

        musicA.setVolume(volume);
    }

    public void setClosestEnemyDistance(float distance) {
        if (distance < actionDistance)
            setActionVolume(1f);
        else if (distance > calmDistance)
            setActionVolume(0f);
        else
            setActionVolume((calmDistance - distance) / (calmDistance - actionDistance));
    }

    public void dispose() {
        musicA.dispose();
        musicB.dispose();
    }
}
