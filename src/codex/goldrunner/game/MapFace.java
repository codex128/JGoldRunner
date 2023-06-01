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
import codex.j3map.J3map;
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
	
	public MapFace(LevelState level, int index) {
		this.level = level;
		this.index = index;
	}
	
	protected UnitControl[][] generateMap(String[] mapData) {
		return new UnitControl[mapData.length+LevelState.TOP_MARGIN][mapData[0].length()];
	}
	public boolean load(String[] mapData, J3map cipher) {
		map = generateMap(mapData);
		for (int i = 0; i < mapData.length; i++) {
			for (int j = 0; j < mapData[i].length(); j++) {
				String key = cipher.getString(""+mapData[i].charAt(j));
				level.loadUnit(key, index, j, i+LevelState.TOP_MARGIN);
			}
		}
		boolean escapable = false;
		for (int j = 0; j < map[0].length; j++) {
			if (map[LevelState.TOP_MARGIN][j].escapable()) {
				level.loadColumn("escape-ladder", index, j, 1, LevelState.TOP_MARGIN);
				escapable = true;
			}
			else {
				level.loadColumn(null, index, j, 1, LevelState.TOP_MARGIN);
			}
			level.loadUnit("goal", index, j, 0);
		}
		return escapable;
	}
	
	public void setAngleFacing(float angle) {
		this.angle = angle;
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
	public boolean isGravityInfluenced() {
		return true;
	}
	public float getAngleFacing() {
		return angle;
	}
	
	public UnitControl getUnit(int x, int y) {
		return map[y][x];
	}
	public MapFace getAdjacentFace(int direction) {
		int i = index+toIndexDirection(direction);
		int l = LevelState.EAST;
		if (i < 0) i = l;
		else if (i > l) i = 0;
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
				throw new IllegalArgumentException("Direction must be horizontal or diagonal!");
		}
	}
	
	/**
	 * Get the unit adjacent to the given unit in the given direction.
	 * @param unit starting unit
	 * @param direction direction of adjacency
	 * @return adjacent unit, null if none adjacent
	 */
	public static UnitControl getAdjacentUnit(UnitControl unit, int direction) {
		Index3i index = new Index3i(unit.getIndex());
		LevelState level = unit.getLevel();
		MapFace face = level.getFace(index.z);
		Index3i d = UnitControl.generateDirectional(direction);
		index.x += d.x;
		index.y += d.y;
		boolean up = index.y < 0;
		boolean right = index.x >= face.getMap()[0].length;
		boolean down = index.y >= face.getMap().length;
		boolean left = index.x < 0;
		if (direction == UnitControl.IN) {
			if (level.is3DFormat() && index.y <= level.getFlatFace().getVerticalIndex()) {
				face = level.getFace(LevelState.UP);
				if (face == null) return null;
				switch (index.z) {
					case LevelState.SOUTH:
						index.y = face.getMap().length-1;
						break;
					case LevelState.WEST:
						index.y = index.x;
						index.x = 0;
						break;
					case LevelState.NORTH:
						index.y = 0;
						index.x = face.getMap().length-1-index.x;
						break;
					case LevelState.EAST:
						index.y = face.getMap().length-1-index.x;
						index.x = face.getMap()[index.y].length-1;
						break;
				}
			}
			else return null;
		}
		else if (up || right || down || left) {
			if (index.z != LevelState.UP) {
				if (down || up || (!unit.getLevel().is3DFormat() && (right || left))) {
					return null;
				}
				if (left) {
					face = face.getAdjacentFace(direction);
					index.x = face.getMap()[0].length-1;
				}
				else if (right) {
					face = face.getAdjacentFace(direction);
					index.x = 0;
				}
			}
			else {
				int y;
				if (face instanceof FlatMapFace) {
					y = ((FlatMapFace)face).getVerticalIndex();
				}
				else y = 0;
				if (down) {
					face = level.getFace(LevelState.SOUTH);
				}
				else if (left) {
					face = level.getFace(LevelState.WEST);
					index.x = index.y;
				}
				else if (up) {
					face = level.getFace(LevelState.NORTH);
					index.x = face.getMap()[0].length-1-index.x;
				}
				else if (right) {
					face = level.getFace(LevelState.EAST);
					index.x = face.getMap()[0].length-1-index.y;
				}
				index.y = y;
			}
		}
		return face.getUnit(index.x, index.y);
	}
	
}
