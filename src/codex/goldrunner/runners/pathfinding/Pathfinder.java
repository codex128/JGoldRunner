/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.runners.pathfinding;

import codex.goldrunner.runners.RunnerControl;
import codex.goldrunner.units.UnitControl;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class Pathfinder {
	
	LinkedList<PathNode> alive = new LinkedList<>();
	LinkedList<UnitControl> visited = new LinkedList<>();
	Collection<RunnerControl> runners = new LinkedList<>();
	PathStarter starter = new PathStarter() {
		@Override
		public PathNode start(Pathfinder master, RunnerControl find, UnitControl start) {
			return new PathNode(master, find, start);
		}
	};
	RunnerControl find;
	Route route;
	boolean complete = false;
	
	public Pathfinder() {}
	public Pathfinder(Collection<RunnerControl> runners) {
		setRunners(runners);
	}
	
	public void find(RunnerControl find) {
		this.find = find;
		refresh();
	}
	public void start(UnitControl unit) {
		alive.add(starter.start(this, find, unit));
	}
	public void refresh() {
		route = null;
		alive.clear();
		complete = false;
	}
	
	public void traverse() {
		if (complete()) return;
		LinkedList<PathNode> born = new LinkedList<>();
		for (PathNode j : alive) {
			PathNode.TraversalStepReport report = j.traverse(visited);
			if (report.route != null) {
				route = report.route;
				alive.clear();
				visited.clear();
				complete = true;
				return;
			}
			born.addAll(report.created);
		}
		if (born.isEmpty()) {
			complete = true;
			alive.clear();
			visited.clear();
			return;
		}
		alive.clear();
		alive.addAll(born);
	}
	
	public Route getRoute() {
		return route;
	}
	public boolean complete() {
		return complete;
	}
	public boolean cold() {
		return !complete() && alive.isEmpty();
	}
	
	public void setRunners(Collection<RunnerControl> runners) {
		this.runners = runners;
	}
	public Collection<RunnerControl> getRunners() {
		return runners;
	}
	
	public void setPathKickstarter(PathStarter starter) {
		this.starter = starter;
	}
	public PathStarter getPathKickstarter() {
		return starter;
	}
	
	
	public static abstract class PathStarter {
		
		public abstract PathNode start(Pathfinder master, RunnerControl find, UnitControl start);
		
	}
	
}
