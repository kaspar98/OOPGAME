package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import helpers.GameInfo;

public class MusicManager {
    private Music musicA;
    private Music musicB;

    private float actionDistance = GameInfo.CAM_SCALING * GameInfo.OUTER_RADIUS;
    private float calmDistance = actionDistance * 3;
    private float targetVolume = 0;
    // kui palju heli tugevust muuta sekundis;
    private float fadeTime = 1f;

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

    public void update(float delta) {
        if (musicA.getVolume() > targetVolume) {
            float change = fadeTime * delta;

            if (musicA.getVolume() - targetVolume > change)
                musicA.setVolume(musicA.getVolume() - change);
            else
                musicA.setVolume(targetVolume);

        } else {
            musicA.setVolume(targetVolume);
        }
    }

    private void setActionVolume(float volume) {
        if (volume > 1f)
            volume = 1f;
        else if (volume < 0)
            volume = 0;

        targetVolume = volume;
    }

    public void setClosestEnemyDistance(float distance) {
        System.out.println(distance);
        if (distance == -1 || distance > calmDistance)
            setActionVolume(0);
        else if (distance < actionDistance)
            setActionVolume(1f);
        else
            setActionVolume((calmDistance - distance) / (calmDistance - actionDistance));
    }

    public void dispose() {
        musicA.dispose();
        musicB.dispose();
    }
}
