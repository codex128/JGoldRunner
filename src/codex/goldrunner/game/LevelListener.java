/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.goldrunner.game;

/**
 *
 * @author gary
 */
public interface LevelListener {

    public default void onAllGoldCollected(LevelState level) {
    }

    public default void onHeroDeath(LevelState level) {
    }

    public default void onFailure(LevelState level) {
    }

    public default void onVictory(LevelState level) {
    }

    public default void onRestart(LevelState level) {
    }

    public default void onSkimp(LevelState level) {
    }

    public default void onQuit(LevelState level) {
    }

    public default void onError(LevelState level, String reason) {
    }

}
