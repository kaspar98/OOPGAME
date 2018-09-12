package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.oopgame.game.enemies.ships.FastShip;
import com.oopgame.game.enemies.ships.MotherShip1;

import helpers.GameInfo;

public class GibsManager {
    private World world;
    private SpriteBatch batch;

    private ObjectMap<String, Array<Sprite>> appearances =
            new ObjectMap<String, Array<Sprite>>();

    private ObjectMap<String, Array<GibsGroup>> alive = new ObjectMap<String, Array<GibsGroup>>();
    private ObjectMap<String, Array<GibsGroup>> dead = new ObjectMap<String, Array<GibsGroup>>();

    public GibsManager(World world, SpriteBatch batch) {
        this.world = world;
        this.batch = batch;

        // loeb vastase gibs'id sisse ja paneb neile õige suuruse
        String key = "enemy_alien_fighter_1b";

        if (!appearances.containsKey(key))
            appearances.put(key, new Array<Sprite>());

        for (int i = 1; i < 9; i++) {
            Sprite sprite = new Sprite(
                    new Texture(Gdx.files.internal(
                            "ships/" + key + "_gibs_" + i + "_t.png")));

            sprite.setSize(
                    sprite.getWidth() * GameInfo.SCALING,
                    sprite.getHeight() * GameInfo.SCALING);

            appearances.get(key).add(sprite);
        }

        key = "enemy_alien_fighter_1";

        if (!appearances.containsKey(key))
            appearances.put(key, new Array<Sprite>());

        for (int i = 1; i < 9; i++) {
            Sprite sprite = new Sprite(
                    new Texture(Gdx.files.internal(
                            "ships/" + key + "_gibs_" + i + "_t.png")));

            sprite.setSize(
                    sprite.getWidth() * GameInfo.SCALING * 2,
                    sprite.getHeight() * GameInfo.SCALING * 2);

            appearances.get(key).add(sprite);
        }

        // loeb playeri gibs'id sisse ja paneb neile õige suuruse
        key = "player_ship_1b";

        if (!appearances.containsKey(key))
            appearances.put(key, new Array<Sprite>());

        for (int i = 1; i < 7; i++) {

            Sprite sprite = new Sprite(
                    new Texture(Gdx.files.internal(key + "_gibs_" + i + "_t.png")));

            sprite.setSize(
                    sprite.getWidth() * GameInfo.SCALING,
                    sprite.getHeight() * GameInfo.SCALING);

            appearances.get(key).add(sprite);
        }

        for (String appearance : appearances.keys()) {
            alive.put(appearance, new Array<GibsGroup>());
            dead.put(appearance, new Array<GibsGroup>());
        }
    }

    public void update() {
        for (String key : alive.keys()) {
            for (GibsGroup gibsGroup : alive.get(key)) {
                if (!gibsGroup.isAlive()) {
                    alive.get(key).removeValue(gibsGroup, false);
                    dead.get(key).add(gibsGroup);
                } else
                    gibsGroup.update();
            }
        }
    }

    public void render() {
        for (Array<GibsGroup> list : alive.values())
            for (GibsGroup gibsGroup : list)
                gibsGroup.draw();
    }

    public void createGibs(String key, float x, float y, Vector2 vektor) {
        if (FastShip.keyType.equals(key)) {
            key = "enemy_alien_fighter_1b";
        } else if (MotherShip1.keyType.equals(key)) {
            key = "enemy_alien_fighter_1";
        }

        if (dead.get(key).size == 0) {
            // teeme uued gibsid
            GibsGroup gibsGroup = new GibsGroup(appearances, key, batch, world, x, y, vektor);

            alive.get(key).add(gibsGroup);
        } else {
            GibsGroup gibsGroup = dead.get(key).pop();

            gibsGroup.start(x, y, vektor);

            alive.get(key).add(gibsGroup);
        }
    }

    public void dispose() {
        for (Array<Sprite> sprites : appearances.values())
            for (Sprite sprite : sprites)
                sprite.getTexture().dispose();
    }
}
