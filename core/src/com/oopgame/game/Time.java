package com.oopgame.game;

import com.badlogic.gdx.utils.TimeUtils;

public class Time {
    // klass, mille objektiga vähendame TimeUtils.millis() kutsumist.
    private long time;

    public Time() {
        time = TimeUtils.millis();
    }

    public void update() {
        time = TimeUtils.millis();
    }

    public long getTime() {
        return time;
    }
}
