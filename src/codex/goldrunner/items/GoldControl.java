/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.items;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.units.UnitControl;
import codex.goldrunner.units.UnitLoader;
import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class GoldControl extends ItemControl implements UnitLoader {
	
	
	@Override
	public void controlUpdate(float tpf) {
		
	}
	@Override
	protected void initParticleEmitter(AssetManager assets) {
		super.initParticleEmitter(assets);
		emitter.setStartColor(new ColorRGBA(1f, 1f, 0f, 1f));
		emitter.setEndColor(new ColorRGBA(1f, 1f, 0f, 0f));
	}
	
	@Override
	public String[] types() {
		return new String[]{"gold"};
	}
	@Override
	public GoldControl createItem(String type, ItemCarrier wrapper, AssetManager assets) {
		Spatial spat = assets.loadModel("Models/items/gold.j3o");
		spat.getControl(AnimComposer.class).setCurrentAction("normal");
		spat.setLocalScale(.3f);
		((Node)spat).addLight(new AmbientLight(ColorRGBA.Gray));
		GoldControl gold = new GoldControl();
		spat.addControl(gold);
		gold.attach(wrapper);
		gold.initParticleEmitter(assets);
		return gold;
	}
	@Override
	public GoldControl createEditorItem(String type, ItemCarrier wrapper, AssetManager assets) {
		Spatial spat = assets.loadModel("Models/items/gold.j3o");
		spat.setMaterial(assets.loadMaterial("Materials/items/gold_editor.j3m"));
		spat.getControl(AnimComposer.class).setCurrentAction("normal");
		spat.setLocalScale(.3f);
		((Node)spat).addLight(new AmbientLight(ColorRGBA.Gray));
		GoldControl gold = new GoldControl();
		spat.addControl(gold);
		//gold.attach(wrapper);
		return gold;
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		return new Node();
	}
	@Override
	public UnitControl loadControl(String type, LevelState level, Point index) {
		return new UnitControl(level, index) {};
	}
	
}
