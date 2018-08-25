package helpers;

import com.badlogic.gdx.math.Vector2;

public class GameInfo {
    public static final int WIDTH = 1600;
    public static final int HEIGHT = 900;
    public static final boolean FULLSCREEN = false;

    public static final float INNER_RADIUS = (WIDTH < HEIGHT ? WIDTH : HEIGHT);
    public static final float OUTER_RADIUS = new Vector2(WIDTH, HEIGHT).len() / 2f;

    public static final float SCALING = 0.1f;
    public static final float CAM_SCALING = SCALING * 2;

    public static final float W_WIDTH = 2048;
    public static final float W_HEIGHT = 2048;

    public static final float FORCE_MULTIPLIER = 5000;

    public static final float PLAYER_MAXSPEED = 45;
    public static final float PLAYER_ACCELERATION = 0.25f;
    public static final long PLAYER_DAMAGED_DURATION = 200;

    public static final long ENEMY_DAMAGED_DURATION = 200;

    public static final long GIBS_DURATION = 5000;
}
