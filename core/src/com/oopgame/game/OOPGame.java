package com.oopgame.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import scenes.MainMenuScreen;

public class OOPGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    private int highscore = 0;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        // highscore sisselugemine
        FileHandle handle = Gdx.files.local("highscore.txt");

        if (handle.exists())
            highscore = Integer.parseInt(handle.readString());

        this.setScreen(new MainMenuScreen(this, highscore));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        getScreen().dispose();
        batch.dispose();
        font.dispose();
    }

}