/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.runners;

import codex.goldrunner.game.MapFace;
import codex.goldrunner.items.ItemControl;
import codex.goldrunner.units.UnitControl;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.action.Action;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Collection;
import java.util.LinkedList;
import codex.goldrunner.items.ItemCarrier;

/**
 *
 * @author gary
 */
public abstract class RunnerControl extends AbstractControl implements Traveller, ItemCarrier {
	
	AnimComposer anim;
	LinkedList<UnitControl> occupy = new LinkedList<>();
	Collection<RunnerControl> runners;
	ItemControl item;
	boolean transition = false;
	boolean moving = false;
	int lastmove = UnitControl.NONE;
	float speed = .1f;
	float idle = 0f;
	boolean alive = true;
	boolean climbingOut = false;
	boolean pause = false;
	
	
	protected RunnerControl() {}
	public RunnerControl(UnitControl start) {
		push(start);
	}
	
	
	@Override
	protected void controlUpdate(float tpf) {
		if (pause || stationary()) return;
		if (getCurrentFace().isGravityInfluenced()) {
			fall(false);
		}
		if (!inTransition()) {
			if (occupy.size() > 1) occupy.removeFirst();
			else if (moving) {
				onStopRunning();
				moving = false;
			}
			UnitControl down = occupy.getLast().getDown();
			if (!occupy.getLast().grabbable() || down == null || down.stand(this)) {
				anim.setCurrentAction("idle");
			}
			else {
				anim.setCurrentAction("idle+hang");
			}
			idle += tpf;
		}
		moveToward(occupy.getLast(), getRealSpeed());
		pickup();
		if (occupy.getLast().killer() && isAlive()) kill();
		anim.setGlobalSpeed(calculateGlobalAnimationSpeed());
	}
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}
	@Override
	public void setSpatial(Spatial spat) {
		super.setSpatial(spat);
		anim = spatial.getControl(AnimComposer.class);
		initAnimation();
	}
	private void initAnimation() {
		anim.setGlobalSpeed(calculateGlobalAnimationSpeed());
		Action run = anim.action("run");
		run.setSpeed(1.5f);
		Action climb = anim.action("climb");
		climb.setSpeed(1.2f);
		Action fall = anim.action("hang").jmeClone();
		fall.setSpeed(0);
		anim.addAction("fall", fall);
		Action idlehang = anim.getAction("hang").jmeClone();
		idlehang.setSpeed(0);
		anim.addAction("idle+hang", idlehang);
		anim.setCurrentAction("idle");
	}
	private void moveToward(UnitControl unit, float speed) {
		Vector3f dest = unit.getSpatial().getWorldTranslation();
		if (spatial.getLocalTranslation().distanceSquared(dest) >= speed*speed) {
			spatial.move(dest.subtract(spatial.getLocalTranslation()).normalizeLocal().multLocal(speed));
		}
		else {
			onArrival(unit);
		}
	}
	
	// actions
	protected void push(UnitControl unit) {
		if (idle > 0f) {
			idle = 0f;
		}
		occupy.addLast(unit);
		if (occupy.size() > 2) occupy.removeFirst();
		if (occupy.getLast().getIndex().z != occupy.getFirst().getIndex().z) {
			onFaceChange(occupy.getFirst().getIndex().z, occupy.getLast().getIndex().z);
		}
		if (!moving) {
			onStartRunning();
			moving = true;
		}
	}
	public boolean warp(UnitControl unit, boolean transition) {
		// instantly moves to
		if (unit.enter(this, true) && !blocked(unit, UnitControl.NONE)) {
			occupy.clear();
			occupy.addLast(unit);
			if (!transition) {
				spatial.setLocalTranslation(unit.getSpatial().getWorldTranslation());
			}
			else this.transition = true;
			return true;
		}
		return false;
	}
	public boolean warp(UnitControl unit, String action) {
		if (warp(unit, true)) {
			anim.setCurrentAction(action);
			return true;
		}
		return false;
	}
	public boolean move(UnitControl unit, int direction, boolean force) {
		// transitionally moves to
		if (alive && occupy.getLast().exit(this, force) && unit.enter(this, force)
				&& !inTransition() && !blocked(unit, direction)) {
			push(unit);
			transition = true;
			climbingOut = false;
			lastmove = direction;
			return true;
		}
		else return false;
	}
	public boolean action(int direction) {
		assert UnitControl.isOrthogonal(direction);
		if (!getCurrentFace().isGravityInfluenced()) {
			return simpleWalk(direction);
		}
		switch (direction) {
			case UnitControl.U: return climb();
			case UnitControl.R:
			case UnitControl.L: return walk(direction);
			case UnitControl.D: return fall(true);
			case UnitControl.IN: return walkIn();
			default: return false;
		}
	}
	public boolean walk(int direction) {
		// walks right or left if there is a solid unit down
		assert UnitControl.isHorizontal(direction);
		UnitControl dest = occupy.getLast().getAdjacent(direction);
		UnitControl down = occupy.getLast().getAdjacent(UnitControl.D);
		if (dest != null && (occupy.getLast().grabbable() || down == null ||
				climbingOut || down.stand(this) || blocked(down, UnitControl.D))) {
			if (move(dest, direction, true)) {
				Quaternion rot = new Quaternion();
				rot.lookAt(occupy.getLast().getSpatial().getWorldTranslation()
						.subtract(occupy.getFirst().getSpatial()
						.getWorldTranslation()), Vector3f.UNIT_Y);
				spatial.setLocalRotation(rot);
				UnitControl d = occupy.getLast().getDown();
				if (occupy.getLast().grabbable() && d != null && !d.stand(this)) {
					performAnimationAction("hang");
				}
				else {
					performAnimationAction("run");
				}
				return true;
			}
		}
		return false;
	}
	public boolean simpleWalk(int direction) {
		assert UnitControl.isOrthogonal(direction);
		UnitControl dest = occupy.getLast().getAdjacent(direction);
		if (dest != null) {
			if (move(dest, direction, true)) {
				Quaternion rot = new Quaternion();
				rot.lookAt(occupy.getLast().getSpatial().getWorldTranslation()
						.subtract(occupy.getFirst().getSpatial()
						.getWorldTranslation()), Vector3f.UNIT_Y);
				spatial.setLocalRotation(rot);
				performAnimationAction("run");
				return true;
			}
		}
		return false;
	}
	public boolean walkIn() {
		UnitControl in = occupy.getLast().getIn();
		if (in != null) {
			if (move(in, UnitControl.IN, true)) {
				System.out.println("perform action: run in");
				performAnimationAction("run");
				return true;
			}
		}
		return false;
	}
	public boolean climb() {
		UnitControl up = occupy.getLast().getUp();
		if (up != null) {
			if (move(up, UnitControl.U, true)) {
				spatial.setLocalRotation(getCurrentFace().getLocalRotation());
				performAnimationAction("climb");
				return true;
			}
		}
		else {
			return walkIn();
		}
		return false;
	}
	public boolean climbOut() {
		UnitControl up = occupy.getLast().getRelative(UnitControl.U);
		if (up != null && warp(up, true)) {
			climbingOut = true;
			spatial.setLocalRotation(occupy.getLast().getSpatial()
					.getLocalRotation().negateLocal());
			performAnimationAction("climb");
			return true;
		}
		return false;
	}
	public boolean fall(boolean force) {
		UnitControl down = occupy.getLast().getDown();
		if (down != null && (force || (!down.stand(this) &&
				!occupy.getLast().grabbable() && !climbingOut))) {
			if (move(down, UnitControl.D, force)) {
				spatial.setLocalRotation(occupy.getLast()
						.getSpatial().getWorldRotation());
				performAnimationAction("fall");
				return true;
			}
		}
		return false;
	}
	
	protected void onStartRunning() {
		
	}
	protected void onStopRunning() {
		
	}
	protected void onArrival(UnitControl arrive) {
		transition = false;
		snap(arrive);
	}
	protected void onFaceChange(int oldFace, int newFace) {
		
	}
	
	@Override
	public boolean kill() {
		alive = false;
		moving = false;
		onStopRunning();
		return true;
	}
	public void pickup() {
		ItemControl item = getUnitItem();
		if (item != null) {
			item.attach(this);
			if (this.item != null) this.item.releaseParticleEffect();
		}
	}
	public boolean stuck() {
		return !occupy.getLast().exit(this, false);
	}
	protected boolean stationary() {
		return occupy.getLast() == null;
	}
	
	protected void performAnimationAction(String name) {
		if (anim.getCurrentAction() != anim.getAction(name)) {
			anim.setCurrentAction(name);
		}
	}
	protected float calculateGlobalAnimationSpeed() {
		return 1.6f;
	}
	protected MapFace getCurrentFace() {
		return occupy.getLast().getLevel().getFace(occupy.getLast().getIndex().z);
	}
	
	@Override
	public ItemControl getItem() {
		return item;
	}
	@Override
	public void acceptItem(ItemControl item) {
		item.getSpatial().removeFromParent();
		this.item = item;
	}
	@Override
	public void releaseItem() {
		item = null;
	}
	protected ItemControl getUnitItem() {
		if (occupy.getLast().getItem() != null &&
				occupy.getLast().inBounds(spatial.getLocalTranslation())) {
			return occupy.getLast().getItem();
		}
		else return null;
	}
	
	protected boolean blocked(UnitControl destination, int direction) {
		return blocked(runners, destination, this);
	}
	protected boolean isOccupied(UnitControl unit) {
		for (RunnerControl e : runners) {
			if (e.getOccupied() == unit) {
				return true;
			}
		}
		return false;
	}
	protected void snap(UnitControl unit) {
		spatial.setLocalTranslation(unit.getSpatial().getWorldTranslation());
	}
	public float getBasicSpeed() {
		return speed;
	}
	public float getRealSpeed() {
		return speed;
	}
	public int getDirection() {
		if (occupy.size() > 1) {
			return occupy.getFirst().getDirectionTo(occupy.getLast());
		}
		return UnitControl.NONE;
	}
	public int getLastDirection() {
		return lastmove;
	}
	
	public void setRunnerList(Collection<RunnerControl> runners) {
		this.runners = runners;
	}
	public void pause(boolean pause) {
		this.pause = pause;
		if (this.pause) {
			anim.setGlobalSpeed(0);
		}
		else anim.setGlobalSpeed(1.6f);
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	@Override
	public UnitControl getOccupied() {
		return occupy.getLast();
	}
	@Override
	public BoundingVolume getWorldBound() {
		return spatial.getWorldBound();
	}
	public boolean inTransition() {
		return transition;
	}
	public boolean isAlive() {
		return alive;
	}
	
	/**
	 * Tests if a runner is blocking a unit.
	 * @param runners list of all runners (not null)
	 * @param ignore automatically ignores testing this runner (can be null)
	 * @param destination the unit to test (not null)
	 * @return if the unit is blocked by a listed runner
	 */
	public static boolean blocked(Collection<RunnerControl> runners, UnitControl destination, RunnerControl... ignore) {
		if (runners == null) return false;
		main: for (RunnerControl runner : runners) {
			for (RunnerControl ig : ignore) {
				if (runner == ig) continue main;
			}
			if (runner.occupy.getLast() == destination
					|| destination.inBounds(runner.getSpatial().getLocalTranslation())) {
				return true;
			}
		}
		return false;
	}
	
}
