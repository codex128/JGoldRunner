/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.runners.Traveller;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class LadderControl extends UnitControl {
	
	public LadderControl() {}
	public LadderControl(LevelState level, Point index) {
		super(level, index);
	}
	
	@Override
	public UnitControl getUp() {
		return getUnitAtIndex(index.x, index.y-1);
	}
	@Override
	public boolean grabbable() {
		return true;
	}
	@Override
	public boolean stand(Traveller travel) {
		return true;
	}
	@Override
	public int zIndex() {
		// mid = in between and blocks digging
		return MID;
	}
	@Override
	public boolean escapable() {
		return true;
	}
	
	@Override
	public String[] types() {
		return new String[]{"ladder", "platform-ladder"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		if (type.equals("platform-ladder"))
			return assets.loadModel("Models/units/platformladder.j3o");
		else return assets.loadModel("Models/units/ladder.j3o");
	}
	@Override
	public UnitControl loadControl(String type, LevelState level,
			Point index) {
		return new LadderControl(level, index);
	}
	
}
