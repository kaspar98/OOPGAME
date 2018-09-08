package com.oopgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private Map<String, BitmapFont> fontMap = new HashMap<String, BitmapFont>();

    private Map<String, FreeTypeFontGenerator> generatorMap =
            new HashMap<String, FreeTypeFontGenerator>();

    private OOPGame oopGame;

    public FontManager(OOPGame oopGame) {
        this.oopGame = oopGame;

        // teeme valmis sobiva suurusega fonti
        generatorMap.put("rational-integer", new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/rational-integer/ratio___.ttf")));
    }

    public void dispose() {
        for (BitmapFont font : fontMap.values())
            font.dispose();

        for (FreeTypeFontGenerator generator : generatorMap.values())
            generator.dispose();
    }

    public void generateFont(String generatorKey,
                             FreeTypeFontGenerator.FreeTypeFontParameter parameter,
                             String fontName) {
        if (!generatorMap.containsKey(generatorKey))
            throw new RuntimeException("sellist fonti pole sisse loetud!");

        BitmapFont font = generatorMap.get(generatorKey).generateFont(parameter);

        fontMap.put(fontName, font);
    }

    public BitmapFont getFont(String fontName) {
        return fontMap.get(fontName);
    }
}
