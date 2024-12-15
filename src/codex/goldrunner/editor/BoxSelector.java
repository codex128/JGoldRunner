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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.awt.Point;
import java.util.function.Consumer;

/**
 *
 * @author gary
 */
public class BoxSelector {

    Slot[] slots;
    Geometry box;

    public void initializeBoxGeometry(AssetManager assets, ColorRGBA color) {
        box = new Geometry("box selector geometry", new Box(.5f, .5f, .5f));
        Material m = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", color);
        m.getAdditionalRenderState().setWireframe(true);
        box.setMaterial(m);
        box.setCullHint(Spatial.CullHint.Always);
        clear();
    }

    public Geometry getBoxGeometry() {
        return box;
    }

    protected Point getLowIndex() {
        Point low = new Point(-1, -1);
        low.x = Math.min(slots[0].getIndex().x, slots[1].getIndex().x);
        low.y = Math.min(slots[0].getIndex().y, slots[1].getIndex().y);
        return low;
    }

    protected Point getHighIndex() {
        Point hi = new Point(-1, -1);
        hi.x = Math.max(slots[0].getIndex().x, slots[1].getIndex().x);
        hi.y = Math.max(slots[0].getIndex().y, slots[1].getIndex().y);
        return hi;
    }

    protected void calculateBoxTransform() {
        box.setCullHint(Spatial.CullHint.Never);
        box.setLocalTranslation(slots[0].getLocation()
                .add(slots[1].getLocation()).divideLocal(2f).setZ(10f));
        int w = Math.abs(slots[0].getIndex().x - slots[1].getIndex().x);
        int h = Math.abs(slots[0].getIndex().y - slots[1].getIndex().y);
        box.setLocalScale(w + 1, h + 1, 1f);
    }

    public Slot[] getSlots() {
        return slots;
    }

    public void placePrimarySlot(Slot slot) {
        slots = new Slot[]{slot, slot};
        calculateBoxTransform();
    }

    public void placeSecondarySlot(Slot slot) {
        if (slots == null) {
            return;
        }
        slots[1] = slot;
        calculateBoxTransform();
    }

    public void clear() {
        slots = null;
        box.setCullHint(Spatial.CullHint.Always);
    }

    public boolean active() {
        return slots != null;
    }

    public void forEachContained(Slot[][] slots, Consumer<Slot> foreach) {
        if (!active()) {
            return;
        }
        Point low = getLowIndex();
        Point hi = getHighIndex();
        int width = slots[0].length;
        int height = slots.length;
        for (int i = low.y; i >= 0 && i < height && i <= hi.y; i++) {
            for (int j = low.x; j >= 0 && j < width && j <= hi.x; j++) {
                foreach.accept(slots[i][j]);
            }
        }
    }

}
