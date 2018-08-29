package com.oopgame.game.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.oopgame.game.Player;

public class GameControls implements InputProcessor {
    // InputProcessor on nagu Listener pmst (vähemalt nii ma aru sain),
    // seega sellega peaks me saama performance'i natuke paremaks ja
    // peaks inputtide süsteemi veits lihtsustama

    private Player player;

    // siia tuleks veel booleanid ka erinevate keypresside jaoks,
    // selleks et teaks, kas nupp on veel all hoitud

    // väidetavalt on libGDX-il 155 input keyd
    private boolean[] keys = new boolean[155];

    // hiire nupud, neid peaks 5 olema
    private boolean[] buttons = new boolean[5];
    // samas androidil saab korraga vajutada mitu näppu , aga näpuvajutus on ikkagi
    // kõigest mouse1, seega peaks mouse1 jaoks eraldi tegema loendamise ja
    // teistele ns booleanid jätma?

    // vektor, mis antakse playerile ette, selle järgi pannakse player liikuma.
    private Vector2 movementVector = new Vector2(0, 0);

    public GameControls(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        // sündmused selleks, kui mõnda klaviatuuri nuppu vajutatakse
        keys[keycode] = true;

        if (keys[Input.Keys.NUM_1])
            player.getGunList().selectGun(0);

        if (keys[Input.Keys.NUM_2])
            player.getGunList().selectGun(1);

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // sündmused selleks, kui mõni klaviatuuri nupp lahti lastakse
        keys[keycode] = false;

        if (keycode == Input.Keys.ESCAPE)
            Gdx.app.exit();

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // loeb ainult nupuvajutust, sellega saaks mingit kirjutamise süsteemi teha,
        // vb teeb mingi commandide interpretatori mingi hetk, et paremini testida

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // sündmused selleks, kui hiirenuppe vajutatakse või ekraanile vajutatakse
        buttons[button] = true;

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // sündmused selleks, kui hiirenupp lahti lastakse või ekraanilt näpp eemaldatakse
        buttons[button] = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // sündmused selleks, kui hiire nuppu hoitakse all ja hiirega liigutakse ringi või
        // tiritakse näpuga mööda ekraani
        player.aimPointer(screenX, screenY);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // sündmused selleks, kui hiirt liigutatakse mööda ekraani ilma vajutamiseta
        player.aimPointer(screenX, screenY);

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // sündmused selleks, kui hiirt scrollitakse
        return false;
    }

    private void mousePress() {
        player.shoot();
    }

    public void holdPressed() {
        // liikumisega seotud nupud
        movementVector.set(0, 0);

        if (keys[Input.Keys.W])
            movementVector.add(0, 1);

        if (keys[Input.Keys.S])
            movementVector.add(0, -1);

        if (keys[Input.Keys.A])
            movementVector.add(-1, 0);

        if (keys[Input.Keys.D])
            movementVector.add(1, 0);

        player.movementVector(movementVector);

        // hiirega seotud nupud
        if (buttons[Input.Buttons.LEFT]) {
            mousePress();
        }
    }
}
