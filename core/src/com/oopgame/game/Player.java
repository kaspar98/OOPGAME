package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Player {
    // viide OOPGame'ile, et SpriteBatch kätte saada ntks
    private OOPGame game;

    // playeri tekstuur ja tekstuuri ääred jne
    private Texture texture;
    public Rectangle bounds;

    private World world;
    private Body body;
    private Fixture fixture;

    // konstruktor x ja y alusel
    public Player(OOPGame game, float x, float y, World world) {
        this.game = game;

        texture = new Texture(Gdx.files.internal("player_laev.png"));

        // seda Rectangle'i ei pruugi meil varsti üldse vaja minna, mulle tundub
        bounds = new Rectangle(
                x - texture.getWidth() / 2f,
                y - texture.getHeight() / 2f,
                texture.getWidth(),
                texture.getHeight()
        );


        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Playerile keha
        body = world.createBody(bodyDef);
        body.setUserData(bounds);

        // esialgselt katsetan keha kuju ringina
        // hiljem peaks ta vist olema mingi ristkülik, mille sisse jääb ainult Playeri tekstuuril
        // nähtav kosmoselaeva põhiosa ning tiivad jääksid välja sellest
        // siis oleks Playeril rohkem "dodge'imise" ruumi, mingi lisa rahuldustunne :D
        CircleShape circle = new CircleShape();
        circle.setRadius(texture.getWidth() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        fixture = body.createFixture(fixtureDef);

        circle.dispose();
    }

    // testimiseks väga lambine inputi jälgimine
    public void inputs() {
        // iga nupuvajutus rakendab Playeri kehale jõudu antud vektori suunas
        // paremale
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            body.applyForceToCenter(
                    10* GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }

        // vasakule
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            body.applyForceToCenter(
                    -10* GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }

        // üles
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            body.applyForceToCenter(
                    0.0f,
                    10* GameInfo.FORCE_MULTIPLIER,
                    true
            );
        }

        // alla
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            body.applyForceToCenter(
                    0.0f,
                    -10* GameInfo.FORCE_MULTIPLIER,
                    true
            );
        }
    }

    // topib tekstuuri SpriteBatchile, x ja y boundsi järgi
    public void render(float delta) {
        game.batch.draw(texture, bounds.x, bounds.y);
        /*Vector2 asukoht = fixture.getBody().getPosition();
        game.batch.draw(texture, asukoht.x, asukoht.y);*/
    }

    public void dispose() {
        // võtab tekstuuri mälust maha
        texture.dispose();
    }
}
