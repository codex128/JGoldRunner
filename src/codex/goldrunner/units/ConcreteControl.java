/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.runners.Traveller;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author gary
 */
public class ConcreteControl extends UnitControl {
	//Any local variables should be encapsulated by getters/setters so they
	//appear in the SDK properties window and can be edited.
	//Right-click a local variable to encapsulate it with getters and setters.
	
	public ConcreteControl() {}
	public ConcreteControl(LevelState level, Point index) {
		super(level, index);
	}
	
	@Override
	public boolean enter(Traveller travel, boolean force) {
		return false;
	}
	@Override
	public boolean exit(Traveller travel, boolean force) {
		return false;
	}
	@Override
	public boolean stand(Traveller travel) {
		return true;
	}
	@Override
	public int zIndex() {
		return UnitControl.FORE;
	}
	
	@Override
	public String[] types() {
		return new String[]{"concrete"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		if (ThreadLocalRandom.current().nextBoolean())
			return assets.loadModel("Models/units/concrete.j3o");
		else return assets.loadModel("Models/units/concrete2.j3o");
	}
	@Override
	public UnitControl loadControl(String type, LevelState level,
			Point index) {
		return new ConcreteControl(level, index);
	}
	
}
