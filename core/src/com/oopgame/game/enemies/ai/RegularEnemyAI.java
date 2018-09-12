package com.oopgame.game.enemies.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oopgame.game.Player;
import com.oopgame.game.enemies.ships.EnemyShip;
import com.oopgame.game.guns.Gun;

import helpers.GameInfo;

public class RegularEnemyAI implements EnemyAI {
    public static String keyType = "regular";

    private static float optDistance = GameInfo.INNER_RADIUS * 0.5f * GameInfo.CAM_SCALING;

    private static float spawnDistance = GameInfo.INNER_RADIUS * 0.25f * GameInfo.SCALING;
    private static float safeDistance = GameInfo.OUTER_RADIUS * GameInfo.CAM_SCALING;

    private Player player;
    private Vector2 playerPosition;
    private Vector2 playerVector;

    private Vector2 movement = new Vector2(1, 0);

    private boolean paused;

    private Vector2 spareVector = new Vector2();

    public RegularEnemyAI(Player player) {
        this.player = player;

        this.playerPosition = player.getPosition();
        this.playerVector = player.getLinearVelocity();

        paused = true;
    }

    @Override
    public String getCommands(EnemyShip ship, String state) {
        // praegu lendavad vaenlased playerile otsa vahepeal,
        // sellest vb saaks teha olukorra, kus vastane lendab õhku ja
        // player saab ka natuke viga, näiteks mingi osa vastasel kokkupõrkel olnud eludest
        // on playeri laevale damage'iks

        /*if (!paused) {
            if ("started".equals(state)) {
                // TODO: liigub spawnist eemale, et seda ei blockiks

                Body shipBody = ship.getBody();
                Vector2 shipPos = shipBody.getPosition();

                float angle = shipBody.getAngle() * MathUtils.radiansToDegrees;
                shipBody.setLinearVelocity(
                        spareVector.set(ship.getMaxSpeed(), 0).setAngle(angle));

                ship.turnTowards(angle);

                vectorBetween(spareVector, shipPos, ship.getSpawnPos());

                if (spareVector.len() > spawnDistance) {
                    state = "ammoCheck";
                }
            } else if ("ammoCheck".equals(state)) {
                ship.slowDown();

                // TODO: kontrollib kas relv on olemas, (kui ei ole siis paneks ns roamima)

                // TODO: kui ammot on vähemalt pool olemas, siis paneb ründama, muidu põgeneb
                Gun gun = ship.getGun();

                if (gun != null) {
                    if (gun.getAmmoLeft() * 2 > gun.getMaxAmmo()) {
                        state = "attacking";
                    } else
                        state = "escaping";
                } *//*else {

                }*//*

            } else if ("attacking".equals(state)) {
                ship.slowDown();

                // TODO: liigub playeri poole kuni on heas kohas, et tulistada

                // TODO: hea koha peal jääb seisma, et liiga lähedale ei läheks

                // TODO: kui ammo otsas, siis paneb ammot checkima

                Body shipBody = ship.getBody();
                Vector2 shipPos = shipBody.getPosition();

                vectorBetween(spareVector, shipPos, playerPosition);
                float distance = spareVector.len();


                if (player.isVisible(shipPos)) {
                    ship.shoot(spareVector.cpy().add(
                            spareVector.cpy().sub(shipBody.getLinearVelocity())
                                    .scl(distance > 1 ? 1f - 1 / distance : 1f)).angle());
                }

                if (ship.getGun().getAmmoLeft() == 0)
                    state = "ammoCheck";
            } else if ("escaping".equals(state)) {
                // TODO: põgeneb niikaua kuni ammot oleks piisavalt, et rünnata uuesti

                // TODO: jääb seisma, kui on piisavalt kaugel, et liiga kaugele ära ei põgeneks

                state = "ammoCheck";
            }
        }*/

        if (!paused) {
            if ("started".equals(state)) {
                Body shipBody = ship.getBody();
                Vector2 shipPos = shipBody.getPosition();

                float angle = shipBody.getAngle() * MathUtils.radiansToDegrees;
                shipBody.setLinearVelocity(
                        spareVector.set(ship.getMaxSpeed(), 0).setAngle(angle));

                ship.turnTowards(angle);

                vectorBetween(spareVector, shipPos, ship.getSpawnPos());

                if (spareVector.len() > spawnDistance)
                    state = "ammoCheck";
            } else if ("ammoCheck".equals(state)) {
                Gun gun = ship.getGun();

                if (gun != null) {
                    if (gun.getAmmoLeft() * 2 > gun.getMaxAmmo()) {
                        state = "attacking";
                    } else
                        state = "escaping";
                }
            } else if ("attacking".equals(state)) {
                Body shipBody = ship.getBody();
                Vector2 shipPos = shipBody.getPosition();

                Vector2 between = new Vector2();
                vectorBetween(between, shipPos, playerPosition);
                float distance = between.len();

                if (optDistance + 5 < distance) {
                    shipBody.applyForceToCenter(
                            between.cpy().setLength(GameInfo.FORCE_MULTIPLIER), true);
                } else if (optDistance > distance) {
                    shipBody.applyForceToCenter(
                            between.cpy().setLength(GameInfo.FORCE_MULTIPLIER * 0.9f).scl(-1),
                            true);
                }

                shipBody.setAngularVelocity(0);

                ship.turnTowards(between.angle());

                if (shipBody.getLinearVelocity().len() > ship.getMaxSpeed()) {
                    shipBody.setLinearVelocity(
                            shipBody.getLinearVelocity().setLength(ship.getMaxSpeed()));
                }

                if (player.isVisible(shipPos))
                    ship.shoot(between.cpy().add(
                            between.cpy().sub(shipBody.getLinearVelocity())
                                    .scl(distance > 1 ? 1f - 1 / distance : 1f)).angle());

                if (ship.getGun().getAmmoLeft() == 0)
                    state = "ammoCheck";
            } else if ("escaping".equals(state)) {
                Body shipBody = ship.getBody();
                Vector2 shipPos = shipBody.getPosition();

                Vector2 between = new Vector2();
                vectorBetween(between, shipPos, playerPosition);
                float distance = between.len();

                float headingAngle = between.angle() - 180;

                if (distance > safeDistance + 10) {
                    shipBody.applyForceToCenter(
                            spareVector.set(GameInfo.FORCE_MULTIPLIER * 0.5f, 0)
                                    .setAngle(headingAngle = between.angle()),
                            true);
                } else if (distance < safeDistance) {
                    shipBody.applyForceToCenter(
                            spareVector.set(GameInfo.FORCE_MULTIPLIER, 0)
                                    .setAngle(headingAngle),
                            true);
                }

                shipBody.setAngularVelocity(0);

                ship.turnTowards(headingAngle);

                if (shipBody.getLinearVelocity().len() > ship.getMaxSpeed()) {
                    shipBody.setLinearVelocity(
                            shipBody.getLinearVelocity().setLength(ship.getMaxSpeed()));
                }

                state = "ammoCheck";
            }
        }

        return state;
    }

    private void vectorBetween(Vector2 vector, Vector2 vectorFrom, Vector2 vectorTo) {
        vector.set(vectorTo).sub(vectorFrom);
    }

    @Override
    public void setPaused(boolean flag) {
        paused = flag;
    }
}
