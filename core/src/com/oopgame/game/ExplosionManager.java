package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class ExplosionManager {
    private SpriteBatch batch;

    private Array<Sprite> frames = new Array<Sprite>();
    private Array<Explosion> explosions = new Array<Explosion>();
    private Array<Explosion> surnuaed = new Array<Explosion>();

    public ExplosionManager(SpriteBatch batch) {
        this.batch = batch;

        for (int i = 0; i < 11; i++) {
            Sprite frame = new Sprite(
                    new Texture(Gdx.files.internal("explosion_1_f" + i + ".png")));

            frame.setSize(
                    frame.getWidth() * GameInfo.SCALING * 3f,
                    frame.getHeight() * GameInfo.SCALING * 3f);

            frame.setOrigin(
                    frame.getWidth() * 0.5f,
                    frame.getHeight() * 0.5f);

            frames.add(frame);
        }
    }

    public void update() {
        for (Explosion explosion : explosions)
            explosion.update();
    }

    public void render() {
        for (Explosion explosion : explosions) {
            explosion.draw(batch);
        }
    }

    public void addExplosion(float x, float y) {
        if (surnuaed.size > 0) {
            Explosion explosion = surnuaed.pop();

            explosion.revive(x, y);

            explosions.add(explosion);
        } else {
            explosions.add(new Explosion(x, y, frames, this));
        }
    }

    public void removeExplosion(Explosion explosion) {
        explosions.removeValue(explosion, false);
        surnuaed.add(explosion);
    }

    public void dispose() {
        for (Sprite frame : frames)
            frame.getTexture().dispose();
    }
}
