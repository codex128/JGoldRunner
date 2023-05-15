/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.runners.pathfinding;

import codex.goldrunner.units.UnitControl;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class Route {
	
	LinkedList<UnitControl> route = new LinkedList<>();	
	
	public Route() {}	
	
	protected void addNode(PathNode junction) {
		route.addFirst(junction.unit);
	}
	public UnitControl get() {
		return route.getFirst();
	}
	public void dismiss() {
		if (!arrived()) route.remove(0);
	}
	public boolean arrived() {
		return route.isEmpty();
	}
	public int length() {
		return route.size();
	}
	
}
