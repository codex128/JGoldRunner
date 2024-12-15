/*
 * The MIT License
 *
 * Copyright 2022 gary.
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
package codex.goldrunner.items;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.units.UnitControl;
import codex.goldrunner.units.UnitLoader;
import codex.goldrunner.util.Index3i;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Controls guns.
 *
 * Does not have anything to do with the political thingy.
 *
 * @author gary
 */
public class GunControl extends ItemControl implements UnitLoader {

    @Override
    public String[] types() {
        return new String[]{"gun"};
    }

    @Override
    public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
        return new Node();
    }

    @Override
    public UnitControl loadControl(String type, LevelState level, Index3i index) {
        return new UnitControl(level, index) {
        };
    }

    @Override
    public ItemControl createItem(String type, ItemCarrier wrapper, AssetManager assets) {
        return new GunControl();
    }

}
