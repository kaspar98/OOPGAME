package com.oopgame.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class DustParticle extends Sprite {
    public DustParticle(Texture texture, Vector2 asukoht) {
        super(texture);
        setPosition(asukoht.x, asukoht.y);
    }

    public void setPosition(Vector2 asukoht) {
        super.setPosition(asukoht.x, asukoht.y);
    }
}
