/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.runners.Traveller;
import codex.goldrunner.util.Index3i;
import codex.jmeutil.Timer;
import codex.jmeutil.TimerListener;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class BrickControl extends UnitControl implements TimerListener {
	
	public static final int UNDIG_TIME = 6;
	Timer time = new Timer(UNDIG_TIME);
	Timer filltime = new Timer(.5f);
	ParticleEmitter emitter;
	
	
	public BrickControl() {}
	public BrickControl(LevelState level, Index3i index) {
		super(level, index);
		time.addListener(this);
	}
	
	
	@Override
	protected void controlUpdate(float tpf) {
		time.update(tpf);
		if (emitter.getParent() != null && emitter.getNumVisibleParticles() == 0) {
			emitter.removeFromParent();
		}
	}
	@Override
	public void dig() {
		time.start();
		spatial.setCullHint(Spatial.CullHint.Always);
		emitter.setLocalTransform(spatial.getLocalTransform());
		emitter.emitAllParticles();
		spatial.getParent().attachChild(emitter);
	}
	@Override
	public boolean enter(Traveller travel, boolean force) {
		return time.isRunning() && super.enter(travel, force) &&
				(travel.fallThroughHoles() || 
				getRelativeTo(travel.getOccupied()) == UnitControl.U);
	}
	@Override
	public boolean exit(Traveller travel, boolean force) {
		return travel.fallThroughHoles();
	}
	@Override
	public boolean diggable() {
		UnitControl up = getRelativeUnit(0, -1);
		return !time.isRunning() && (up == null ||
				up.zIndex() == UnitControl.BACK);
	}
	@Override
	public boolean stand(Traveller travel) {
		return !time.isRunning();
	}
	@Override
	public boolean killer() {
		return !time.isRunning();
	}
	@Override
	public boolean fumble() {
		return true;
	}
	@Override
	public int zIndex() {
		// fore = in front and blocks digging
		return !time.isRunning() ? FORE : BACK;
	}
	@Override
	public void onTimerFinish(Timer timer) {		
		spatial.setCullHint(Spatial.CullHint.Inherit);
		time.reset();
	}
	
	private ParticleEmitter createParticleEmitter(AssetManager assets) {
		emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
		emitter.setMaterial(assets.loadMaterial("Materials/effects/brickfragment.j3m"));
		emitter.setImagesX(3); emitter.setImagesY(3);
		emitter.setRotateSpeed(4);
		emitter.setSelectRandomImage(true);
		emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
		emitter.getParticleInfluencer().setVelocityVariation(.60f);
		emitter.setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
		emitter.setGravity(0f, 6f, 0f);
		emitter.setHighLife(2); emitter.setLowLife(.2f);
		emitter.setStartSize(.1f); emitter.setEndSize(.5f);
		emitter.setParticlesPerSec(0);
		emitter.setNumParticles(50);
		Vector3f p = new Vector3f(.5f, .5f, .5f);
		emitter.setShape(new EmitterBoxShape(p.negate(), p));
		return emitter;
	}
	
	@Override
	public String[] types() {
		return new String[]{"brick"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		Spatial spat = assets.loadModel("Models/units/brick.j3o");
		return spat;
	}
	@Override
	public UnitControl loadControl(String type, LevelState level, Index3i index) {
		BrickControl brick = new BrickControl(level, index);
		brick.createParticleEmitter(level.getApplication().getAssetManager());		
		return brick;
	}
	
}
