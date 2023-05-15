/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class BarControl extends UnitControl {
	
	public BarControl() {}
	public BarControl(LevelState level, Point index) {
		super(level, index);
	}
	
	@Override
	public boolean grabbable() {
		return true;
	}
	@Override
	public int zIndex() {
		// mid = in between and blocks digging
		return UnitControl.FORE;
	}
	
	@Override
	public String[] types() {
		return new String[]{"bar"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		return assets.loadModel("Models/units/bar.j3o");
	}
	@Override
	public UnitControl loadControl(String type, LevelState level,
			Point index) {
		return new BarControl(level, index);
	}
	
}
