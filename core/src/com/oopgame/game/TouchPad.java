package com.oopgame.game;
// KASUTATUD TUTORIAL http://www.bigerstaff.com/libgdx-touchpad-example/

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class TouchPad {
    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    public TouchPad(Texture bg, Texture knob) {
        // touchpadi tekstuurid
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", bg);
        touchpadSkin.add("touchKnob", knob);

        // joonistatav osa
        touchpadStyle = new Touchpad.TouchpadStyle();
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        touchpad = new Touchpad(5, touchpadStyle);
    }

    public Touchpad getTouchpad(){
        // et kasutada touchpad.getKnobPercentX jne
        return touchpad;
    }

    public void dispose(){
        touchpadSkin.dispose();
    }
}
