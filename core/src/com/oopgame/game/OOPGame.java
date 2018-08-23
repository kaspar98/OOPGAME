package com.oopgame.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import scenes.MainMenuScreen;

public class OOPGame extends Game {
    private SpriteBatch batch;
    private BitmapFont font;

    private int highscore = 0;

    public void create() {
        batch = new SpriteBatch();

        // teeme valmis sobiva suurusega fonti
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/rational-integer/ratio___.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 64;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);

        generator.dispose();

        // highscore sisselugemine
        FileHandle handle = Gdx.files.local("highscore.txt");

        if (handle.exists())
            highscore = Integer.parseInt(handle.readString());
        else handle.writeString("0", false);

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

    public BitmapFont getFont() {
        return font;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}