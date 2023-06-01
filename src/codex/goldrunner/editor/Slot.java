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
package codex.goldrunner.editor;

import codex.goldrunner.game.BackgroundElement;
import codex.goldrunner.game.LevelState;
import codex.goldrunner.items.ItemCarrier;
import codex.goldrunner.items.ItemControl;
import codex.goldrunner.runners.RunnerControl;
import codex.goldrunner.units.UnitLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.event.MouseListener;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class Slot implements ItemCarrier {
	
	public static final String
			INDEX_X = "index_x",
			INDEX_Y = "index_y";
	private static final String
			UNIT = "unit spatial",
			BACKGROUND = "background spatial";
	
	AssetManager assets;
	Point index;
	Node n = new Node("[master]");
	Unit unit;
	BackgroundElement background;
	
	Slot(AssetManager assets, MouseListener listener, Point index) {
		this.assets = assets;
		this.index = index;
		n.addControl(new MouseEventControl(listener));
		n.setUserData(INDEX_X, index.x);
		n.setUserData(INDEX_Y, index.y);
		loadVisualBoundaries();
	}
	
	private void loadVisualBoundaries() {
		Geometry g = new Geometry("boundaries", new Quad(1f, 1f));
		g.setLocalTranslation(0, 0, -10f);
		Material m = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
		m.setColor("Color", ColorRGBA.Blue);
		m.getAdditionalRenderState().setWireframe(true);
		g.setMaterial(m);
		n.attachChild(g);
	}	
	
	public void setUnit(Unit unit) {
		n.detachChildNamed(UNIT);
		this.unit = unit;
		if (this.unit != null) {
			Node wrapper = new Node(UNIT);
			UnitLoader loader = LevelState.getUnitLoader(this.unit.getKey());
			Spatial u = loader.loadSpatial(this.unit.getKey(), true, assets);
			if (u != null) {
				wrapper.attachChild(u);
			}
			RunnerControl runner = loader.spawn(this.unit.getKey(), true, null, assets);
			if (runner != null) {
				wrapper.attachChild(runner.getSpatial());
			}
			ItemControl item = loader.createEditorItem(this.unit.getKey(), this, assets);
			if (item != null) {
				wrapper.attachChild(item.getSpatial());
			}
			n.attachChild(wrapper);
		}
	}
	
	public Unit getUnit() {
		return unit;
	}
	public BackgroundElement getBackgroundElement() {
		return background;
	}
	public Node getMasterNode() {
		return n;
	}
	public Vector3f getLocation() {
		return n.getLocalTranslation();
	}
	public Point getIndex() {
		return index;
	}

	@Override
	public ItemControl getItem() {
		return null;
	}
	@Override
	public void acceptItem(ItemControl item) {}
	@Override
	public void releaseItem() {}
	
}
