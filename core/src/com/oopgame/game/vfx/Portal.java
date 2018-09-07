package com.oopgame.game.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import helpers.GameInfo;

public class Portal implements VisualEffect, VisualEffectKeeper {
    public static String keyType = "portal";

    private int layer;

    private Vector2 pos = new Vector2();
    private float scale;

    private int lastFrame = 300;
    private int frame = 0;
    private float holdStart = lastFrame * 0.2f;
    private float holdEnd = lastFrame * 0.8f;

    private Bloom[] bloom = new Bloom[2];
    private static Color bloomColor = new Color(0.3f, 0, 0, 1);

    private List<LaserEffect> laserEffects = new ArrayList<LaserEffect>();

    private Eye[][] eyes = new Eye[2][8];

    private Vector2[] eyeLocations;
    private float angleOffset = 0;

    private Deque<VisualEffect> toRemove = new LinkedList<VisualEffect>();

    private VisualEffectsManager vfxManager;
    private VisualEffectKeeper vfxKeeper;

    public Portal(int layer, float x, float y, float scale,
                  int lastFrame, float midpointMultiplier, float holdMultiplier,
                  VisualEffectsManager vfxManager) {
        this.vfxManager = vfxManager;
        this.vfxKeeper = vfxManager;

        reconfigure(layer, x, y, scale,
                lastFrame, midpointMultiplier, holdMultiplier);
    }

    public void reconfigure(int layer, float x, float y, float scale,
                            int lastFrame, float midpointMultiplier, float holdMultiplier) {
        this.layer = layer;

        this.pos.set(x, y);
        this.scale = scale;

        float midpoint = lastFrame * midpointMultiplier;
        holdStart = midpoint - midpoint * holdMultiplier;
        holdEnd = midpoint + (lastFrame - midpoint) * holdMultiplier;

        for (int i = 0; i < bloom.length; i++) {
            VisualEffect vfx = vfxManager.getFromPool(Bloom.keyType);

            if (vfx != null)
                bloom[i] = (Bloom) vfx;
            else
                bloom[i] = vfxManager.createBloom(0, 0, 0, 0,
                        Color.BLACK, 0,
                        0, 0, 0,
                        0, 0);

            bloom[i].setVisualEffectKeeper(this);
            if (i == 0)
                bloom[i].reconfigure(0, x, y, 3f * scale,
                        Color.BLACK, lastFrame,
                        0, 1, 0,
                        midpointMultiplier, holdMultiplier);
            else
                bloom[i].reconfigure(0, x, y, 1.8f * scale,
                        bloomColor, lastFrame,
                        0, 1, 0,
                        midpointMultiplier, holdMultiplier);
            bloom[i].restart();
        }

        int eyeCount = eyes[0].length;
        Vector2 circle = new Vector2(8 * scale * GameInfo.SCALING, 0);
        float angle;
        eyeLocations = new Vector2[eyeCount];

        for (int i = 0; i < eyeCount; i++) {
            angle = (360 / eyeCount) * i;

            Vector2 spawn = pos.cpy().add(circle.setAngle(angle));
            eyeLocations[i] = spawn;

            for (int j = 0; j < eyes.length; j++) {
                Eye eye;
                VisualEffect vfx = vfxManager.getFromPool(Eye.keyType);

                if (j == 0) {
                    if (vfx != null) {
                        eye = (Eye) vfx;
                        eye.reconfigure(0, 0, 0, angle, scale * 0.3f,
                                lastFrame, 0, 1, 0,
                                midpointMultiplier, holdMultiplier);
                    } else
                        eye = vfxManager.createEye(
                                0, 0, 0, angle, scale * 0.3f,
                                lastFrame, 0, 1, 0,
                                midpointMultiplier, holdMultiplier);
                } else {
                    if (vfx != null) {
                        eye = (Eye) vfx;
                        eye.reconfigure(0, 0, 0, 0, scale * 0.5f,
                                lastFrame, 0, 0.5f, 0,
                                midpointMultiplier, holdMultiplier);
                    } else
                        eye = vfxManager.createEye(
                                0, 0, 0, 0, scale * 0.5f,
                                lastFrame, 0, 0.5f, 0,
                                midpointMultiplier, holdMultiplier);
                }

                eye.setOrigin(0, eye.getHeight() * 0.5f);
                eye.setAngle(angle + (j == 0 ? 0 : 45));
                eye.setOriginBasedPosition(spawn.x, spawn.y);

                eye.setVisualEffectKeeper(this);

                eye.restart();

                eyes[j][i] = eye;
            }
        }
    }

