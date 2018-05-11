package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import helpers.GameInfo;

public class Player implements DynamicBodied {
    // viide OOPGame'ile, et SpriteBatch kätte saada ntks
    private OOPGame game;

    // playeri tekstuur ja tekstuuri ääred jne
    private Texture texture;
    public Sprite sprite;

    private World world;
    public Body body;
    private Fixture fixture;

    // konstruktor x ja y alusel
    public Player(OOPGame game, float x, float y, World world) {
        this.game = game;

        texture = new Texture(Gdx.files.internal("player_laev.png"));
        // loome tekstuuriga tegeleva sprite'i

        sprite = new Sprite(texture);
        // positsiooni sisendi arvutus (keskpunkt) -> (nurgapunkt)
        sprite.setPosition(
                x - texture.getWidth() / 2f,
                y - texture.getHeight() / 2f
        );
        sprite.setScale(GameInfo.SCALING);


        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Playerile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        // esialgselt katsetan keha kuju ringina
        // hiljem peaks ta vist olema mingi ristkülik, mille sisse jääb ainult Playeri tekstuuril
        // nähtav kosmoselaeva põhiosa ning tiivad jääksid välja sellest
        // siis oleks Playeril rohkem "dodge'imise" ruumi, mingi lisa rahuldustunne :D
        CircleShape circle = new CircleShape();
        circle.setRadius(sprite.getWidth() * GameInfo.SCALING / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);

        circle.dispose();
    }

    // testimiseks väga lambine inputi jälgimine
    public void inputs(TouchPad touchpad) {
        // iga nupuvajutus rakendab Playeri kehale jõudu antud vektori suunas
        // paremale
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            body.applyForceToCenter(
                    10 * GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }

        // vasakule
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            body.applyForceToCenter(
                    -10 * GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }

        // üles
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            body.applyForceToCenter(
                    0.0f,
                    10 * GameInfo.FORCE_MULTIPLIER,
                    true
            );
        }

        // alla
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            body.applyForceToCenter(
                    0.0f,
                    -10 * GameInfo.FORCE_MULTIPLIER,
                    true
            );
        }

        // touchpadi inputist saadud info põhjalt paneme playeri vastava vektori suunas liikuma
        float touchpadX = touchpad.getTouchpad().getKnobPercentX();
        float touchpadY = touchpad.getTouchpad().getKnobPercentY();
        /*if (touchpadX!=0 && touchpadY!=0)
            body.setTransform(body.getPosition().x +touchpadX*3, body.getPosition().y+touchpadY*3,0);*/
        body.applyForceToCenter(
                touchpadX*GameInfo.FORCE_MULTIPLIER,
                touchpadY*GameInfo.FORCE_MULTIPLIER,
                true
        );
        // touchpadi inputist saadud info põhjal keerame playeri vaatama sinna kuhu ta parasjagu kiirendab
        // (kuna tekstuuri nina ei ole seal kus body nina asub lahutame 90 kraadi)
        // (kuna arctan annab vahemikus -90 kuni 90 kraadi peame tegutsema kahes osas)
        if (touchpadX < 0) {
            sprite.setRotation((float) Math.toDegrees(Math.atan(touchpadY / touchpadX))- 90 + 180);
        }
        if (touchpadX > 0) {
            sprite.setRotation((float) Math.toDegrees(Math.atan(touchpadY / touchpadX)) - 90);

        }

        System.out.println(body.getLinearVelocity().len());
    }

    // topib tekstuuri SpriteBatchile, x ja y boundsi järgi
    public void render(float delta) {
        // sprite tegeleb nüüd tekstuuri renderimisega ise
        sprite.draw(game.batch);
    }

    public void bodyUpdate() {
        // muudab sprite'i keskpunkti asukoht vastavalt keha asukohale
        sprite.setCenter(body.getPosition().x, body.getPosition().y);

        // muudab sprite'i suunda vastavalt keha suunale
        //sprite.setRotation(MathUtils.radiansToDegrees * (body.getAngle()));
    }

    public void dispose() {
        // võtab tekstuuri mälust maha
        texture.dispose();
    }
}
