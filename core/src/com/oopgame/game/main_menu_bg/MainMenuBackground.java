package com.oopgame.game.main_menu_bg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import helpers.GameInfo;

public class MainMenuBackground {
    private static float maxRadius = GameInfo.WIDTH;

    private static Sprite sprite = new Sprite(new Texture(
            Gdx.files.internal("bgl_motiondust_1_t.png")));

    private Vector2 center = new Vector2(GameInfo.WIDTH * 0.5f, GameInfo.HEIGHT * 0.5f);

    private SpriteBatch batch;

    private List<MainMenuParticle> particles = new ArrayList<MainMenuParticle>();

    public MainMenuBackground(SpriteBatch batch) {
        this.batch = batch;

        while (particles.size() < 40) {
            MainMenuParticle particle = new MainMenuParticle(sprite, center, maxRadius);

            particles.add(particle);
        }
    }

    public void render() {
        for (MainMenuParticle particle : particles)
            particle.draw(batch);
    }

    public void update() {
        for (MainMenuParticle particle : particles) {
            particle.update();

            if (particle.getDistance() > maxRadius)
                particle.reconfigure();
        }
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
