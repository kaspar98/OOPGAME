package com.oopgame.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.oopgame.game.FontManager;
import com.oopgame.game.Player;
import com.oopgame.game.guns.Gun;

import java.util.ArrayList;
import java.util.List;

import helpers.GameInfo;

public class UIManager {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private Player player;

    private Sprite compass;
    private Sprite compass_marker_appearance;
    private Array<UIMarker> compass_markers = new Array<UIMarker>();

    private float dx = 105 * GameInfo.CAM_SCALING;

    private Texture back;

    private UIBar health;
    private Sprite health_back;
    private UIBar shield;
    private Sprite shield_back;

    private Image screenOverlay = new Image(new Sprite(new Texture(
            Gdx.files.internal("ui/screenOverlay.jpg"))));

    private List<GunInfoButton> gunButtons = new ArrayList<GunInfoButton>();

    public UIManager(SpriteBatch batch, OrthographicCamera camera, Stage stage,
                     Player player, FontManager fontManager) {
        this.batch = batch;
        this.camera = camera;
        this.stage = stage;
        this.player = player;

        // compassi elemendi loomine
        compass = new Sprite(new Texture("ui1_compass2_t.png"));
        compass.setSize(
                compass.getWidth() * GameInfo.CAM_SCALING,
                compass.getHeight() * GameInfo.CAM_SCALING);

        // compassil olevate markerite tekstuuri lugemine
        compass_marker_appearance = new Sprite(
                new Texture(Gdx.files.internal("ui1_compass_marker2_t.png")));

        compass_marker_appearance.setSize(
                compass_marker_appearance.getWidth() * GameInfo.CAM_SCALING,
                compass_marker_appearance.getHeight() * GameInfo.CAM_SCALING);

        compass_marker_appearance.setOrigin(
                compass_marker_appearance.getWidth(),
                compass_marker_appearance.getHeight() * 0.5f);

        // health bari loomine
        health = new UIBar(new Texture("ui1_health2a_t.png"),
                player.getMaxHealth(), -dx, camera);

        // shield bari loomine
        shield = new UIBar(new Texture("ui1_shield2a_t.png"),
                player.getMaxShield(), dx, camera);

        // baride tausta tekstuuri lugemine
        back = new Texture(Gdx.files.internal("ui1_health1_back_t.png"));

        // health bari taust
        health_back = new Sprite(back);
        health_back.setSize(
                health_back.getWidth() * GameInfo.CAM_SCALING,
                health_back.getHeight() * GameInfo.CAM_SCALING);

        // shield bari taust
        shield_back = new Sprite(back);
        shield_back.setSize(
                health_back.getWidth(),
                health_back.getHeight());
        shield_back.setFlip(true, false);

        screenOverlay.setSize(GameInfo.WIDTH, GameInfo.HEIGHT);
        screenOverlay.setColor(0, 0, 0, 0);

        stage.addActor(screenOverlay);

        int count;
        Gun[] guns = player.getGunList().getGuns();
        if ((count = guns.length) > 0) {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                    new FreeTypeFontGenerator.FreeTypeFontParameter();

            parameter.size = 40;

            fontManager.generateFont("rational-integer", parameter, "gunInfo");

            Color color = new Color(0, 0.68f, 1, 1);
            Color colorUsable = Color.WHITE.cpy();
            colorUsable.a = 0.25f;
            Color colorEmpty = Color.RED.cpy();

            Label.LabelStyle usableStyle =
                    new Label.LabelStyle(fontManager.getFont("gunInfo"), colorUsable);
            Label.LabelStyle selectedStyle =
                    new Label.LabelStyle(fontManager.getFont("gunInfo"), color);
            Label.LabelStyle emptyStyle =
                    new Label.LabelStyle(fontManager.getFont("gunInfo"), colorEmpty);


            for (int i = 0; i < count; i++) {
                Gun gun = guns[i];

                GunInfoButton button = new GunInfoButton(i + 1, gun, stage,
                        usableStyle, selectedStyle, emptyStyle);

                gunButtons.add(button);

                stage.addActor(button);
            }

            float spacing = gunButtons.get(0).getWidth() * (1.025f);
            float start = 0.5f * (GameInfo.WIDTH - spacing * (count - 1));

            float y = gunButtons.get(0).getHeight() * 0.6f;

            for (int i = 0; i < count; i++) {
                GunInfoButton button = gunButtons.get(i);

                button.setCenter(start + i * spacing, y);
            }
        }
    }

    public void update() {
        compass.setCenter(
                camera.position.x,
                camera.position.y);

        for (UIMarker marker : compass_markers)
            marker.update();

        health.update(player.getHealth());
        health_back.setPosition(health.getX(), health.getY());
        shield.update(player.getShield());
        shield_back.setPosition(
                shield.getX() + shield.getWidth() - shield_back.getWidth(),
                shield.getY());

        for (GunInfoButton button : gunButtons) {
            button.update();
        }
    }

    public void render() {
        health_back.draw(batch);
        shield_back.draw(batch);
        health.draw(batch);
        shield.draw(batch);
        compass.draw(batch);
        for (UIMarker marker : compass_markers)
            marker.draw(batch);
    }

    public void placeOverlay(float r, float g, float b, float a) {
        screenOverlay.setColor(r, g, b, a);
    }

    public void removeOverlay() {
        placeOverlay(0, 0, 0, 0);
    }

    public void dispose() {
        compass.getTexture().dispose();
        compass_marker_appearance.getTexture().dispose();

        health.getTexture().dispose();
        shield.getTexture().dispose();
        back.dispose();
    }

    public void reviveMarker(UIMarker marker) {
        compass_markers.add(marker);
    }

    public UIMarker addMarker(Vector2 point) {
        UIMarker marker;

        marker = new UIMarker(
                compass_marker_appearance,
                camera, compass.getWidth() * 0.5f,
                point, this);

        compass_markers.add(marker);

        return marker;
    }

    public void removeMarker(UIMarker uiMarker) {
        compass_markers.removeValue(uiMarker, false);
    }
}
