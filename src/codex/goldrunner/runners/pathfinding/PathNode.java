/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.runners.pathfinding;

import codex.goldrunner.runners.RunnerControl;
import codex.goldrunner.runners.Traveller;
import codex.goldrunner.units.UnitControl;
import com.jme3.bounding.BoundingVolume;
import java.util.LinkedList;

/**
 * This pathfinder algorithm is flawed on purpose to mislead enemies
 * in a seemingly idiotic fassion.
 * 
 * @author gary
 */
public class PathNode implements Traveller {
	
	Pathfinder master;
	PathNode parent;
	RunnerControl find;
	UnitControl unit;
	
	public PathNode(Pathfinder master, RunnerControl find, UnitControl unit) {
		this.master = master;
		this.find = find;
		this.unit = unit;
	}
	protected PathNode(Pathfinder master, PathNode parent, RunnerControl find, UnitControl unit) {
		this(master, find, unit);
		this.parent = parent;
	}
	
	@Override
	public boolean fallThroughHoles() {
		return true;
	}
	
	protected PathNode createPathNode(PathNode parent, RunnerControl find, UnitControl unit) {
		return new PathNode(master, parent, find, unit);
	}
	
	protected boolean find() {
		return unit == find.getOccupied();
	}
	protected Route createRoute() {
		Route route = new Route();
		route.addNode(this);
		if (parent != null) parent.supplyRoute(route);
		return route;
	}
	protected void supplyRoute(Route route) {
		if (parent != null) {
			route.addNode(this);
			parent.supplyRoute(route);
		}
	}
	protected TraversalStepReport probeVerticle(LinkedList<UnitControl> visited,
			LinkedList<PathNode> created, UnitControl here, int direction) {
		assert UnitControl.isVerticle(direction);
		return probeNext(visited, created, here, unit.getAdjacent(direction));
	}
	protected TraversalStepReport probeHorizontal(LinkedList<UnitControl> visited,
			LinkedList<PathNode> created, UnitControl here, int direction) {
		assert UnitControl.isHorizontal(direction);
		UnitControl next = unit.getAdjacent(direction);
		if (next == null) return null;
		UnitControl down = unit.getRelative(UnitControl.D);
		if (next.grabbable() || down == null || down.stand(this) ||
				RunnerControl.blocked(master.getRunners(), down, find)) {
			return probeNext(visited, created, here, next);
		}
		else return null;
	}
	protected TraversalStepReport probeNext(LinkedList<UnitControl> visited,
			LinkedList<PathNode> created, UnitControl here, UnitControl next) {
		if (next == null || !next.enter(this, false) || !here.exit(this, false)) {
			return null;
		}
		for (UnitControl v : visited) {
			if (next == v) return null;
		}
		visited.add(next);
		PathNode child = createPathNode(this, find, next);
		if (child.find()) {
			return new TraversalStepReport(child.createRoute(), null);
		}
		created.add(child);
		return null;
	}
	
	public TraversalStepReport traverse(LinkedList<UnitControl> visited) {
		if (unit == null) return null;
		LinkedList<PathNode> created = new LinkedList<>();
		TraversalStepReport rep;
		// running this code should make the pathfinding more intelligent
		/*for (int i : UnitControl.getVerticleDirections()) {
			rep = probeVerticle(visited, created, unit, i);
			if (rep != null) return rep;
		}		
		for (int i : UnitControl.getHorizontalDirections()) {
			rep = probeHorizontal(visited, created, unit, i);
			if (rep != null) return rep;
		}*/
		for (int i : UnitControl.getOrthogonalDirections()) {
			UnitControl down = unit.getDown();
			if ((UnitControl.isHorizontal(i) || i == UnitControl.IN) && !unit.grabbable() && down != null && !down.stand(this) && unit.getFace().isGravityInfluenced()) {
				continue;
			}
			rep = probeNext(visited, created, unit, unit.getAdjacent(i));
			if (rep != null) return rep;
		}
		return new TraversalStepReport(null, created);
	}
		
	public Pathfinder getMasterPathfinder() {
		return master;
	}
	@Override
	public UnitControl getOccupied() {
		return unit;
	}
	@Override
	public boolean kill() {
		return false;
	}
	@Override
	public BoundingVolume getWorldBound() {
		return null;
	}
	
	
	public static class TraversalStepReport {
		public Route route;
		public LinkedList<PathNode> created;
		protected TraversalStepReport(Route route, LinkedList<PathNode> created) {
			this.route = route;
			this.created = created;
		}
	}
	
}
