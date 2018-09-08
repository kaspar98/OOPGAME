package com.oopgame.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.oopgame.game.GibsManager;
import com.oopgame.game.Player;
import com.oopgame.game.Time;
import com.oopgame.game.enemies.ai.EnemyAI;
import com.oopgame.game.enemies.ai.RegularEnemy;
import com.oopgame.game.enemies.ships.EnemyShip;
import com.oopgame.game.enemies.ships.FastShip;
import com.oopgame.game.enemies.ships.MotherShip1;
import com.oopgame.game.guns.damagers.DamagerManager;
import com.oopgame.game.ui.UIManager;
import com.oopgame.game.vfx.VisualEffectsManager;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import helpers.GameInfo;

public class EnemyManager {
    private SpriteBatch batch;
    private World world;

    private Time time;

    private Vector2 playerPos;

    private UIManager uiManager;
    private DamagerManager damagerManager;
    private VisualEffectsManager vfxManager;
    private GibsManager gibsManager;

    // sprite'ide hoidmiseks
    private Map<String, List<Sprite>> spriteMap = new HashMap<String, List<Sprite>>();

    private Map<String, BodyDef> bodyDefMap = new HashMap<String, BodyDef>();

    private Map<String, FixtureDef> fixtureDefMap = new HashMap<String, FixtureDef>();

    private Map<String, Shape> shapeMap = new HashMap<String, Shape>();

    // ai map
    private Map<String, EnemyAI> aiMap = new HashMap<String, EnemyAI>();

    private Deque<EnemyShip> toAdd = new LinkedList<EnemyShip>();

    private List<EnemyShip> aliveShips = new ArrayList<EnemyShip>();
    private Map<String, Deque<EnemyShip>> shipPools = new HashMap<String, Deque<EnemyShip>>();
    private Deque<EnemyShip> toDeactivate = new LinkedList<EnemyShip>();

    private List<EnemyPlacer> placersAlive = new ArrayList<EnemyPlacer>();
    private Deque<EnemyPlacer> toDeactivatePlacers = new LinkedList<EnemyPlacer>();
    private Deque<EnemyPlacer> placersPool = new LinkedList<EnemyPlacer>();


    public EnemyManager(SpriteBatch batch, World world, Time time, Player player,
                        UIManager uiManager, DamagerManager damagerManager,
                        VisualEffectsManager vfxManager, GibsManager gibsManager) {
        this.batch = batch;
        this.world = world;
        this.time = time;
        this.playerPos = player.getPosition();

        this.uiManager = uiManager;
        this.damagerManager = damagerManager;
        this.vfxManager = vfxManager;
        this.gibsManager = gibsManager;

        aiMap.put(RegularEnemy.keyType, new RegularEnemy(player));
    }

    public void update() {
        for (EnemyPlacer placer = toDeactivatePlacers.poll(); placer != null;
             placer = toDeactivatePlacers.poll()) {
            placersAlive.remove(placer);
            placersPool.add(placer);
        }

        for (EnemyShip ship = toDeactivate.poll(); ship != null; ship = toDeactivate.poll()) {
            aliveShips.remove(ship);

            String key = ship.getKeyType();

            ship.killGraphics();
            ship.deactivate();

            if (!shipPools.containsKey(key))
                shipPools.put(key, new LinkedList<EnemyShip>());

            shipPools.get(key).add(ship);
        }

        for (EnemyShip ship = toAdd.poll(); ship != null; ship = toAdd.poll())
            aliveShips.add(ship);

        for (EnemyShip ship : aliveShips)
            ship.update();

        for (EnemyPlacer placer : placersAlive)
            placer.update();
    }

    public void render() {
        for (EnemyShip ship : aliveShips)
            ship.draw(batch);
    }

    public void dispose() {
        for (List<Sprite> sprites : spriteMap.values())
            for (Sprite sprite : sprites)
                sprite.getTexture().dispose();

        for (Shape shape : shapeMap.values())
            shape.dispose();
    }

    public void addEnemyPlacer(float x, float y, float angle, String carrierKey,
                               int millisTillSpawn, int millisSpawnInterval,
                               int fastEnemies) {
        EnemyPlacer placer = placersPool.poll();

        if (placer != null)
            placer.reconfigure(x, y, angle, carrierKey,
                    millisTillSpawn, millisSpawnInterval,
                    fastEnemies);
        else
            placer = new EnemyPlacer(x, y, angle, carrierKey,
                    time, millisTillSpawn, millisSpawnInterval,
                    fastEnemies,
                    this, vfxManager);

        placersAlive.add(placer);
    }

