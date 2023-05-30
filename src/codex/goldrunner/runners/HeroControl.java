/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.runners;

import codex.goldrunner.GameGlobals;
import codex.goldrunner.SettingsState;
import codex.goldrunner.game.LevelState;
import codex.goldrunner.items.ItemControl;
import codex.goldrunner.units.UnitControl;
import codex.goldrunner.units.UnitLoader;
import codex.goldrunner.util.JoystickEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.ProgressBar;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class HeroControl extends RunnerControl implements AnalogListener,
		ActionListener, JoystickEventListener, UnitLoader {
	
	public static final float SPEED = .12f;
	public static final float JOYSTICK_SENSITIVITY_X = .2f;
	public static final float JOYSTICK_SENSITIVITY_Y = .1f;
	
	int goldcollected = 0;
	boolean dashing = false;
	boolean sneaking = false;
	float dashspeed = .05f;
	float sneakspeed = -.05f;
	float energy = 100f;
	float energymileage = 2f;
	float energyregen = .01f;
	int particleDensity = 50;
//	boolean dashingenabled = !false;
	ParticleEmitter emitter;
	Node hud = new Node("hero hud");
	ProgressBar energybar = new ProgressBar("Energy");
	float joystickX = 0;
	float joystickY = 0;
//	DynamicAnimControl dac;
	AudioNode steps;
	AudioNode collect;
	
	
	/**
	 * For internal use only.
	 */
	public HeroControl() {
		super();
	}
	public HeroControl(UnitControl start) {
		super(start);
//		initParticleEmitter(assets);
//		initHUD(assets);
//		//initAudio(assets);
//		push(start);
		speed = SPEED;
	}
	
	
	protected void initParticleEmitter(AssetManager assets) {
		emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
		emitter.setMaterial(assets.loadMaterial("Materials/effects/heroboost.j3m"));
		emitter.setImagesX(15); emitter.setImagesY(1);
		Vector3f p = new Vector3f(LevelState.UNIT_SIZE/2, LevelState.UNIT_SIZE/2,
				LevelState.UNIT_SIZE);
		emitter.setShape(new EmitterBoxShape(p.negate(), p));
		emitter.setRotateSpeed(1);
		emitter.setSelectRandomImage(true);
		emitter.setGravity(0f, 0f, 0f);
		emitter.setStartColor(new ColorRGBA(1f, 0f, 0f, .7f));
		emitter.setEndColor(new ColorRGBA(1f, .5f, 0f, 0f));
		emitter.setStartSize(.6f);
		emitter.setEndSize(.7f);
		emitter.setLowLife(3f);
		emitter.setHighLife(3f);
		emitter.setNumParticles(particleDensity*10);
		emitter.setParticlesPerSec(0);
		if (spatial instanceof Node) {
			((Node)spatial).attachChild(emitter);
		}
	}
	protected void initHUD(AssetManager assets) {
		Point size = GameGlobals.getWindowSize();
		energybar.setPreferredSize(new Vector3f(size.x/2, 20, 0));
		energybar.setProgressPercent(energy);
		energybar.setLocalTranslation(size.x/2-energybar.getPreferredSize().x/2,
				energybar.getPreferredSize().y+10, 0);
		if (SettingsState.getInstance().boostingEnabled()) hud.attachChild(energybar);
	}
	protected void initAudio(AssetManager assets) {
		steps = new AudioNode(assets.loadAudio("Sounds/steps_chain.ogg"), new AudioKey());
		steps.setLooping(true);
	}
	public String[] getInputMappings() {
		return "up right down left d a space".split(" ");
	}
	
	@Override
	protected void controlUpdate(float tpf) {
//		if (dac != null) return;
		super.controlUpdate(tpf);
		if (UnitControl.isHorizontal(getLastDirection())) {
			joystickMovementY();
			joystickMovementX();
		}
		else {
			joystickMovementX();
			joystickMovementY();
		}
		energybar.setProgressPercent(energy/100);
		if (dashing) {
			if ((energy -= energymileage) < 0) {
				energy = 0;
				dashing = false;
				emitter.setParticlesPerSec(0);
			}
		}
		else if (/*occupy.size() == 1 &&*/ (energy += energyregen) > 100) {
			energy = 100;
		}
	}
	private void joystickMovementY() {
		SettingsState inst = SettingsState.getInstance();
		if (joystickY < -inst.getJoystickSensitivityY()) {
			climb();
		}
		else if (joystickY > inst.getJoystickSensitivityY()) {
			fall(true);
		}		
	}
	private void joystickMovementX() {
		SettingsState inst = SettingsState.getInstance();
		if (joystickX > inst.getJoystickSensitivityX()) {
			walk(UnitControl.R);
		}
		else if (joystickX < -inst.getJoystickSensitivityX()) {
			walk(UnitControl.L);
		}		
	}
	
	public void dig(int direction) {
		assert UnitControl.isDiagonal(direction);
		if (dashing) return;
		UnitControl dig = occupy.getLast().getAdjacent(direction);
		if (dig != null && dig.diggable()) {
			dig.dig();
		}
	}
	@Override
	public boolean kill() {
		boolean k = super.kill();
		//spatial.setCullHint(Spatial.CullHint.Always);
		return k;
	}
	@Override
	public boolean fallThroughHoles() {
		return true;
	}
	@Override
	public boolean fallThroughPlatforms() {
		return false;
	}
	@Override
	public void acceptItem(ItemControl item) {
		ParticleEmitter emit = item.releaseParticleEffect();
		if (item.getSpatial().getParent() != null)
			item.getSpatial().getParent().attachChild(emit);
		emit.setLocalTransform(item.getSpatial().getLocalTransform());
		item.getSpatial().removeFromParent();
		energy = 100;
		goldcollected++;
	}
	@Override
	protected boolean blocked(UnitControl destination, int direction) {
		if (direction == UnitControl.D) {
			return super.blocked(destination, direction);
		}
		return false;
	}
	@Override
	protected float calculateGlobalAnimationSpeed() {		
		return (super.calculateGlobalAnimationSpeed()/getBasicSpeed())*getRealSpeed();
	}
	
	@Override
	public float getRealSpeed() {
		if (dashing) {
			return super.getRealSpeed()+dashspeed;
		}
		else if (sneaking || SettingsState.getInstance().sluggishModeEnabled()) {
			return super.getRealSpeed()+sneakspeed;
		}
		else {
			return super.getRealSpeed();
		}
	}
	public float getEnergy() {
		return energy;
	}	
	public int getGoldCollected() {
		return goldcollected;
	}
	public ProgressBar getEnergyBar() {
		return energybar;
	}
	public Node getHUD() {
		return hud;
	}
	
	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (!inTransition()) {
			switch (name) {
				case "up": climb(); break;
				case "right": walk(UnitControl.R); break;
				case "down": fall(true); break;
				case "left": walk(UnitControl.L); break;
			}
		}
	}
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
			case "d": if (isPressed) dig(UnitControl.DR); break;
			case "a": if (isPressed) dig(UnitControl.DL); break;
			case "space": if (isPressed && SettingsState.getInstance().boostingEnabled() &&
					!dashing && energy >= 100) {
				dashing = true;
				emitter.setParticlesPerSec(particleDensity);
			} break;
		}
	}
	@Override
	public void onJoyAxisEvent(JoyAxisEvent evt) {
		String id = evt.getAxis().getLogicalId();
		if (id.equals(JoystickAxis.X_AXIS)) {
			joystickX = evt.getValue();
		}
		else if (id.equals(JoystickAxis.Y_AXIS)) {
			joystickY = evt.getValue();
		}
		else if (id.equals(JoystickAxis.POV_X)) {
			if (evt.getValue() > 0f) walk(UnitControl.R);
			else if (evt.getValue() < 0f) walk(UnitControl.L);
		}
		else if (id.equals(JoystickAxis.POV_Y)) {
			if (evt.getValue() < 0f) fall(true);
			else if (evt.getValue() > 0f) climb();
		}
	}
	@Override
	public void onJoyButtonEvent(JoyButtonEvent evt) {
		String id = evt.getButton().getLogicalId();
		if (evt.isPressed()) {
			if (id.equals(JoystickButton.BUTTON_5) ||
					id.equals(JoystickButton.BUTTON_3)) {
				dig(UnitControl.DR);
			}
			else if (id.equals(JoystickButton.BUTTON_4) ||
					id.equals(JoystickButton.BUTTON_2)) {
				dig(UnitControl.DL);
			}
			else if (id.equals(JoystickButton.BUTTON_0) &&
					SettingsState.getInstance().boostingEnabled() &&
					!dashing && energy >= 100) {
				dashing = true;
				emitter.setParticlesPerSec(particleDensity);
			}
		}
		if (id.equals(JoystickButton.BUTTON_1)) {
			sneaking = evt.isPressed();
		}
	}

	@Override
	public String[] types() {
		return new String[]{"hero"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		return new Node();
	}
	@Override
	public UnitControl loadControl(String type, LevelState level, Point index) {
		return new UnitControl(level, index){};
	}
	@Override
	public HeroControl spawn(String type, boolean editor, UnitControl wrapper, AssetManager assets) {
		Spatial s = assets.loadModel("Models/runners/hero.j3o");
		s.setLocalScale(.3f);
		((Node)s).addLight(new AmbientLight(ColorRGBA.White));
		HeroControl hero = new HeroControl(/*asset, */wrapper);
		s.addControl(hero);
		hero.initParticleEmitter(assets);
		hero.initHUD(assets);
//		hero.initAudio(assets);
		return hero;
	}
	
}
