package com.oopgame.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.oopgame.game.Player;
import com.oopgame.game.Time;
import com.oopgame.game.enemies.ai.EnemyAI;
import com.oopgame.game.enemies.ai.RegularEnemy;
import com.oopgame.game.enemies.ships.EnemyShip;
import com.oopgame.game.enemies.ships.FastShip;
import com.oopgame.game.guns.damagers.DamagerManager;

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

    private DamagerManager damagerManager;

    // sprite'ide hoidmiseks
    private Map<String, Sprite> spriteMap = new HashMap<String, Sprite>();

    private List<EnemyShip> aliveShips = new ArrayList<EnemyShip>();
    private Map<String, Deque<EnemyShip>> shipPools = new HashMap<String, Deque<EnemyShip>>();
    private Deque<EnemyShip> toDeactivate = new LinkedList<EnemyShip>();


    private Map<String, BodyDef> bodyDefMap = new HashMap<String, BodyDef>();

    private Map<String, FixtureDef> fixtureDefMap = new HashMap<String, FixtureDef>();

    private Map<String, Shape> shapeMap = new HashMap<String, Shape>();


    // ai map
    private Map<String, EnemyAI> aiMap = new HashMap<String, EnemyAI>();


    public EnemyManager(SpriteBatch batch, World world, Time time, Player player,
                        DamagerManager damagerManager) {
        this.batch = batch;
        this.world = world;
        this.time = time;
        this.playerPos = player.getPosition();

        this.damagerManager = damagerManager;

        // TODO: vb saab seda mappide valmispanemise protsessi vastaste loomise juurde tõsta?

        // paneme valmis FastShip objektide loomiseks vajalikud objektid
        String key = "fastShip";

        Sprite sprite = new Sprite(new Texture(
                Gdx.files.internal("ships/enemy_alien_fighter_1b_t.png")));

        sprite.setSize(
                sprite.getWidth() * GameInfo.SCALING,
                sprite.getHeight() * GameInfo.SCALING);

        sprite.setOrigin(
                sprite.getWidth() * 0.5f,
                sprite.getHeight() * 0.5f);

        spriteMap.put(key, sprite);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDefMap.put(key, bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(spriteMap.get(key).getHeight() * 0.3f);

        shapeMap.put(key, circle);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shapeMap.get(key);
        fixtureDef.density = 0.9f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        fixtureDefMap.put(key, fixtureDef);


        aiMap.put("regular", new RegularEnemy(player));
    }

    public void update() {
        for (EnemyShip ship = toDeactivate.poll(); ship != null; ship = toDeactivate.poll()) {
            aliveShips.remove(ship);

            String key;

            if (ship instanceof FastShip) {
                key = "fastShip";
            } else
                throw new RuntimeException("sellist vastase laeva tüüpi pole siin välja toodud!");

            if (!shipPools.containsKey(key))
                shipPools.put(key, new LinkedList<EnemyShip>());

            shipPools.get(key).add(ship);

            ship.deactivate();
        }

        for (EnemyShip ship : aliveShips)
            ship.update();
    }

    public void render() {
        for (EnemyShip ship : aliveShips)
            ship.draw(batch);
    }

    public void dispose() {
        for (Sprite sprite : spriteMap.values())
            sprite.getTexture().dispose();

        for (Shape shape : shapeMap.values())
            shape.dispose();
    }

    public void addEnemy(int fastShipCount) {
        if (fastShipCount <= 0) return;

        // TODO: testimisega leida hea kaugus, kus vastaseid spawnida
        Vector2 spawn = playerPos.cpy().add(
                new Vector2(1, 0).setLength(GameInfo.OUTER_RADIUS * GameInfo.SCALING)
                        .setAngle(MathUtils.random(360)));

        // TODO: tuleb natuke veel ümber teha, et teise klassi laevu ka spawniks
        for (int i = 0; i < fastShipCount; i++) {
            Vector2 point = spawn.cpy().add(new Vector2(1, 0).setLength(100 * GameInfo.SCALING)
                    .setAngle(MathUtils.random(360)));

            String key = "fastShip";

            if (!shipPools.containsKey(key))
                shipPools.put(key, new LinkedList<EnemyShip>());

            EnemyShip ship = shipPools.get(key).poll();

            if (ship != null) {
                ship.reset(point);
            } else {
                ship = new FastShip(point, world, spriteMap.get(key), time,
                        bodyDefMap.get(key), fixtureDefMap.get(key),
                        this, damagerManager, aiMap.get("regular"));
            }

            aliveShips.add(ship);
        }
    }

    public void poolEnemyShip(EnemyShip ship) {
        toDeactivate.add(ship);
    }
}
