/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.units;

import codex.goldrunner.game.LevelState;
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
import java.awt.Point;
import java.io.IOException;
import codex.goldrunner.items.ItemCarrier;
import com.jme3.anim.AnimComposer;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author gary
 */
public abstract class UnitControl extends AbstractControl
		implements UnitLoader, ItemCarrier {
	
	public static final int NONE = -1, U = 0, R = 1, D = 2, L = 3,
			DR = 4, DL = 5;
	public static final int FORE = 0, MID = 1, BACK = 2;
	LevelState level;
	Point index;
	ItemControl item;
	
	/**
	 * For internal use only.
	 */
	public UnitControl() {}
	public UnitControl(LevelState level, Point index) {
		this.level = level;
		this.index = index;
		initialize();
	}
	
	
	protected void initialize() {}
	
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
		UnitControl control = new UnitControl(level, index){};
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
			case U: return getUp();
			case R: return getRight();
			case D: return getDown();
			case L: return getLeft();
			case DR: return getDownRight();
			case DL: return getDownLeft();
			default: return null;
		}
	}
	public UnitControl getRelative(int direction) {
		switch (direction) {
			case U: return getRelativeUnit(0, -1);
			case R: return getRelativeUnit(1, 0);
			case D: return getRelativeUnit(0, 1);
			case L: return getRelativeUnit(-1, 0);
			case DR: return getRelativeUnit(1, 1);
			case DL: return getRelativeUnit(-1, 1);
			default: return null;
		}
	}
	public UnitControl getUp() {
		return null;
	}
	public UnitControl getRight() {
		return getRelativeUnit(1, 0);
	}
	public UnitControl getDown() {
		return getRelativeUnit(0, 1);
	}
	public UnitControl getLeft() {
		return getRelativeUnit(-1, 0);
	}
	public UnitControl getDownRight() {
		return getRelativeUnit(1, 1);
	}
	public UnitControl getDownLeft() {
		return getRelativeUnit(-1, 1);
	}
	
	public boolean isAdjacentTo(UnitControl unit) {
		for (int i = 0; i < L+1; i++) {
			UnitControl a = getAdjacent(i);
			if (a != null && a == unit) return true;
		}
		return false;
	}
	public boolean isAdjacentToDiagonally(UnitControl unit) {
		return getAdjacent(DR) == unit || getAdjacent(DL) == unit;
	}
	public int getDirectionTo(UnitControl unit) {
		for (int i = 0; i < DL+1; i++) {
			if (getAdjacent(i) == unit) return i;
		}
		return NONE;
	}
	public int getRelativeTo(UnitControl unit) {
		for (int i = 0; i < DL+1; i++) {
			if (getRelative(i) == unit) return i;
		}
		return NONE;
	}
	public UnitControl getUnitAtIndex(int x, int y) {
		if (y < 0 || y >= level.getUnits().length ||
				x < 0 || x >= level.getUnits()[y].length) return null;
		else return level.getUnits()[y][x];
	}
	public UnitControl getRelativeUnit(int x, int y) {
		return getUnitAtIndex(index.x+x, index.y+y);
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
		Vector3f loc = spatial.getLocalTranslation();
		float rad = LevelState.UNIT_SIZE/2;
		return vec.x > loc.x-rad && vec.x < loc.x+rad &&
				vec.y > loc.y-rad && vec.y < loc.y+rad &&
				vec.z > loc.z-rad && vec.z < loc.z+rad;
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
		if (!(spatial instanceof Node)) return null;
		else return (Node)spatial;
	}	
	public Point getIndex() {
		return index;
	}
	public LevelState getLevel() {
		return level;
	}
	
	public static Integer getDiagonalVersion(int direction) {
		switch (direction) {
			case R: return DR;
			case L: return DL;
			default: return null;
		}
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
	public static float getZIndexRatio(int zindex) {
		switch (zindex) {
			case FORE: return 0;
			case MID: return .1f;
			case BACK: return -1;
			default: throw new IllegalArgumentException("No such z index");
		}
	}
	public static Integer reverseDirection(int direction) {
		switch (direction) {
			case U: return D;
			case R: return L;
			case D: return U;
			case L: return R;
			default: return null;
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
		return new int[]{U, R, D, L};
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
	public UnitControl loadControl(String type, LevelState level,
			Point index) {
		return new UnitControl(level, index) {};
	}
	
}