    public void addMotherShip1(float x, float y, float angle,
                               int millisSpawnInterval,
                               int fastShips) {
        String key = MotherShip1.keyType;

        if (!spriteMap.containsKey(key)) {
            List<Sprite> sprites = new ArrayList<Sprite>();

            Sprite sprite = new Sprite(new Texture(
                    Gdx.files.internal("ships/enemy_alien_fighter_1_t.png")));

            sprite.setScale(GameInfo.SCALING * 2.5f);

            sprite.setOrigin(
                    sprite.getWidth() * 0.5f,
                    sprite.getHeight() * 0.5f);

            sprites.add(sprite);
            spriteMap.put(key, sprites);

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            bodyDefMap.put(key, bodyDef);

            CircleShape circle = new CircleShape();
            circle.setRadius(spriteMap.get(key).get(0).getHeight() * 0.09f);

            shapeMap.put(key, circle);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shapeMap.get(key);
            fixtureDef.density = 0.9f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0.1f;

            fixtureDefMap.put(key, fixtureDef);
        }

        MotherShip1 carrier;

        EnemyShip ship = getFromPool(key);

        if (ship != null) {
            carrier = (MotherShip1) ship;
            carrier.reconfigure(x, y, angle, null, millisSpawnInterval,
                    fastShips);
            carrier.reset();
        } else {
            carrier = new MotherShip1(x, y, angle, world,
                    time, millisSpawnInterval,
                    fastShips,
                    spriteMap.get(key), bodyDefMap.get(key), fixtureDefMap.get(key),
                    this, uiManager, damagerManager, vfxManager, gibsManager);
        }

        toAdd.add(carrier);
    }

    public void addFastShip(float x, float y, float angle) {
        String key = FastShip.keyType;

        if (!spriteMap.containsKey(key)) {
            List<Sprite> sprites = new ArrayList<Sprite>();

            Sprite sprite = new Sprite(new Texture(
                    Gdx.files.internal("ships/enemy_alien_fighter_1b_t.png")));

            sprite.setScale(GameInfo.SCALING);

            sprite.setOrigin(
                    sprite.getWidth() * 0.5f,
                    sprite.getHeight() * 0.5f);
            sprites.add(sprite);
            spriteMap.put(key, sprites);

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            bodyDefMap.put(key, bodyDef);

            CircleShape circle = new CircleShape();
            circle.setRadius(spriteMap.get(key).get(0).getHeight() * 0.04f);

            shapeMap.put(key, circle);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shapeMap.get(key);
            fixtureDef.density = 0.9f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0.1f;

            fixtureDefMap.put(key, fixtureDef);
        }

        FastShip fastShip;
        EnemyShip ship = getFromPool(key);

        if (ship != null) {
            fastShip = (FastShip) ship;
            fastShip.reconfigure(x, y, angle, aiMap.get("regular"));
            fastShip.reset();
        } else {
            fastShip = new FastShip(x, y, angle, world, time,
                    spriteMap.get(key), bodyDefMap.get(key), fixtureDefMap.get(key),
                    this, uiManager, damagerManager, vfxManager, gibsManager,
                    aiMap.get("regular"));
        }

        toAdd.add(fastShip);
    }

    public Vector2 posNearPlayer(float radius) {
        return posNearPlayer(radius, new Vector2());
    }

    public Vector2 posNearPlayer(float radius, Vector2 vector) {
        return vector.set(1, 0).setLength(radius)
                .setAngle(MathUtils.random(360))
                .add(playerPos);
    }

    private EnemyShip getFromPool(String keyType) {
        if (!shipPools.containsKey(keyType))
            shipPools.put(keyType, new LinkedList<EnemyShip>());

        return shipPools.get(keyType).poll();
    }

    public void poolEnemyShip(EnemyShip ship) {
        toDeactivate.add(ship);
    }

    public void poolEnemyPlacer(EnemyPlacer enemyPlacer) {
        toDeactivatePlacers.add(enemyPlacer);
    }
}
