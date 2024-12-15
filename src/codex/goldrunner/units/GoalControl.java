/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.util.Index3i;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class GoalControl extends UnitControl {

    public GoalControl() {
    }

    public GoalControl(LevelState level, Index3i index) {
        super(level, index);
    }

    @Override
    public boolean goal() {
        return true;
    }

    @Override
    public String[] types() {
        return new String[]{"goal"};
    }

    @Override
    public UnitControl loadControl(String type, LevelState level, Index3i index) {
        return new GoalControl(level, index);
    }

}
