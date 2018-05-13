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
    private Vector2 pos;
    private Vector2 vel;

    private float spawnDistance = (GameInfo.OUTER_RADIUS + 16) * GameInfo.SCALING;
    private float maxDistance = spawnDistance + 32 * GameInfo.SCALING;


    public DustParticleManager(SpriteBatch batch, Player player) {
        this.batch = batch;

        this.pos = player.body.getPosition();
        this.vel = player.body.getLinearVelocity();

        tekstuur = new Texture(Gdx.files.internal("bgl_motiondust_1_t.png"));

        while (tolm.size < 10)
            tolm.add(new DustParticle(tekstuur, uusAsukoht(true)));
    }

    public void update() {
        while (tolm.size < 10)
            tolm.add(new DustParticle(tekstuur, uusAsukoht(false)));

        for (DustParticle tükk : tolm) {
            tükk.update(vel);

            if (new Vector2(
                    tükk.getX() - pos.x,
                    tükk.getY() - pos.y
            ).len() > maxDistance) {
                tükk.setPosition(uusAsukoht(false));
            }
        }
    }

    private Vector2 uusAsukoht(boolean suvalineKaugus) {
        float suvaline = MathUtils.degreesToRadians * (MathUtils.random(-45f, 45f) + vel.angle());

        float d = (suvalineKaugus ? MathUtils.random(spawnDistance) : spawnDistance);

        return new Vector2(
                MathUtils.cos(suvaline) * d + pos.x,
                MathUtils.sin(suvaline) * d + pos.y
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
