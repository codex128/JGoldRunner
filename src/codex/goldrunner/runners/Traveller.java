/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.goldrunner.runners;

import codex.goldrunner.units.UnitControl;
import com.jme3.bounding.BoundingVolume;

/**
 *
 * @author gary
 */
public interface Traveller {

    public abstract UnitControl getOccupied();

    public default boolean fallThroughHoles() {
        return false;
    }

    public default boolean fallThroughPlatforms() {
        return true;
    }

    public abstract boolean kill();

    public abstract BoundingVolume getWorldBound();

}
