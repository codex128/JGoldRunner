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
import codex.goldrunner.runners.pathfinding.IntelligentPathNode;
import codex.goldrunner.runners.pathfinding.PathNode;
import codex.goldrunner.runners.pathfinding.Pathfinder;
import codex.goldrunner.units.BrickControl;
import codex.goldrunner.units.UnitControl;
import codex.goldrunner.units.UnitLoader;
import codex.jmeutil.Timer;
import codex.jmeutil.TimerListener;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author gary
 */
public class EnemyControl extends RunnerControl implements 
		TimerListener, UnitLoader {
	
	public static final int MAX_INTEL = 3;
	private static final HashMap<String, Integer> INTELMAP = new HashMap<>();
	static {
		INTELMAP.put("enemy", 0);
		INTELMAP.put("nemesis", 1);
		INTELMAP.put("brainy", 2);
		INTELMAP.put("master", MAX_INTEL);
	}
	
	Pathfinder pathfinder = new Pathfinder();
	int intelligence = 0;
	HeroControl chase;
	Timer climbOutTimer = new Timer(4);
	Timer respawnTimer = new Timer(2);
	float catchmargin = .2f;
	Vector3f originalItemScale = new Vector3f();
	
	
	public EnemyControl() {
		super();
	}
	public EnemyControl(UnitControl start) {
		this(start, 0);
	}
	public EnemyControl(UnitControl start, int intelligence) {
		super(start);
		initializeEnemyIntelligence(intelligence);
		climbOutTimer.addListener(this);
		respawnTimer.addListener(this);
		speed = GameGlobals.Runners.ENEMY_SLOW_SPEED;
	}	
	
	
	protected void initializeEnemyIntelligence(int intel) {
		intelligence = intel;
		if (intelligence > 0) {
			configureBetterPathfinding();
		}
	}
	private void configureBetterPathfinding() {
		pathfinder.setPathKickstarter(new Pathfinder.PathStarter() {
			@Override
			public PathNode start(Pathfinder master, RunnerControl find, UnitControl start) {
				return new IntelligentPathNode(master, find, start);
			}
		});
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (chase == null) return;
		respawnTimer.update(tpf);
		if (pause) return;
		if (respawnTimer.isRunning()) {
			boolean flip = respawnTimer.getTime()%.5f < .25f;
			Spatial.CullHint cull = spatial.getCullHint();
			if (!cull.equals(Spatial.CullHint.Inherit) && flip) {
				spatial.setCullHint(Spatial.CullHint.Inherit);
			}
			else if (!cull.equals(Spatial.CullHint.Always) && !flip) {
				spatial.setCullHint(Spatial.CullHint.Always);
			}
		}
		if (!isAlive() || stationary()) return;
		super.controlUpdate(tpf);
		simpleChase();
		if (caught()) chase.kill();
		climbOutTimer.update(tpf);
		if (stuck()) {
			if (!climbOutTimer.isRunning()) {
				climbOutTimer.setDuration(ThreadLocalRandom.current()
						.nextInt(2, BrickControl.UNDIG_TIME+2));
				climbOutTimer.start();
			}
		}
		else climbOutTimer.reset();
		// run faster if hero is above
		if (chase.occupy.getLast() == occupy.getLast().getRelative(UnitControl.U) &&
				occupy.getLast().getAdjacent(UnitControl.U) == null) {
			speed = GameGlobals.Runners.ENEMY_FAST_SPEED;
		}
		else speed = GameGlobals.Runners.ENEMY_SLOW_SPEED;
	}
	@Override
	public boolean move(UnitControl unit, int direction, boolean force) {
		if (super.move(unit, direction, force)) {
			if (occupy.getLast().fumble() && item != null) {
				UnitControl up = unit.getRelative(UnitControl.U);
				ItemControl i = item;
				if (up != null && item.attach(up)) {
					i.releaseParticleEffect();
				}
			}
			return true;
		}
		return false;
	}	
	@Override
	public boolean climbOut() {
		UnitControl up = occupy.getLast().getRelative(UnitControl.U);
		if (chase.occupy.getLast() != up) return super.climbOut();
		else return false;
	}
	@Override
	protected void onArrival(UnitControl unit) {
		super.onArrival(unit);
		if (pathfinder.getRoute() != null) {
			pathfinder.getRoute().dismiss();
			if (pathfinder.getRoute().arrived()) {
				pathfinder.refresh();
			}
		}
	}
	@Override
	public void onTimerFinish(Timer timer) {
		if (timer == climbOutTimer) {
			climbOut();
			climbOutTimer.reset();
		}
		else if (timer == respawnTimer) {
			climbOutTimer.reset();
			respawnTimer.reset();
			spatial.setCullHint(Spatial.CullHint.Inherit);
			alive = true;
		}
	}
	@Override
	public void pickup() {
		if (item == null && occupy.getLast().getItem() != null
				&& !inTransition()) {
			if (ThreadLocalRandom.current().nextBoolean()) {
				super.pickup();
			}
		}
	}
	@Override
	public void acceptItem(ItemControl item) {
		this.item = item;
		originalItemScale.set(this.item.getSpatial().getLocalScale());
		this.item.getSpatial().setLocalScale(originalItemScale.mult(2));
		SkinningControl skin = spatial.getControl(SkinningControl.class);
		skin.getAttachmentsNode("Hand.Right").attachChild(this.item.getSpatial());
	}
	@Override
	public void releaseItem() {
		item.getSpatial().removeFromParent();
		item.getSpatial().setLocalScale(originalItemScale);
		super.releaseItem();
	}
	@Override
	public boolean kill() {
		alive = false;
		/*UnitControl spawn = getSpawnableUnit();
		if (spawn != null) {
			warp(spawn, true);
			alive = true;
			//respawnTimer.start();
		}*/
		return true;
	}
	@Override
	public void setRunnerList(Collection<RunnerControl> runners) {
		super.setRunnerList(runners);
		pathfinder.setRunners(this.runners);
	}
	@Override
	public boolean fallThroughHoles() {
		return SettingsState.getInstance().extremeModeEnabled();
	}
	@Override
	protected boolean blocked(UnitControl destination, int direction) {
		return RunnerControl.blocked(runners, destination, this, chase);
	}
	
	protected void simpleChase() {
		if (chase == null) return;
		Vector3f there = chase.getOccupied().getSpatial()
				.getLocalTranslation();
		Vector3f here = occupy.getLast().getSpatial().getLocalTranslation();
		if (inTransition()) return;
		// if not following path, use B-line method
		if (pathfinder.getRoute() == null && intelligence < 2 &&
				((there.y > here.y && climb()) ||
				(there.x > here.x && walk(UnitControl.R)) ||
				(there.x < here.x && walk(UnitControl.L)) ||
				(there.y < here.y && fall(true)))) {
			pathfinder.refresh();
		}
		else {
			if (intelligence > 2) {
				pathfinder.refresh();
			}
			// start pathfinding
			if (pathfinder.cold()) {
				pathfinder.find(chase);
				pathfinder.start(occupy.getLast());
			}
			// traverse
			while (!pathfinder.complete()) {
				pathfinder.traverse();
			}
			// follow path
			if (pathfinder.getRoute() != null) {
				action(occupy.getLast().getDirectionTo(pathfinder.getRoute().get()));
			}
		}
	}
	private boolean caught() {
		if (chase == null || respawnTimer.isRunning()) return false;
		if (occupy.getLast() == chase.occupy.getLast() ||
				occupy.getLast() == chase.occupy.getFirst() ||
				occupy.getFirst() == chase.occupy.getLast()) {
			Vector3f here = spatial.getLocalTranslation(),
					there = chase.getSpatial().getLocalTranslation();
			return here.distance(there) < catchmargin;
		}
		return false;
	}	
	public UnitControl getSpawnableUnit() {
		Point index = occupy.getLast().getIndex();
		UnitControl dest = occupy.getLast().getUnitAtIndex(index.x, 0);
		if (dest != null && dest.enter(this, true) && !isOccupied(dest)) {
			return dest;
		}
		else {
			UnitControl[][] units = occupy.getLast().getLevel().getUnits();
			ArrayList<Integer> indices = new ArrayList<>();
			for (int i = 0; i < units[0].length; i++) {
				indices.add(i);
			}
			while (!indices.isEmpty()) {
				int i = ThreadLocalRandom.current().nextInt(indices.size());
				UnitControl enter = units[0][i];
				if (enter.enter(this, true) && !isOccupied(enter)) {
					return enter;
				}
				indices.remove(i);
			}
		}
		return null;
	}
	public void chase(HeroControl hero) {
		chase = hero;
	}
	
	public int getIntelligenceLevel() {
		return intelligence;
	}

	@Override
	public String[] types() {
		return new String[]{"enemy", "nemesis", "brainy", "master"};
	}
	@Override
	public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
		return new Node();
	}
	@Override
	public UnitControl loadControl(String type, LevelState level, Point index) {
		return new UnitControl(level, index) {};
	}
	@Override
	public RunnerControl spawn(String type, boolean editor, UnitControl wrapper, AssetManager assets) {
		Spatial spatial = assets.loadModel("Models/runners/enemy.j3o");
		//spatial.move(0, 1, 0);
		spatial.setLocalScale(.3f);
		EnemyControl enemy = new EnemyControl(wrapper, Math.max(INTELMAP.get(type), SettingsState.getInstance().getMinimumEnemyIntelligence()));
		System.out.println("enemy intelligence: "+enemy.getIntelligenceLevel());
		spatial.addControl(enemy);
		return enemy;
	}
	
}
