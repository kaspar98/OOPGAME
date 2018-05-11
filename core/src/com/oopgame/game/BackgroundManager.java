package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class BackgroundManager {
    private SpriteBatch batch;

    private OrthographicCamera camera;

    Array<Background> backgrounds = new Array<Background>();

    private Background grid;

    public BackgroundManager(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;

        this.camera = camera;

        grid = new Background("dev_grid1_t.png",
                GameInfo.WIDTH / 2f * GameInfo.SCALING,
                GameInfo.HEIGHT / 2f * GameInfo.SCALING
        );

        /*backgrounds.put("grid",
                new Background("dev_grid1_t.png",
                        GameInfo.HEIGHT / 2 * GameInfo.SCALING));*/
        /*backgrounds.put("nebula", new Background("bg_starfield_nebula_1a.png"));*/
    }

    public void update() {
        /*for (Background background : backgrounds.values())
            background.update(camera.position.x, camera.position.y);*/
        grid.update(camera.position.x, camera.position.y);
    }

    public void render() {
        /*for (Background background : backgrounds.values())
            background.draw(batch);*/
        grid.draw(batch);
    }

    public void dispose() {
        /*for (Background background : backgrounds.values())
            background.dispose();*/
        grid.dispose();
    }
}
