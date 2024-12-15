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
import codex.j3map.J3map;

/**
 *
 * @author gary
 */
public class FlatMapFace extends MapFace {

    int verticalIndex;

    public FlatMapFace(LevelState level, int index, int verticleIndex) {
        super(level, index);
        this.verticalIndex = verticleIndex;
    }

    @Override
    public UnitControl[][] generateMap(String[] mapData) {
        return new UnitControl[mapData.length][mapData[0].length()];
    }

    @Override
    public boolean load(String[] mapData, J3map cipher) {
        map = generateMap(mapData);
        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[i].length(); j++) {
                String key = cipher.getString("" + mapData[i].charAt(j));
                level.loadUnit(key, index, j, i);
            }
        }
        return false;
    }

    @Override
    public boolean isGravityInfluenced() {
        return false;
    }

    public int getVerticalIndex() {
        return verticalIndex;
    }

}
