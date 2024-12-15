/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.runners.Traveller;
import codex.goldrunner.util.Index3i;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author gary
 */
public class PlatformControl extends UnitControl {

    public PlatformControl() {
    }

    public PlatformControl(LevelState level, Index3i index) {
        super(level, index);
    }

    @Override
    public boolean enter(Traveller travel, boolean force) {
        return travel.fallThroughPlatforms() || force;
    }

    @Override
    public boolean stand(Traveller travel) {
        return !travel.fallThroughPlatforms();
    }

    @Override
    public boolean physical() {
        return true;
    }

    @Override
    public String[] types() {
        return new String[]{"platform"};
    }

    @Override
    public Spatial loadSpatial(String type, boolean editor, AssetManager assets) {
        if (editor) {
            Geometry g = new Geometry("extra", new Box(.4f, .4f, .4f));
            Material m = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
            m.setColor("Color", ColorRGBA.DarkGray);
            g.setMaterial(m);
            Node n = new Node();
            n.attachChild(g);
            n.attachChild(loadSpatial(type, false, assets));
            return n;
        } else {
            return assets.loadModel("Models/units/platform.j3o");
        }
    }

    @Override
    public UnitControl loadControl(String type, LevelState level, Index3i index) {
        return new PlatformControl(level, index);
    }

}
