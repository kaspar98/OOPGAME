package com.oopgame.game.inputs;

import com.badlogic.gdx.InputProcessor;

import scenes.MainMenuScreen;

public class MainMenuControls implements InputProcessor {
    private MainMenuScreen screen;

    public MainMenuControls(MainMenuScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        screen.proceed();

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screen.proceed();

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
