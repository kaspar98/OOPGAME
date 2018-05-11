package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class BackgroundManager {
    private SpriteBatch batch;

    private OrthographicCamera camera;

    Array<Background> backgrounds = new Array<Background>();

    public BackgroundManager(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;

        this.camera = camera;

        backgrounds.add(new Background("bg_starfield_nebula_1g_t2.png",
                GameInfo.WIDTH / 2f * GameInfo.SCALING,
                GameInfo.HEIGHT / 2f * GameInfo.SCALING));

        /*Background background = new Background("bg_starfield_nebula_1a_t2.png", 0);
        background.parallaxConstantY = background.parallaxConstantX;
        background.dx = 1;
        background.dy = 0.5f;
        background.setRotation(45);

        backgrounds.add(background);*/

        /*backgrounds.add(new Background("dev_grid1_t.png",
                GameInfo.WIDTH / 2f * GameInfo.SCALING,
                GameInfo.HEIGHT / 2f * GameInfo.SCALING));*/
    }

    public void update() {
        for (Background background : backgrounds)
            background.update(camera.position.x, camera.position.y);
    }

    public void render() {
        for (Background background : backgrounds)
            background.draw(batch);
    }

    public void dispose() {
        for (Background background : backgrounds)
            background.dispose();
    }
}
