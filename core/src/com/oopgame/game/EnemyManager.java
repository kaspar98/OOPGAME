package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    private SpriteBatch batch;
    private Array<Enemy> vaenlased = new Array<Enemy>();
    private Vector2 pos;
    private World world;
    private Texture texture;
    private BulletManager bulletManager;

    private UIManager uiManager;

    public EnemyManager(SpriteBatch batch, Player player, World world, UIManager uiManager, BulletManager bulletManager) {
        this.batch = batch;
        this.pos = player.body.getPosition();
        this.world = world;
        this.uiManager = uiManager;
        this.bulletManager = bulletManager;
        texture = new Texture(Gdx.files.internal("enemy_alien_fighter_1b_t.png"));

        addEnemy();
    }

    public void update(Player player) {
        bulletManager.update();
        pos = player.body.getPosition();
        for (Enemy e : vaenlased) {
            e.update(pos.x, pos.y);
        }
    }

    public Array<Enemy> getVaenlased() {
        return vaenlased;
    }

    public void render() {
        bulletManager.render();
        for (Enemy e : vaenlased) {
            e.draw(batch);
        }
    }

    public void dispose() {
        for (Enemy e : vaenlased) {
            e.dispose();
        }
        bulletManager.dispose();
    }

    public void addEnemy() {
        Enemy enemy = new Enemy(10, 10, world, texture, bulletManager, pos, this);

        vaenlased.add(enemy);

        uiManager.addMarker(enemy.getBody().getPosition());
    }

    public BulletManager getBulletManager() {
        return bulletManager;
    }

    public void removeEnemy(Enemy e) {
        vaenlased.removeValue(e, false);
        uiManager.removeMarker(e.getBody().getPosition());
    }
}

