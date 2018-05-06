package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    // viide OOPGame'ile, et SpriteBatch kätte saada ntks
    private OOPGame game;

    // playeri tekstuur ja tekstuuri ääred jne
    private Texture texture;
    public Rectangle bounds;

    // konstruktor x ja y alusel
    public Player(OOPGame game, float x, float y) {
        this.game = game;

        texture = new Texture(Gdx.files.internal("player_laev.png"));
        bounds = new Rectangle(
                x - texture.getWidth() / 2f,
                y - texture.getHeight() / 2f,
                texture.getWidth(),
                texture.getHeight()
        );
    }

    // testimiseks väga lambine inputi jälgimine
    public void inputs() {
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            bounds.x += 200 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            bounds.x -= 200 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Keys.UP)) {
            bounds.y += 200 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            bounds.y -= 200 * Gdx.graphics.getDeltaTime();
        }
    }

    // topib tekstuuri SpriteBatchile, x ja y boundsi järgi
    public void render(float delta) {
        game.batch.draw(texture, bounds.x, bounds.y);
    }

    public void dispose() {
        // võtab tekstuuri mälust maha
        texture.dispose();
    }
}
