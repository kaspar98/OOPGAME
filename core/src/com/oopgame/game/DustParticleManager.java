package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import helpers.GameInfo;

public class DustParticleManager {
    private SpriteBatch batch;
    private Array<DustParticle> tolm = new Array<DustParticle>();
    private Texture tekstuur;

    // salvestan hoopis positsiooni ja velocity vektorid,
    // sest tuleb välja, et nende isendit ennast uuendatakse,
    // mitte ei asendata uue isendiga
    private Vector2 playerPos;
    private Vector2 playerVel;

    private float spawnDistance = (GameInfo.WIDTH / 2 + 16) * GameInfo.SCALING;
    private float maxDistance = spawnDistance + 16 * GameInfo.SCALING;


    public DustParticleManager(SpriteBatch batch, Player player) {
        this.batch = batch;

        this.playerPos = player.body.getPosition();
        this.playerVel = player.body.getLinearVelocity();

        tekstuur = new Texture(Gdx.files.internal("bgl_motiondust_1_t.png"));
    }

    public void update() {
        float x = playerPos.x;
        float y = playerPos.y;

        float dx = playerVel.x;
        float dy = playerVel.y;

        float suund = playerVel.angleRad();

        while (tolm.size < 10)
            tolm.add(new DustParticle(tekstuur, uusAsukoht(suund, x, y)));

        for (DustParticle tükk : tolm) {
            tükk.setPosition(tükk.getX() - dx / 10f, tükk.getY() - dy / 10f);
            if (new Vector2(
                    tükk.getX() - x,
                    tükk.getY() -y
            ).len() > maxDistance) {
                tükk.setPosition(uusAsukoht(suund, x, y));
            }
        }
    }

    private Vector2 uusAsukoht(float suund, float x, float y) {
        float suvaline = MathUtils.degreesToRadians * MathUtils.random(-45f, 45f) + suund;

        return new Vector2(
                MathUtils.cos(suvaline) * spawnDistance + x,
                MathUtils.sin(suvaline) * spawnDistance + y
        );
    }

    public void render() {
        for (DustParticle tükk : tolm) {
            tükk.draw(batch);
        }
    }

    public void dispose() {
        tekstuur.dispose();
    }
}
