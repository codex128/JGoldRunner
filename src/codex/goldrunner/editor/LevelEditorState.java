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

import codex.goldrunner.GameGlobals;
import codex.goldrunner.game.BackgroundElement;
import codex.goldrunner.game.LevelListener;
import codex.goldrunner.game.LevelState;
import codex.goldrunner.game.management.LevelData;
import codex.goldrunner.util.IncompatibilityException;
import codex.j3map.J3map;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.event.MouseListener;
import com.simsilica.lemur.event.PopupState;
import java.awt.Point;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 *
 * @author gary
 */
public class LevelEditorState extends BaseAppState implements
		LevelListener, MouseListener {
	
	private static final String
			NEUTRAL = " ",
			SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final Point
			DEFAULT_SIZE = new Point(20, 20);
	
	MapEditor editor;
	EditorClient client;
	LevelData data;
	LevelData testing;
	Slot[][] slots;
	Slot hero;
	Camera mapcam;
	ViewPort mapview;
	Node gui = new Node("gui");
	Node grid = new Node("grid");
	Container tools = new Container();
	float zoom = 20f;
	String name = "???";
	String creator = "???";
	Container details;
	
	public LevelEditorState() {
		setEnabled(false);
	}
	
	@Override
	protected void initialize(Application app) {
		// scene
		mapcam = getApplication().getGuiViewPort().getCamera().clone();
		mapcam.setViewPort(.25f, .9f, .25f, .9f);
		grid.setLocalTranslation(200, 60, 0);
		grid.setLocalScale(zoom);
		gui.attachChild(tools);
		gui.attachChild(grid);
		tools.setLocalTranslation(20, 470, 0);
		tools.setPreferredSize(new Vector3f(150, 400, 0));
		Node probeNode = (Node)app.getAssetManager().loadModel("Scenes/defaultProbe.j3o");
		LightProbe probe = (LightProbe)probeNode.getLocalLightList().iterator().next();
		grid.addLight(probe);
		GameGlobals.applyHexDirectionalLighting(grid, ColorRGBA.Gray);
		
		Container dash = new Container();
		gui.attachChild(dash);
		dash.setLocalTranslation(300, 40, 0);
		dash.setLayout(new BoxLayout(Axis.X, FillMode.Even));
		dash.addChild(new Button("Save")).addClickCommands((source) -> {
			exportLevel();
		});
		dash.addChild(new Button("Details")).addClickCommands((source) -> {
			GameGlobals.getChild(details, TextField.class, "name").setText(name);
			GameGlobals.getChild(details, TextField.class, "creator").setText(creator);
			getState(PopupState.class).showPopup(details);
		});
		dash.addChild(new Button("Test")).addClickCommands((source) -> {
			setEnabled(false);
			testing = new LevelData(writeLevelToSource(new J3map()));
			LevelState test = new LevelState(this, testing);
			getStateManager().attach(test);
		});
		dash.addChild(new Button("Exit")).addClickCommands((source) -> {
			setEnabled(false);
			client.onExit();
		});
		setMapEditor(new UnitEditor(this));
		
		// details popup
		details = new Container();
		details.setLocalTranslation(200f, 300f, 20f);
		details.addChild(new TextField(name)).setName("name");
		details.addChild(new TextField(creator)).setName("creator");
		details.addChild(new Button("Confirm")).addClickCommands((source) -> {
			name = GameGlobals.getChild(details, TextField.class, "name").getText();
			creator = GameGlobals.getChild(details, TextField.class, "creator").getText();
			getState(PopupState.class).closePopup(details);
		});
		details.addChild(new Button("Cancel")).addClickCommands((source) -> {
			getState(PopupState.class).closePopup(details);
		});
	}
	@Override
	protected void cleanup(Application app) {}
	@Override
	protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(gui);
//		mapview = getApplication()
//				.getRenderManager().createPostView("mapview", mapcam);
//		mapview.setClearFlags(true, true, true);
//		mapview.attachScene(grid);
//		mapview.setBackgroundColor(ColorRGBA.DarkGray);
	}
	@Override
	protected void onDisable() {
		gui.removeFromParent();
//		getApplication().getRenderManager().removePostView(mapview);
	}
	@Override
	public void update(float tpf) {
		grid.updateLogicalState(tpf);
	}
	@Override
	public void render(RenderManager rm) {
		grid.updateGeometricState();
	}
	
	public void setMapEditor(MapEditor editor) {
		if (this.editor != null) {
			tools.detachChild(this.editor.getToolsGui());
			this.editor.onDisable();
		}
		this.editor = editor;
		if (this.editor != null) {
			this.editor.onEnable();
			tools.addChild(this.editor.getToolsGui());
		}
	}
	public MapEditor getCurrentMapEditor() {
		return editor;
	}
	
	private void clear() {
		data = null;
		if (slots == null) return;
		forEachSlot((slot) -> {
			grid.detachChild(slot.getMasterNode());
		});
		slots = null;
	}
	public void createNewLevel(EditorClient client) {
		clear();
		setEnabled(true);
		this.client = client;
		name = "???";
		creator = "???";
		loadNewLevel();
	}
	public void importLevel(EditorClient client, LevelData data) {
		clear();
		setEnabled(true);
		this.client = client;
		this.data = data;
		name = this.data.getSource().getString("name");
		creator = this.data.getSource().getString("creator");
		loadExistingLevel();
	}
	public void exportLevel() {
		if (client == null) return;
		J3map save;
		if (data != null) {
			save = data.getSource();
			// this is to convert older formats to the current format
			save.clear();
		}
		else save = new J3map();
		writeLevelToSource(save);
		if (data == null) {
			data = new LevelData(save);
			client.saveNewLevelData(data);
		}
	}
	public J3map writeLevelToSource(J3map save) {
		save.store("version", LevelData.VERSION);
		save.store("name", ""+name);
		save.store("creator", ""+creator);
		J3map cipher = new J3map();
		J3map background = new J3map();
		String[] map = new String[slots.length];
		HashMap<String, String> convert = new HashMap<>();
		int x = 0, y = 0;
		int symbolindex = 0;
		int bgindex = 0;
		for (Slot[] row : slots) {
			String r = "";
			for (Slot slot : row) {
				if (slot.getUnit() != null) {
					String symbol = convert.get(slot.getUnit().getKey());
					if (symbol == null) {
						symbol = ""+SYMBOLS.charAt(symbolindex++);
						convert.put(slot.getUnit().getKey(), symbol);
						cipher.store(symbol, slot.getUnit().getKey());
					}
					r += symbol;
				}
				else {
					r += NEUTRAL;
				}
				if (slot.getBackgroundElement() != null) {
					slot.getBackgroundElement().setIndex(new Point(x, y));
					background.store(""+(bgindex++),
							slot.getBackgroundElement());
				}
				x++;
			}
			map[y++] = r;
		}
		save.overwrite("map", map);
		save.overwrite("cipher", cipher);
		//save.overwrite("background", background);
		return save;
	}
	
	private void loadExistingLevel() {
		AssetManager assets = getApplication().getAssetManager();
		J3map source = data.getSource();
		Integer version = source.getInteger("version");
		if (version != null && version > LevelData.VERSION) {
			throw new IncompatibilityException(
				"This version of JGoldRunner cannot read levels of version "+(LevelData.VERSION+1)+" or above!");
		}
		J3map cipher = source.getJ3map("cipher");
		if (cipher == null) cipher = data.getSource();
		String[] map = source.getStringArray("map");
		final int width = map[0].length(), height = map.length;
		createSlotGrid(width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Slot s = new Slot(assets, this, new Point(j, i));
				grid.attachChild(s.getMasterNode());
				s.getMasterNode().setLocalTranslation(j, height-i, 0);
				slots[i][j] = s;
				String key = cipher.getString(""+map[i].charAt(j));
				if (key != null) {
					s.setUnit(new Unit(key));
					if (key.equals("hero")) {
						setHeroSlot(s);
					}
				}
			}
		}
	}
	private void loadNewLevel() {
		AssetManager assets = getApplication().getAssetManager();
		int width = DEFAULT_SIZE.x;
		int height = DEFAULT_SIZE.y;
		createSlotGrid(width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Slot s = new Slot(assets, this, new Point(j, i));
				grid.attachChild(s.getMasterNode());
				s.getMasterNode().setLocalTranslation(j, height-i, 0);
				slots[i][j] = s;
			}
		}
	}
	private void createSlotGrid(int width, int height) {
		slots = new Slot[height][width];
	}
	
	public Slot[][] getSlots() {
		return slots;
	}
	public Node getGridNode() {
		return grid;
	}
	public Node getGuiNode() {
		return gui;
	}
	
	public void setSlotUnit(Slot slot, Unit unit) {
		if (unit != null && unit.getKey().equals("hero")) setHeroSlot(slot);
		else if (slot == hero) setHeroSlot(null);
		slot.setUnit(unit);
	}
	
	public Slot getSlotByIndex(Point index) {		
		return slots[index.y][index.x];
	}
	public Slot getSlotBySpatial(Spatial spatial) {
		Integer x = spatial.getUserData(Slot.INDEX_X);
		if (x == null) return null;
		Integer y = spatial.getUserData(Slot.INDEX_Y);
		if (y == null) return null;
		return slots[y][x];
	}
	public void forEachSlot(Consumer<Slot> foreach) {
		for (Slot[] row : slots) {
			for (Slot slot : row) {
				foreach.accept(slot);
			}
		}
	}
	
	public void setHeroSlot(Slot slot) {
		if (hero != null) {
			hero.setUnit(null);
		}
		hero = slot;
		if (hero != null) {
			hero.setUnit(new Unit("hero"));
		}
	}
	public Slot getHeroSlot() {
		return hero;
	}

	@Override
	public void onHeroDeath(LevelState level) {}
	@Override
	public void onFailure(LevelState level) {
		onRestart(level);
	}
	@Override
	public void onVictory(LevelState level) {
		onQuit(level);
	}
	@Override
	public void onRestart(LevelState level) {
		if (testing != null) level.load(this, testing);
		else onQuit(level);
	}
	@Override
	public void onSkimp(LevelState level) {
		onQuit(level);
	}
	@Override
	public void onQuit(LevelState level) {
		getStateManager().detach(level);
		setEnabled(true);
		testing = null;
	}
	@Override
	public void onError(LevelState level, String reason) {
		getState(OptionPanelState.class).show(
				"A Fatal Error has Occured!", "Reason: "+reason);
		onQuit(level);
	}

	@Override
	public void mouseButtonEvent(MouseButtonEvent mbe,
			Spatial sptl, Spatial sptl1) {
		if (editor != null) {
			editor.mouseButtonEvent(mbe, sptl, sptl1);
		}
	}
	@Override
	public void mouseEntered(MouseMotionEvent mme,
			Spatial sptl, Spatial sptl1) {
		if (editor != null) {
			editor.mouseEntered(mme, sptl, sptl1);
		}
	}
	@Override
	public void mouseExited(MouseMotionEvent mme,
			Spatial sptl, Spatial sptl1) {
		if (editor != null) {
			editor.mouseExited(mme, sptl, sptl1);
		}
	}
	@Override
	public void mouseMoved(MouseMotionEvent mme,
			Spatial sptl, Spatial sptl1) {
		if (editor != null) {
			editor.mouseMoved(mme, sptl, sptl1);
		}
	}
	
}
