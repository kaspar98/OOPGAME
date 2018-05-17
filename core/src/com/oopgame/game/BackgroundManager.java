package com.oopgame.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class BackgroundManager {
    private SpriteBatch batch;

    private OrthographicCamera camera;

    private Array<Background> backgrounds = new Array<Background>();

    private Array<Planet> planets = new Array<Planet>();

    public BackgroundManager(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;

        this.camera = camera;

        backgrounds.add(
                new Background(
                        ("bg_starfield_nebula_a" + MathUtils.random(1, 11) + "_t2.png"),
                        GameInfo.WIDTH / 2f * GameInfo.CAM_SCALING,
                        GameInfo.HEIGHT / 2f * GameInfo.CAM_SCALING));

        /*Background background = new Background("bg_starfield_nebula_1a_t2.png", 0);
        background.parallaxConstantY = background.parallaxConstantX;
        background.dx = 1;
        background.dy = 0.5f;
        background.setRotation(45);

        backgrounds.add(background);*/

        /*backgrounds.add(new Background("dev_grid1_t.png",
                GameInfo.WIDTH / 2f * GameInfo.SCALING,
                GameInfo.HEIGHT / 2f * GameInfo.SCALING));*/

        for (int i = 1; i < 8; i++)
            planets.add(new Planet("planet_" + i + "b_t.png"));
    }

    public void update() {
        for (Background background : backgrounds)
            background.update(camera.position.x, camera.position.y);
        for (Planet planet : planets)
            planet.update(camera.position.x, camera.position.y);
    }

    public void render() {
        for (Background background : backgrounds)
            background.draw(batch);
        for (Planet planet : planets)
            planet.draw(batch);
    }

    public void dispose() {
        for (Background background : backgrounds)
            background.dispose();
    }
}
