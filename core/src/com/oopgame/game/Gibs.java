package com.oopgame.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

import helpers.GameInfo;

public class Gibs {
    private SpriteBatch batch;
    private Array<Gib> gibs = new Array<Gib>();
    private long timeStart;
    private boolean alive;

    public Gibs(ObjectMap<String, Array<Sprite>> appearances, String key,
                SpriteBatch batch, World world, float x, float y, Vector2 vektor) {
        for (Sprite sprite : appearances.get(key))
            gibs.add(new Gib(sprite, world));

        start(x, y, vektor);
    }

    public void start(float x, float y, Vector2 vektor) {
        timeStart = TimeUtils.millis();
        alive = true;

        for (Gib gib : gibs) {
            gib.start(x, y, vektor);
        }
    }

    public void update() {
        if (alive) {
            long time = TimeUtils.millis();

            // kui enemy surma saab, siis hakatakse hoopis gibs'e uuendama
            for (Gib gib : gibs)
                gib.update();

            // kui gibside aeg otsa saab, siis lõpetatakse enemy uuendamine
            if (timeStart + GameInfo.GIBS_DURATION < time) {
                for (Gib gib : gibs)
                    gib.stop();
            }
        }
    }

    public void draw() {
        long time = TimeUtils.millis();

        float visibleTime = GameInfo.GIBS_DURATION * 0.5f;

        float alpha = (time - timeStart > visibleTime ?
                1f - (time - timeStart - visibleTime) / (GameInfo.GIBS_DURATION - visibleTime) : 1);

        for (Gib gib : gibs)
            gib.draw(batch, alpha);
    }

    public boolean isAlive() {
        return alive;
    }
}
