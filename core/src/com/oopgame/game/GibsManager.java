package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import helpers.GameInfo;

public class GibsManager {
    private World world;
    private SpriteBatch batch;

    private ObjectMap<String, Array<Sprite>> appearances =
            new ObjectMap<String, Array<Sprite>>();

    private ObjectMap<String, Array<Gibs>> alive = new ObjectMap<String, Array<Gibs>>();
    private ObjectMap<String, Array<Gibs>> dead = new ObjectMap<String, Array<Gibs>>();

    public GibsManager(World world, SpriteBatch batch) {
        this.world = world;
        this.batch = batch;

        // loeb vastase gibs'id sisse ja paneb neile õige suuruse
        String key = "enemy_alien_fighter_1b";

        if (!appearances.containsKey(key))
            appearances.put(key, new Array<Sprite>());

        for (int i = 1; i < 9; i++) {
            Sprite sprite = new Sprite(
                    new Texture(Gdx.files.internal(key + "_gibs_" + i + "_t.png")));

            sprite.setSize(
                    sprite.getWidth() * GameInfo.SCALING,
                    sprite.getHeight() * GameInfo.SCALING);

            appearances.get(key).add(sprite);
        }

        // loeb playeri gibs'id sisse ja paneb neile õige suuruse
        /*key = "player_ship_1b";

        if (!appearances.containsKey(key))
            appearances.put(key, new Array<Sprite>());

        for (int i = 1; i < 7; i++) {

            Sprite sprite = new Sprite(
                    new Texture(Gdx.files.internal(key + "_gibs_" + i + "_t.png")));

            sprite.setSize(
                    sprite.getWidth() * GameInfo.SCALING,
                    sprite.getHeight() * GameInfo.SCALING);

            appearances.get(key).add(sprite);
        }*/

        for (String appearance : appearances.keys()) {
            alive.put(appearance, new Array<Gibs>());
            dead.put(appearance, new Array<Gibs>());
        }
    }

    public void update() {
        for (String key : alive.keys()) {
            for (Gibs gibs : alive.get(key)) {
                if (!gibs.isAlive()) {
                    alive.get(key).removeValue(gibs, false);
                    dead.get(key).add(gibs);
                } else
                    gibs.update();
            }
        }
    }

    public void render() {
        for (Array<Gibs> list : alive.values())
            for (Gibs gibs : list)
                gibs.draw();
    }

    public void createGibs(String key, float x, float y, Vector2 vektor) {
        if (dead.get(key).size == 0) {
            // teeme uued gibsid
            Gibs gibs = new Gibs(appearances, key, batch, world, x, y, vektor);

            alive.get(key).add(gibs);
        } else {
            Gibs gibs = dead.get(key).pop();

            gibs.start(x, y, vektor);

            alive.get(key).add(gibs);
        }
    }

    public void dispose() {
        for (Array<Sprite> sprites : appearances.values())
            for (Sprite sprite : sprites)
                sprite.getTexture().dispose();
    }
}
