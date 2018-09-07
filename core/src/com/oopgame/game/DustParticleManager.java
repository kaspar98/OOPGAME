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

    private float spawnDistance = (GameInfo.OUTER_RADIUS + 16) * GameInfo.CAM_SCALING;
    private float maxDistance = spawnDistance + 32 * GameInfo.CAM_SCALING;


    public DustParticleManager(SpriteBatch batch, Player player) {
        this.batch = batch;

        this.pos = player.getPosition();
        this.vel = player.getLinearVelocity();

        tekstuur = new Texture(Gdx.files.internal("bgl_motiondust_1_t.png"));

        while (tolm.size < 60 * GameInfo.WIDTH / 1920f)
            tolm.add(new DustParticle(tekstuur,
                    uusAsukoht(true, true)));
    }

    public void update() {
        for (DustParticle tükk : tolm) {
            tükk.update(vel);

            if (tükk.getPosition().sub(pos).len() > maxDistance)
                tükk.setPosition(uusAsukoht(false, false));
        }
    }

    private Vector2 uusAsukoht(boolean suvalineKaugus, boolean suvalineNurk) {
        float suvaline = (suvalineNurk ?
                MathUtils.random(0, 360f) :
                MathUtils.random(-45f, 45f) + vel.angle());

        float d = (suvalineKaugus ?
                MathUtils.random(spawnDistance) :
                spawnDistance);

        return new Vector2(
                MathUtils.cosDeg(suvaline) * d + pos.x,
                MathUtils.sinDeg(suvaline) * d + pos.y);
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
