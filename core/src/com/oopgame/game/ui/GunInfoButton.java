package com.oopgame.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.oopgame.game.guns.Gun;

import java.util.HashMap;

public class GunInfoButton extends Image {
    private static Sprite back = new Sprite(new Texture(
            Gdx.files.internal("ui/gunButtons/gunButtonBack.png")));
    private static Sprite frame = new Sprite(new Texture(
            Gdx.files.internal("ui/gunButtons/gunButtonFrame.png")));

    private Image backImage = new Image(back);

    private float x;
    private float y;

    private Label.LabelStyle usableStyle;
    private Label.LabelStyle selectedStyle;
    private Label.LabelStyle emptyStyle;

    private Gun gun;

    private Stage stage;

    private boolean selected = false;

    private HashMap<String, Label> labels = new HashMap<String, Label>();

    public GunInfoButton(int number, Gun gun, Stage stage,
                         Label.LabelStyle usableStyle,
                         Label.LabelStyle selectedStyle,
                         Label.LabelStyle emptyStyle) {
        super(frame);

        stage.addActor(backImage);
        backImage.setColor(0, 0, 0, 0.5f);


        this.gun = gun;
        this.stage = stage;

        reconfigure(0, 0, number,
                usableStyle,
                selectedStyle,
                emptyStyle);
    }

    private void reconfigure(float x, float y, int number,
                             Label.LabelStyle usableStyle,
                             Label.LabelStyle selectedStyle,
                             Label.LabelStyle emptyStyle) {
        this.usableStyle = usableStyle;
        this.selectedStyle = selectedStyle;
        this.emptyStyle = emptyStyle;

        labels.put("name", new Label(gun.getName(), usableStyle));

        labels.put("number", new Label("" + number, usableStyle));

        labels.put("ammo", new Label("" + gun.ammoLeft(), usableStyle));

        labels.put("ammoMax", new Label("/" + gun.maxAmmo(), usableStyle));

        for (Label label : labels.values())
            stage.addActor(label);

        setCenter(x, y);
    }

    public void setCenter(float x, float y) {
        this.x = x;
        this.y = y;

        super.setPosition(x, y, Align.center);
        backImage.setPosition(x, y, Align.center);

        float frameWidth = frame.getWidth();
        float frameHeight = frame.getHeight();

        labels.get("name").setPosition(x + frameWidth * 0.425f, y, Align.bottomRight);
        labels.get("number").setPosition(x - frameWidth * 0.425f, y, Align.bottomLeft);
        labels.get("ammo").setPosition(
                x - frameWidth * 0.05f,
                y + frameHeight * 0.05f, Align.topRight);
        labels.get("ammoMax").setPosition(
                x - frameWidth * 0.05f,
                y + frameHeight * 0.05f, Align.topLeft);
    }

    public void update() {
        selected = gun.isSelected();

        int ammoLeft = gun.ammoLeft();
        labels.get("ammo").setText("" + ammoLeft);

        Label.LabelStyle style = usableStyle;

        if (gun.ammoLeft() == 0)
            style = emptyStyle;
        else if (selected)
            style = selectedStyle;

        for (Label label : labels.values())
            label.setStyle(style);

        super.setColor(style.fontColor);
    }

    public void dispose() {
        back.getTexture().dispose();
        frame.getTexture().dispose();
    }
}
