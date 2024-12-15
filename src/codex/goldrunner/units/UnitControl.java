/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
import codex.goldrunner.game.MapFace;
import codex.goldrunner.items.ItemControl;
import codex.goldrunner.runners.Traveller;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import codex.goldrunner.items.ItemCarrier;
import codex.goldrunner.util.Index3i;
import com.jme3.anim.AnimComposer;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;

/**
 *
 * @author gary
 */
public abstract class UnitControl extends AbstractControl implements PhysicsControl, UnitLoader, ItemCarrier {

    public static final int NONE = -1, U = 0, R = 1, D = 2, L = 3, IN = 4, DR = 5, DL = 6;
    public static final int FORE = 0, MID = 1, BACK = 2;
    LevelState level;
    Index3i index;
    ItemControl item;
    PhysicsSpace space;
    RigidBodyControl rigidBody;

    /**
     * For internal use only.
     */
    public UnitControl() {
    }

    /**
     * Initialize a new UnitControl.
     *
     * @param level level this unit is part of
     * @param index index of the unit: x and y define position of face, z
     * defines face.
     */
    public UnitControl(LevelState level, Index3i index) {
        this.level = level;
        this.index = index;
        initialize();
    }

    protected void initialize() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        UnitControl control = new UnitControl(level, index) {
        };
        //TODO: copy parameters to new Control
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }

    public void dig() {
        // destroy this unit's spatial
    }

    public UnitControl getAdjacent(int direction) {
        switch (direction) {
            case U:
                return getUp();
            case R:
                return getRight();
            case D:
                return getDown();
            case L:
                return getLeft();
            case DR:
                return getDownRight();
            case DL:
                return getDownLeft();
            case IN:
                return getIn();
            default:
                return null;
        }
    }

    public UnitControl getRelative(int direction) {
        switch (direction) {
            case U:
                return getRelativeUnit(0, -1);
            case R:
                return getRelativeUnit(1, 0);
            case D:
                return getRelativeUnit(0, 1);
            case L:
                return getRelativeUnit(-1, 0);
            case DR:
                return getRelativeUnit(1, 1);
            case DL:
                return getRelativeUnit(-1, 1);
            default:
                return null;
        }
    }

    public UnitControl getUp() {
        if (getFace().isGravityInfluenced()) {
            return null;
        }
        return MapFace.getAdjacentUnit(this, U);
    }

    public UnitControl getIn() {
        return MapFace.getAdjacentUnit(this, IN);
    }

    public UnitControl getRight() {
        return MapFace.getAdjacentUnit(this, R);
    }

    public UnitControl getDown() {
        return MapFace.getAdjacentUnit(this, D);
    }

    public UnitControl getLeft() {
        return MapFace.getAdjacentUnit(this, L);
    }

    public UnitControl getDownRight() {
        return MapFace.getAdjacentUnit(this, DR);
    }

    public UnitControl getDownLeft() {
        return MapFace.getAdjacentUnit(this, DL);
    }

    public boolean isAdjacentTo(UnitControl unit) {
        for (int i = 0; i <= L; i++) {
            UnitControl a = MapFace.getAdjacentUnit(this, i);
            if (a != null && a == unit) {
                return true;
            }
        }
        return false;
    }

    public int getDirectionTo(UnitControl unit) {
        for (int i = 0; i <= IN; i++) {
            if (getAdjacent(i) == unit) {
                return i;
            }
        }
        return NONE;
    }

    public int getRelativeTo(UnitControl unit) {
        for (int i = 0; i < DL + 1; i++) {
            if (getRelative(i) == unit) {
                return i;
            }
        }
        return NONE;
    }

    public UnitControl getUnitAtIndex(int face, int x, int y) {
        if (face < 0 || face >= level.getFaces().length
                || y < 0 || y >= level.getUnitsForFace(face).length
                || x < 0 || x >= level.getUnitsForFace(face)[y].length) {
            return null;
        } else {
            return level.getUnitsForFace(face)[y][x];
        }
    }

    public UnitControl getRelativeUnit(int x, int y) {
        return getUnitAtIndex(index.z, index.x + x, index.y + y);
    }

    public boolean enter(Traveller travel, boolean force) {
        // checks if the person may enter this unit
        return true;
    }

    public boolean exit(Traveller travel, boolean force) {
        // checks if the person may exit this unit
        return true;
    }

    public boolean inBounds(Vector3f vec) {
        Vector3f loc = spatial.getWorldTranslation();
        float rad = LevelState.UNIT_SIZE / 2;
        return vec.x > loc.x - rad && vec.x < loc.x + rad
                && vec.y > loc.y - rad && vec.y < loc.y + rad
                && vec.z > loc.z - rad && vec.z < loc.z + rad;
    }

    public boolean diggable() {
        // air cannot be dug
        return false;
    }

    public boolean grabbable() {
        // air cannot be grabbed to
        return false;
    }

    public boolean stand(Traveller travel) {
        return solid();
    }

    public boolean solid() {
        return false;
    }

    public boolean killer() {
        return false;
    }

    public boolean fumble() {
        // forces runners to drop item on enter
        return false;
    }

    public boolean escapable() {
        return false;
    }

    public boolean goal() {
        return false;
    }

    public boolean physical() {
        return false;
    }

    public int zIndex() {
        // back = behind everything and does not block digging
        return BACK;
    }

    @Override
    public ItemControl getItem() {
        return item;
    }

    @Override
    public boolean canAcceptItem(ItemControl item) {
        return this.item == null && getNode() != null;
    }

    @Override
    public void acceptItem(ItemControl item) {
        Node n = getNode();
        if (n != null) {
            this.item = item;
            n.attachChild(this.item.getSpatial());
            this.item.getSpatial().getControl(AnimComposer.class).setCurrentAction("float");
        }
    }

    @Override
    public void releaseItem() {
        //getNode().detachChild(item.getSpatial());
        item.getSpatial().getControl(AnimComposer.class).setCurrentAction("normal");
        item = null;
    }

    protected Node getNode() {
        if (!(spatial instanceof Node)) {
            return null;
        } else {
            return (Node) spatial;
        }
    }

    public Index3i getIndex() {
        return index;
    }

    public LevelState getLevel() {
        return level;
    }

    public MapFace getFace() {
        return getLevel().getFace(index.z);
    }

    public static Integer getDiagonalVersion(int direction) {
        switch (direction) {
            case R:
                return DR;
            case L:
                return DL;
            default:
                return null;
        }
    }

    public static boolean isOrthogonal(int direction) {
        return isHorizontal(direction) || isVerticle(direction);
    }

    public static boolean isHorizontal(int direction) {
        return direction == R || direction == L;
    }

    public static boolean isVerticle(int direction) {
        return direction == U || direction == D;
    }

    public static boolean isDiagonal(int direction) {
        return direction == DR || direction == DL;
    }

    public static Integer reverseDirection(int direction) {
        switch (direction) {
            case U:
                return D;
            case R:
                return L;
            case D:
                return U;
            case L:
                return R;
            default:
                return null;
        }
    }

    public static Index3i generateDirectional(int direction) {
        switch (direction) {
            case U:
                return new Index3i(0, -1, 0);
            case R:
                return new Index3i(1, 0, 0);
            case DR:
                return new Index3i(1, 1, 0);
            case D:
                return new Index3i(0, 1, 0);
            case DL:
                return new Index3i(-1, 1, 0);
            case L:
                return new Index3i(-1, 0, 0);
            case IN:
                return new Index3i(0, 0, 0);
            default:
                throw new IllegalArgumentException("Invalid direction integer " + direction + "!");
        }
    }

    public static int[] getHorizontalDirections() {
        return new int[]{R, L};
    }

    public static int[] getVerticleDirections() {
        return new int[]{U, D};
    }

    public static int[] getDiagonalDirections() {
        return new int[]{DR, DL};
    }

    public static int[] getOrthogonalDirections() {
        return new int[]{U, R, D, L, IN};
    }

    @Override
    public String[] types() {
        return new String[]{LevelState.DEFAULT_LOADER};
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
    public PhysicsSpace getPhysicsSpace() {
        return space;
    }

    @Override
    public void setPhysicsSpace(PhysicsSpace space) {
        if (true || this.space == space) {
            return;
        }
        if (space != null) {
            this.space = space;
            if (!physical()) {
                return;
            }
            if (spatial == null) {
                throw new IllegalStateException("Must have spatial to initialize physics!");
            }
            initializePhysicsBody();
        } else {
            if (rigidBody != null) {
                cleanupPhysicsBody();
            }
            this.space = null;
        }
    }

    protected void initializePhysicsBody() {
        rigidBody = new RigidBodyControl(0f);
        spatial.addControl(rigidBody);
        space.add(rigidBody);
        rigidBody.setPhysicsLocation(spatial.getWorldTranslation());
        rigidBody.setPhysicsRotation(spatial.getWorldRotation());
    }

    protected void cleanupPhysicsBody() {
        rigidBody.getSpatial().removeControl(rigidBody);
        space.remove(rigidBody);
        rigidBody = null;
    }

}
