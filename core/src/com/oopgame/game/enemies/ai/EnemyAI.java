package com.oopgame.game.enemies.ai;

import com.oopgame.game.enemies.ships.EnemyShip;

public interface EnemyAI {
    String getCommands(EnemyShip ship, String state);

    void setPaused(boolean flag);
}
