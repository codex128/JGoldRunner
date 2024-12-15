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

import codex.j3map.J3map;
import com.jme3.asset.AssetManager;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.event.MouseListener;

/**
 *
 * @author gary
 */
public abstract class MapEditor implements MouseListener {

    LevelEditorState editor;
    AssetManager assets;
    J3map selections;
    Container tools = new Container("editor tools");

    public MapEditor(LevelEditorState editor) {
        this.editor = editor;
        assets = this.editor.getApplication().getAssetManager();
        selections = J3map.openJ3map(assets.loadAsset("Interface/selections.j3map"));
    }

    protected LevelEditorState getEditor() {
        return editor;
    }

    protected AssetManager getAssets() {
        return assets;
    }

    protected J3map getSelectionSource() {
        return selections;
    }

    protected Slot[][] getSlots() {
        return editor.getSlots();
    }

    protected Container getToolsGui() {
        return tools;
    }

    protected abstract void initialize();

    protected abstract void onEnable();

    protected abstract void onDisable();

    @Override
    public void mouseExited(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
    }

    @Override
    public void mouseMoved(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
    }

}
