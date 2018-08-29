package com.oopgame.game.enemies.ai;

import com.oopgame.game.enemies.ships.EnemyShip;

public interface EnemyAI {
    void getCommands(EnemyShip ship);
}
