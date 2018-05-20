package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import helpers.GameInfo;

public class GibsManager {
    private World world;

    private ObjectMap<String, Array<Sprite>> appearances =
            new ObjectMap<String, Array<Sprite>>();

    public GibsManager(World world) {
        this.world = world;

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
    }

    public Array<Gibs> createGibs(String key) {
        Array<Gibs> gibs = new Array<Gibs>();

        for (Sprite sprite : appearances.get(key))
            gibs.add(new Gibs(sprite, world));

        return gibs;
    }

    public void dispose() {
        for (Array<Sprite> sprites : appearances.values())
            for (Sprite sprite : sprites)
                sprite.getTexture().dispose();
    }
}
