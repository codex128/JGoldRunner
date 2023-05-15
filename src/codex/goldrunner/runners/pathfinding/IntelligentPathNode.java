/*
 * The MIT License
 *
 * Copyright 2023 gary.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package codex.goldrunner.runners.pathfinding;

import codex.goldrunner.runners.RunnerControl;
import codex.goldrunner.units.UnitControl;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class IntelligentPathNode extends PathNode {
	
	public IntelligentPathNode(Pathfinder master, RunnerControl find, UnitControl unit) {
		super(master, find, unit);
	}
	protected IntelligentPathNode(Pathfinder master, PathNode parent, RunnerControl find, UnitControl unit) {
		super(master, parent, find, unit);
	}
	
	@Override
	protected IntelligentPathNode createPathNode(PathNode parent, RunnerControl find, UnitControl unit) {
		return new IntelligentPathNode(getMasterPathfinder(), parent, find, unit);
	}	
	@Override
	public TraversalStepReport traverse(LinkedList<UnitControl> visited) {
		if (unit == null) return null;
		LinkedList<PathNode> created = new LinkedList<>();
		TraversalStepReport rep;
		for (int i : UnitControl.getVerticleDirections()) {
			rep = probeVerticle(visited, created, unit, i);
			if (rep != null) return rep;
		}		
		for (int i : UnitControl.getHorizontalDirections()) {
			rep = probeHorizontal(visited, created, unit, i);
			if (rep != null) return rep;
		}
		return new TraversalStepReport(null, created);
	}
	
}
