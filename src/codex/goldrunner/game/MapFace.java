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
package codex.goldrunner.game;

import codex.goldrunner.units.UnitControl;
import codex.goldrunner.util.Index3i;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author gary
 */
public class MapFace extends Node {
	
	LevelState level;
	UnitControl[][] map;
	int index;
	float angle;
	
	public MapFace(LevelState level, UnitControl[][] map, int index, float angle) {
		this.level = level;
		this.index = index;
		this.map = map;
		this.angle = angle;
		setLocalRotation(new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y));
	}
	
	public LevelState getLevel() {
		return level;
	}
	public int getIndex() {
		return index;
	}
	public UnitControl[][] getMap() {
		return map;
	}
	public float getAngle() {
		return angle;
	}
	
	public UnitControl getUnit(int x, int y) {
		return map[y][x];
	}
	public MapFace getAdjacentFace(int direction) {
		int i = index+toIndexDirection(direction);
		int l = level.getFaces().length;
		if (i < 0) i = l-1;
		else if (i >= l) i = 0;
		return level.getFace(i);
	}
	private int toIndexDirection(int direction) {
		// only supporting horizontally adjacent faces for now
		switch (direction) {
			case UnitControl.DL:
			case UnitControl.L: return 1;
			case UnitControl.DR:
			case UnitControl.R: return -1;
			default:
				throw new IllegalArgumentException("Direction must be horizontal!");
		}
	}
	
	public static UnitControl getAdjacentUnit(UnitControl unit, int direction) {
		//assert UnitControl.isOrthogonal(direction);
		//System.out.println("--- starting ---");
		Index3i index = new Index3i(unit.getIndex());
		//System.out.println("  base index: "+index);
		LevelState level = unit.getLevel();
		MapFace face = level.getFace(index.z);
		Index3i d = UnitControl.generateDirectional(direction);
		index.x += d.x;
		index.y += d.y;
		//System.out.println("  moved index: "+index);
		if (index.y < 0 || index.y >= face.getMap().length) {
			return null;
		}
		if (index.x < 0) {
			face = face.getAdjacentFace(direction);
			index.x = face.getMap()[0].length-1;
			//System.out.println("    wrap left to "+face.getIndex());
		}
		else if (index.x >= face.getMap()[0].length) {
			face = face.getAdjacentFace(direction);
			index.x = 0;
			//System.out.println("   wrap right to "+face.getIndex());
		}
		//System.out.println("  final index: "+index);
		return face.getUnit(index.x, index.y);
	}
	
}
