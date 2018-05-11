package helpers;

import com.badlogic.gdx.math.Vector2;

public class GameInfo {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static final float HYPOTENUSE = new Vector2(WIDTH, HEIGHT).len() / 2f;
    public static final float SCALING = 0.1f;

    public static final float FORCE_MULTIPLIER = 5000;

    public static final float W_WIDTH = 2048;
    public static final float W_HEIGHT = 2048;
}
