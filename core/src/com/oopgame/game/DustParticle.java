package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import helpers.GameInfo;

public class DustParticle extends Sprite {
    public DustParticle(Texture texture, Vector2 asukoht) {
        super(texture);
        setSize(0, GameInfo.CAM_SCALING);

        setOrigin(getWidth(), getHeight() / 2f);

        setPosition(asukoht.x, asukoht.y);
    }

    public void update(Vector2 vel) {
        setPosition(
                getX() - vel.x / GameInfo.PLAYER_MAXSPEED * 2f,
                getY() - vel.y / GameInfo.PLAYER_MAXSPEED * 2f
        );

        setSize(vel.len() / 9f, getHeight());
        setRotation(vel.angle());
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public void setPosition(Vector2 asukoht) {
        super.setPosition(asukoht.x, asukoht.y);
    }
}
