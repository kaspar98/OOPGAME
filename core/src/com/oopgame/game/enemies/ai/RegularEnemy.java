package com.oopgame.game.enemies.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.oopgame.game.Player;
import com.oopgame.game.enemies.ships.EnemyShip;

import helpers.GameInfo;

public class RegularEnemy implements EnemyAI {
    private static float optDistance = GameInfo.INNER_RADIUS * 0.5f;

    private Player player;
    private Vector2 playerPosition;
    private Vector2 playerVector;

    private Vector2 movement = new Vector2(1, 0);

    public RegularEnemy(Player player) {
        this.player = player;

        this.playerPosition = player.getPosition();
        this.playerVector = player.getLinearVelocity();
    }

    @Override
    public void getCommands(EnemyShip ship) {
        movement.set(1, 0);

        Body shipBody = ship.getBody();
        Vector2 shipPosition = shipBody.getPosition();
        Vector2 shipVector = shipBody.getLinearVelocity();

        Vector2 shipToPlayer = shipPosition.cpy().sub(playerPosition).scl(-1);
        float distance = shipToPlayer.len();

        // liikumine
        if (optDistance + 5 < distance) {
            ship.movement(movement.setAngle(shipToPlayer.angle()));
        } else if (optDistance > distance) {
            ship.movement(movement.setAngle(playerVector.angle()));
        } else
            ship.slowDown();

        // tulistamine
        if (player.isVisible(shipPosition)) {
            ship.shoot(shipToPlayer.cpy().add(shipToPlayer.cpy().sub(shipVector)
                    .scl(distance > 1 ? 1f - 1 / distance : 1f)).angle());
        }
    }
}
