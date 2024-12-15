/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.items.ItemControl;
import codex.goldrunner.runners.RunnerControl;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import codex.goldrunner.items.ItemCarrier;
import codex.goldrunner.util.Index3i;

/**
 *
 * @author gary
 */
public interface UnitLoader {

    public abstract String[] types();

    public abstract Spatial loadSpatial(String type, boolean editor, AssetManager assets);

    public abstract UnitControl loadControl(String type, LevelState level, Index3i index);

    public default RunnerControl spawn(String type, boolean editor, UnitControl wrapper, AssetManager assets) {
        return null;
    }

    public default ItemControl createItem(String type, ItemCarrier wrapper, AssetManager assets) {
        return null;
    }

    public default ItemControl createEditorItem(String type, ItemCarrier wrapper, AssetManager assets) {
        return createItem(type, wrapper, assets);
    }

}
