package com.oopgame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    private SpriteBatch batch;
    private Array<Enemy> vaenlased = new Array<Enemy>();
    private Vector2 pos;
    private World world;

    public EnemyManager(SpriteBatch batch, Player player, World world) {
        this.batch = batch;

        this.pos = player.body.getPosition();

        vaenlased.add(new Enemy(10, 10, world));
    }

    public void update(Player player) {
        pos = player.body.getPosition();
        for (Enemy e : vaenlased) {
            e.update(pos.x, pos.y);
        }
    }


    public void render() {
        for (Enemy e : vaenlased) {
            e.draw(batch);
        }
    }

    public void dispose() {
        for (Enemy e : vaenlased) {
            e.dispose();
        }
    }
}
