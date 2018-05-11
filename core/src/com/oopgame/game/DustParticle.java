package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import helpers.GameInfo;

public class DustParticle extends Sprite {
    public DustParticle(Texture texture, Vector2 asukoht) {
        super(texture);
        setSize(0, GameInfo.SCALING);

        setOrigin(getWidth(), getHeight() / 2f);

        setPosition(asukoht.x, asukoht.y);
    }

    public void update(Vector2 vel) {
        setPosition(
                getX() - vel.x / 120,
                getY() - vel.y / 120
        );

        setSize(vel.len() / 12, getHeight());
        setRotation(vel.angle());
    }

    public void setPosition(Vector2 asukoht) {
        super.setPosition(asukoht.x, asukoht.y);
    }
}