    @Override
    public void removeEffect(VisualEffect vfx) {
        if (vfx instanceof LaserEffect) {
            if (frame < lastFrame)
                resetLaserEffect((LaserEffect) vfx);
            else
                toRemove.add(vfx);
        } else if (vfx instanceof Bloom) {
            toRemove.add(vfx);

        } else if (vfx instanceof Eye) {
            toRemove.add(vfx);
        }
    }

    private void resetLaserEffect(LaserEffect fx) {
        fx.reconfigure(0, pos.x, pos.y, MathUtils.random(360f), 0.1f * scale,
                Color.RED, MathUtils.random(90, 120),
                MathUtils.random(0.01f, 0.03f) * scale,
                0, 1, 0,
                0.5f, 0);
        fx.restart();
    }

    @Override
    public void restart() {
        frame = 0;
    }

    @Override
    public void update() {
        for (int i = 0; i < eyes[0].length; i++) {
            Vector2 loc = eyeLocations[i];
            loc.sub(pos);
            float angle = loc.angle() + 1;
            loc.add(pos);

            for (int j = 0; j < eyes.length; j++) {
                Eye eye = eyes[j][i];

                if (eye != null) {
                    eye.update();

                    if (j == 0) {
                        loc.sub(pos).setAngle(angle).add(pos);
                        eye.setAngle(angle + 1);
                    } else {
                        loc.sub(pos).setAngle(angle + angleOffset).add(pos);
                        eye.setAngle(angle + 45/* + angleOffset*/);
                    }

                    eye.setOriginBasedPosition(loc.x, loc.y);
                }

                loc.sub(pos).setAngle(angle).add(pos);
            }
        }

        angleOffset -= 4;

        for (Bloom fx : this.bloom)
            if(fx != null)
                fx.update();

        int laserEffectsleft;
        if ((laserEffectsleft = laserEffects.size()) < 10 + (int) scale && frame < lastFrame) {
            VisualEffect vfx = vfxManager.getFromPool(LaserEffect.keyType);

            LaserEffect laserEffect;

            if (vfx != null) {
                laserEffect = (LaserEffect) vfx;
            } else
                laserEffect = vfxManager.createLaserEffect(0, 0, 0, 0, 0,
                        Color.WHITE, 0, 0,
                        0, 0, 0,
                        0, 0);

            laserEffect.setVisualEffectKeeper(this);
            resetLaserEffect(laserEffect);

            laserEffects.add(laserEffect);
        }

        for (LaserEffect fx : laserEffects)
            fx.update();

        effectRemoval();

        if (frame++ >= lastFrame && bloom[1] == null && laserEffectsleft == 0)
            vfxKeeper.removeEffect(this);
    }

    private void effectRemoval() {
        for (VisualEffect vfx = toRemove.poll(); vfx != null; vfx = toRemove.poll()) {
            vfx.setVisualEffectKeeper(vfxManager);

            if (vfx instanceof LaserEffect) {
                laserEffects.remove(vfx);
            } else if (vfx instanceof Bloom) {
                for (int i = 0; i < bloom.length; i++)
                    if (bloom[i] == vfx) {
                        bloom[i] = null;
                    }
            } else if (vfx instanceof Eye) {
                for (int i = 0; i < eyes.length; i++)
                    for (int j = 0; j < eyes[0].length; j++)
                        if (eyes[i][j] == vfx) {
                            eyes[i][j] = null;
                            break;
                        }
            }

            vfxManager.removeEffect(vfx);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (bloom[0] != null)
            bloom[0].draw(batch);

        for (Eye eye : eyes[1])
            if (eye != null)
                eye.draw(batch);

        for (Eye eye : eyes[0])
            if (eye != null)
                eye.draw(batch);

        if (bloom[1] != null)
            bloom[1].draw(batch);

        for (LaserEffect fx : laserEffects)
            fx.draw(batch);
    }

    @Override
    public int getLayer() {
        return layer;
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    @Override
    public void deactivate() {
        for (int i = 0; i < eyes.length; i++) {
            for (int j = 0; j < eyes[0].length; j++) {
                Eye eye = eyes[i][j];

                if (eye != null) {
                    eyes[i][j] = null;

                    eye.setVisualEffectKeeper(vfxManager);
                    eye.deactivate();
                    vfxManager.removeEffect(eye);
                }
            }
        }

        for (int i = 0; i < bloom.length; i++)
            if (bloom[i] != null) {
                bloom[i].setVisualEffectKeeper(vfxManager);
                vfxKeeper.removeEffect(bloom[i]);

                bloom[i] = null;
            }

        for (LaserEffect fx : laserEffects) {
            fx.setVisualEffectKeeper(vfxManager);

            vfxKeeper.removeEffect(fx);
        }

        laserEffects.clear();
    }

    @Override
    public void setVisualEffectKeeper(VisualEffectKeeper vfxKeeper) {
        this.vfxKeeper = vfxKeeper;
    }
}
