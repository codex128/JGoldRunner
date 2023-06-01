/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelListener;
import codex.goldrunner.game.LevelState;
import codex.goldrunner.runners.Traveller;
import codex.goldrunner.util.Index3i;
import codex.jmeutil.Timer;
import codex.jmeutil.TimerListener;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class EscapeLadderControl extends LadderControl implements LevelListener,
		TimerListener {
	
	boolean activated = false;
	Timer timer = new Timer(0);
	ParticleEmitter emitter;
	
	
	public EscapeLadderControl() {}
	public EscapeLadderControl(LevelState level, Index3i index) {
		super(level, index);
		timer.setCycleMode(Timer.CycleMode.ONCE);
	}
	
	
	@Override
	public void controlUpdate(float tpf) {
		timer.update(tpf);
		if (emitter == null) return;
		if (emitter.getParent() == null && timer.isRunning() &&
				timer.getTimeRemaining() < .001f) {
		}
		if (emitter.getParent() != null && emitter.getNumVisibleParticles() == 0) {
			emitter.removeFromParent();
		}
	}
	
	@Override
	public UnitControl getUp() {
		return (activated ? getRelativeUnit(0, -1) : null);
	}
	@Override
	public boolean grabbable() {
		return activated;
	}
	@Override
	public boolean stand(Traveller travel) {
		return activated;
	}
	@Override
	public int zIndex() {
		// mid = in between and blocks digging
		return (activated ? MID : BACK);
	}
	@Override
	public boolean physical() {
		return false;
	}
	
	@Override
	public void onAllGoldCollected(LevelState level) {
		timer.addListener(this);
		timer.setDuration((float)index.y/10);
		timer.start();
	}
	@Override
	public void onFailure(LevelState level) {}
	@Override
	public void onVictory(LevelState level) {}
	@Override
	public void onRestart(LevelState level) {
		level.removeListener(this);
	}
	@Override
	public void onQuit(LevelState level) {}
	
	@Override
	public void onTimerFinish(Timer timer) {
		spatial.setCullHint(Spatial.CullHint.Inherit);
		spatial.getParent().attachChild(emitter);
		emitter.setLocalTransform(spatial.getLocalTransform());
		emitter.emitAllParticles();
		//initializePhysicsBody();
		activated = true;
	}
	private void initParticleEmitter(AssetManager assets) {
		emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
		emitter.setMaterial(assets.loadMaterial("Materials/effects/laddermagic.j3m"));
		emitter.setImagesX(2); emitter.setImagesY(2);
		emitter.setRotateSpeed(5);
		emitter.setRandomAngle(true);
		emitter.setSelectRandomImage(true);
		emitter.setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
		emitter.setEndColor(new ColorRGBA(1f, 1f, 1f, 0f));
		emitter.setStartSize(LevelState.UNIT_SIZE*3f);
		emitter.setEndSize(LevelState.UNIT_SIZE*.1f);
		emitter.setLowLife(1f);
		emitter.setHighLife(1f);
		emitter.setGravity(0f, 0f, 0f);
		emitter.setParticlesPerSec(0);
		emitter.setNumParticles(1);
	}
	
	@Override
	public String[] types() {
		return new String[]{"escape-ladder"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		if (!editor) {
			Spatial spat = super.loadSpatial(type, editor, assets);
			spat.setCullHint(Spatial.CullHint.Always);
			return spat;
		}
		else {
			Spatial spat = assets.loadModel("Models/units/escapeladder.j3o");
			spat.setMaterial(assets.loadMaterial("Materials/units/ladder.j3m"));
			return spat;
		}
	}	
	@Override
	public UnitControl loadControl(String type, LevelState level, Index3i index) {
		EscapeLadderControl ladder = new EscapeLadderControl(level, index);
		level.addListener(ladder);
		AssetManager assets = level.getApplication().getAssetManager();
		ladder.initParticleEmitter(assets);
		return ladder;
	}
	
}
