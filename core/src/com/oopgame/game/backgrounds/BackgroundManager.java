package com.oopgame.game.backgrounds;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

import helpers.GameInfo;

public class BackgroundManager {
    private SpriteBatch batch;

    private Vector3 camPos;

    private Array<Background> backgrounds = new Array<Background>();

    private Array<Planet> planets = new Array<Planet>();

    public BackgroundManager(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camPos = camera.position;

        backgrounds.add(
                new Background(
                        ("bg_starfield_nebula_a" + MathUtils.random(1, 11) + "_t2.png"),
                        GameInfo.WIDTH / 2f * GameInfo.CAM_SCALING,
                        GameInfo.HEIGHT / 2f * GameInfo.CAM_SCALING,
                        camPos));

        for (int i = 1; i < 8; i++)
            planets.add(new Planet("planet_" + i + "b_t.png", camPos));

        planets.sort(new Comparator<Planet>() {
            @Override
            public int compare(Planet planet, Planet t1) {
                float para1 = planet.getParallaxConstant();
                float para2 = t1.getParallaxConstant();
                if (para1 < para2) return 1;
                else if (para1 > para2) return -1;
                return 0;
            }
        });
    }

    public void update() {
        for (Background background : backgrounds)
            background.update();

        for (Planet planet : planets)
            planet.update();
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

        for (Planet planet : planets)
            planet.dispose();
    }
}
