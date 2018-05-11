package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

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

        // loome tekstuuriga tegeleva sprite'i ja seadistame ta vastavalt suurendusele
        sprite = new Sprite(texture);

        sprite.setSize(
                texture.getWidth() * GameInfo.SCALING,
                texture.getHeight() * GameInfo.SCALING
        );

        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);

        // positsiooni sisendi arvutus (keskpunkt) -> (nurgapunkt)
        sprite.setPosition(
                x - sprite.getWidth() / 2f,
                y - sprite.getHeight() / 2f
        );


        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // loome Playerile keha
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(sprite.getWidth() * 0.15f, sprite.getHeight() * 0.45f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixture = body.createFixture(fixtureDef);

        box.dispose();
    }

    // testimiseks väga lambine inputi jälgimine
    public void inputs(TouchPad touchpad) {
        // iseenesest me enam seda ei vaja, aga jätsin igaksjuhuks alles praegu, kui peaks tahtma
        // kusaltki meelde tuletada, kuidas me tegime input key polli
        /*if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            body.applyForceToCenter(
                    10 * GameInfo.FORCE_MULTIPLIER,
                    0.0f,
                    true
            );
        }*/

        // touchpadi inputist saadud info põhjalt paneme playeri vastava vektori suunas liikuma
        Vector2 touchpadVector = new Vector2(
                touchpad.getTouchpad().getKnobPercentX(),
                touchpad.getTouchpad().getKnobPercentY()
        );
        body.applyForceToCenter(
                touchpadVector.x * GameInfo.FORCE_MULTIPLIER,
                touchpadVector.y * GameInfo.FORCE_MULTIPLIER,
                true
        );
        /*float touchpadX = touchpad.getTouchpad().getKnobPercentX();
        float touchpadY = touchpad.getTouchpad().getKnobPercentY();
        *//*if (touchpadX!=0 && touchpadY!=0)
            body.setTransform(body.getPosition().x +touchpadX*3, body.getPosition().y+touchpadY*3,0);*//*
        body.applyForceToCenter(
                touchpadX * GameInfo.FORCE_MULTIPLIER,
                touchpadY * GameInfo.FORCE_MULTIPLIER,
                true
        );*/
        // touchpadi inputist saadud info põhjal keerame playeri vaatama sinna kuhu ta parasjagu kiirendab
        // (kuna tekstuuri nina ei ole seal kus body nina asub lahutame 90 kraadi)
        // (kuna arctan annab vahemikus -90 kuni 90 kraadi peame tegutsema kahes osas)
        /*if (touchpadX < 0) {
            sprite.setRotation((float) Math.toDegrees(Math.atan(touchpadY / touchpadX))- 90 + 180);
        }
        if (touchpadX > 0) {
            sprite.setRotation((float) Math.toDegrees(Math.atan(touchpadY / touchpadX)) - 90);
        }*/
        if (touchpadVector.len() > 0) {
            sprite.setRotation(touchpadVector.angle() - 90);
            body.setTransform(
                    body.getPosition(),
                    (touchpadVector.angle() - 90) * MathUtils.degRad
            );
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
    }

    public void dispose() {
        // võtab tekstuuri mälust maha
        texture.dispose();
    }
}
