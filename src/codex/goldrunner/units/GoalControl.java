/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class GoalControl extends UnitControl {
	
	
	public GoalControl() {}
	public GoalControl(LevelState level, Point index) {
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
	public UnitControl loadControl(String type, LevelState level,
			Point index) {
		return new GoalControl(level, index);
	}
	
}
