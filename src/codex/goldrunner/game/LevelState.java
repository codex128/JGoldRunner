/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.game;

import codex.goldrunner.AudioLibrary;
import codex.goldrunner.SettingsState;
import codex.goldrunner.game.management.LevelData;
import codex.goldrunner.items.*;
import codex.goldrunner.runners.*;
import codex.goldrunner.units.UnitControl;
import codex.goldrunner.units.UnitLoader;
import codex.goldrunner.util.Index3i;
import codex.goldrunner.util.JoystickEventListener;
import codex.goldrunner.util.TrackerLight;
import codex.jmeutil.listen.Listenable;
import codex.jmeutil.Timer;
import codex.jmeutil.TimerListener;
import codex.jmeutil.Trash;
import codex.j3map.J3map;
import codex.jmeutil.Motion;
import codex.jmeutil.character.OrbitalCamera;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.JoystickButton;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 *
 * @author gary 
 */
public class LevelState extends BaseAppState implements ActionListener,
		Listenable<LevelListener>, TimerListener, JoystickEventListener {
	
	public static final String DEFAULT_LOADER = "[DEFAULT_LOADER]";
	public static final float UNIT_SIZE = 1f;
	private static final HashMap<String, UnitLoader> loaders = new HashMap<>();
	public static final int
			SOUTH = 0, WEST = 1, NORTH = 2, EAST = 3, UP = 4;
	public static final int TOP_MARGIN = 6;
	
	LevelListener client;
	OrbitalCamera chase;
	LevelData data;
	Node scene = new Node("game scene");
	Node gui = new Node("gui scene");
	Node level = new Node("level scene");
	Node background = new Node("background scene");
	TrackerLight[] trackers;
	ConcurrentLinkedQueue<LevelListener> listeners = new ConcurrentLinkedQueue<>();
	MapFace[] faces;
	HeroControl hero;
	LinkedList<RunnerControl> runners = new LinkedList<>();
	LinkedList<EnemyControl> enemies = new LinkedList<>();
	int gold = 0;
	boolean is3D = false;
	boolean allgoldcollected = false;
	Timer endtimer = new Timer(.3f);
	AudioNode music;
	Trash trash = new Trash();
	
    
	public LevelState(LevelListener client, LevelData data) {
		this.client = client;
		this.data = data;
	}
	
	
    @Override
    protected void initialize(Application app) {
		// input
		app.getInputManager().addListener(this, "r", "backspace", "end");
		app.getInputManager().addRawInputListener(this);
		
		// scene
		((SimpleApplication)app).getRootNode().attachChild(scene);
		((SimpleApplication)app).getGuiNode().attachChild(gui);
		Texture tex = app.getAssetManager().loadTexture("Textures/lagoon_south.jpg");
		tex.setWrap(Texture.WrapMode.Repeat);
		Spatial sky = SkyFactory.createSky(app.getAssetManager(), tex, tex,
				tex, tex, tex, tex);
		scene.attachChild(sky);
		scene.attachChild(level);
		scene.attachChild(background);
		background.setLocalTranslation(0, 0, -UNIT_SIZE*2);
		
		// lighting
		scene.addLight(new DirectionalLight(new Vector3f(1, -1, -1), new ColorRGBA(96f/255f, 149f/255f, 164f/255f, 1f)));
		scene.addLight(new AmbientLight(ColorRGBA.DarkGray));
		trackers = new TrackerLight[2];
		trackers[0] = new TrackerLight(new Vector3f(0f, 0f, 10f));
		trackers[1] = new TrackerLight(new Vector3f(20f, 0f, 10f));
		for (TrackerLight light : trackers) {
			light.setColor(ColorRGBA.Yellow);
			light.setSpotRange(0f);
			light.setSpotInnerAngle(FastMath.PI*.001f);
			light.setSpotOuterAngle(FastMath.PI*.015f);
			//scene.addLight(light);
		}
		
		// heads up display
		Container hud = new Container();
		gui.attachChild(hud);
		
		// camera
		chase = new OrbitalCamera(app.getCamera(), GuiGlobals.getInstance().getInputMapper());
		GuiGlobals.getInstance().getInputMapper().activateGroup(OrbitalCamera.INPUT_GROUP);
		chase.getDistanceDomain().set(null, null);
		chase.setDistance(18f);
		//chase.setHorizontalAngle(FastMath.HALF_PI);
		chase.setMotion(Motion.INSTANT);
		chase.setLocationOffset(new Vector3f(0, 1, 0));
		endtimer.addListener(this);
		
		// music
		music = new AudioNode(getStateManager().getState(AudioLibrary.class)
				.getAudioData("Game"), new AudioKey());
		music.setPositional(false);
		music.setLooping(true);
		music.setVolume(.5f);
		scene.attachChild(music);
		if (SettingsState.getInstance().musicEnabled()) {
			music.play();
		}
		
		// load level
		load(client, data);
	}
    @Override 
    protected void cleanup(Application app) {
		destroy();
		scene.detachAllChildren();
		scene.removeFromParent();
		gui.detachAllChildren();
		gui.removeFromParent();
		level.detachAllChildren();
		background.detachAllChildren();
		listeners.clear();
		music.stop();
		app.getInputManager().removeListener(this);
		app.getInputManager().removeRawInputListener(this);
		((SimpleApplication)app).getRootNode().detachChild(scene);
		((SimpleApplication)app).getGuiNode().detachChild(gui);
	}
    @Override
    protected void onEnable() {
		if (hero != null) getApplication().getInputManager()
				.addListener(hero, hero.getInputMappings());
		getApplication().getInputManager().setCursorVisible(false);
	}	
	@Override
    protected void onDisable() {
		//level.detachAllChildren();
		//enemies.clear();
		getApplication().getInputManager().removeListener(hero);
		getApplication().getInputManager().setCursorVisible(true);
		//gold = 0;
		//allgoldcollected = false;
	}
    @Override
    public void update(float tpf) {
		if (!hero.isAlive() && hero.isEnabled() && !endtimer.isRunning()) {
			endtimer.start();
			for (RunnerControl runner : runners) runner.pause(true);
			notifyListeners(l -> l.onHeroDeath(this));
		}
		endtimer.update(tpf);
		if (endtimer.isRunning()) return;
		for (TrackerLight light : trackers) {
			light.updateDirection(tpf);
		}
		if (hero.getGoldCollected() >= gold && !allgoldcollected) {
			allgoldcollected = true;
			notifyListeners(l -> {
				l.onAllGoldCollected(this);
			});
		}
		if (allgoldcollected && hero.getOccupied().goal()) {
			System.out.println("victory");
			notifyListeners(l -> l.onVictory(this));
		}
		for (EnemyControl enemy : enemies) {			
			if (!enemy.isAlive()) {
				UnitControl spawn = enemy.getSpawnableUnit();
				if (spawn != null) {
					enemy.getSpatial().setLocalTranslation(spawn.getSpatial()
							.getLocalTranslation().add(0, 10f, 0));
					enemy.warp(spawn, "fall");
					enemy.setAlive(true);
				}
			}
		}
		trash.clear();
	}
	
	public void restart() {
		for (LevelListener l : listeners) l.onRestart(this);
		client.onRestart(this);
	}
	public void destroy() {
		BulletAppState bullet = getState(BulletAppState.class);
		if (faces != null) for (MapFace face : faces) {
			if (bullet != null && bullet.getPhysicsSpace() != null) for (int i = 0; i < face.getMap().length; i++) {
				for (int j = 0; j < face.getMap()[i].length; j++) {
					bullet.getPhysicsSpace().remove(face.getUnit(j, i));
				}
			}
			face.detachAllChildren();
		}
		level.detachAllChildren();
		background.detachAllChildren();
		gui.detachAllChildren();
		for (TrackerLight light : trackers) {
			light.track(null);
		}
		listeners.clear();
		runners.clear();
		enemies.clear();
		gold = 0;
		allgoldcollected = false;
		client = null;
		faces = null;
		if (hero != null) {
			hero.getSpatial().removeControl(chase);
			getApplication().getInputManager().removeListener(hero);
			getApplication().getInputManager().removeRawInputListener(hero);
		}
		hero = null;
	}
	public void load(LevelListener client, LevelData data) {
		destroy();
		this.client = client;
		this.data = data;
		Integer version = this.data.getSource().getInteger("version");
		if (version != null && version > LevelData.VERSION) {
			notifyListeners(l -> l.onError(this, "Your JGoldRunner version is too old\nto run levels of version "+(LevelData.VERSION+1)+" or above!"));
		}
		is3D = this.data.getSource().getBoolean("3D", false);
		loadMap();
		if (hero == null) {
			notifyListeners(l -> l.onError(this, "No Hero found in Level!"));
		}
		else {
			gui.attachChild(hero.getHUD());
			level.attachChild(hero.getSpatial());
			scene.attachChild(background);
			for (TrackerLight light : trackers) {
				light.track(hero.getSpatial());
			}
			hero.setRunnerList(runners);
			for (EnemyControl enemy : enemies) {
				level.attachChild(enemy.getSpatial());
				enemy.setRunnerList(runners);
				enemy.chase(hero);
			}
			// input
			getApplication().getInputManager().addListener(hero,
					hero.getInputMappings());
			getApplication().getInputManager().addRawInputListener(hero);
			// fields for map padding demensions
			final int thickness = 1;
			final int height = 1;
			// load background quad
//			createLevelBackground(height, thickness, topmargin);
			// load outside concrete
			loadBorderConcrete(thickness, height);
			// load world scene
			createWorldBackground(1f);
			if (is3D) create3DWorld();
		}
	}
	
	private void loadMap() {		
		J3map cipher = data.getSource().getJ3map("cipher");
		if (cipher == null) cipher = data.getSource();
		faces = new MapFace[is3DFormat() ? 5 : 1];
		boolean escapable = false;
		for (int f = 0; f < faces.length; f++) {
			String[] map = data.getSource().getStringArray("map"+(f > 0 ? f : ""));
			if (map == null) {
				throw new IllegalStateException("Level data missing map"+(f > 0 ? f : "")+"!");
			}
			faces[f] = createMapFace(f);
			if (faces[f].load(map, cipher)) {
				escapable = true;
			}
			level.attachChild(faces[f]);
			faces[f].setLocalTranslation(getFaceTranslation(f));
			faces[f].setLocalRotation(getFaceRotation(f));
			faces[f].setAngleFacing(getFacingAngle(f));
		}		
		if (!escapable) {
			notifyListeners(l -> l.onError(this, "The level cannot be escaped from!"));
		}
	}
	protected void loadUnit(String key, int face, int x, int y) {
		AssetManager assets = getApplication().getAssetManager();
		UnitLoader loader = getUnitLoader(key);
		Spatial spatial = loader.loadSpatial(key, false, assets);
		UnitControl unit = loader.loadControl(key, this, new Index3i(x, y, face));
		spatial.addControl(unit);
		Vector3f location = getMapLocation(x, y, 0f);
		spatial.setLocalTranslation(location);
		faces[face].attachChild(spatial);
		faces[face].getMap()[y][x] = unit;
		// physics
		getState(BulletAppState.class).getPhysicsSpace().add(unit);
		// load runner
		RunnerControl person = loader.spawn(key, false, unit, assets);
		if (person != null) {
			level.attachChild(person.getSpatial());
			person.getSpatial().setLocalTranslation(location);
			if (hero == null && person instanceof HeroControl) {
				hero = (HeroControl)person;
				hero.getSpatial().addControl(chase);
				hero.setCameraController(chase);
			}
			else if (person instanceof EnemyControl) {
				enemies.addLast((EnemyControl)person);
			}
			runners.addLast(person);
		}
		// load item
		ItemControl item = loader.createItem(key, unit, assets);
		if (item != null) {
			if (item instanceof GoldControl) {
				gold++;
			}
		}
	}
	protected void loadColumn(String key, int face, int column, int start, int end) {
		assert column >= 0 && column < faces[face].getMap()[0].length;
		for (int i = Math.max(start, 0); (end < 0 | i < end) && i < faces[face].getMap().length; i++) {
			loadUnit(key, face, column, i);
		}
	}
	private void loadBorderConcrete(int thickness, int height) {
//		UnitLoader loader = loaders.get("concrete");
//		AssetManager assets = getApplication().getAssetManager();
//		if (loader != null) {
//			for (int j = 0; j < thickness; j++) {
//				for (int i = -(height-1)+j+1; i <= units.length+thickness-1; i++) {
//					Spatial c1 = loader.loadSpatial("concrete", false, assets);
//					Spatial c2 = loader.loadSpatial("concrete", false, assets);
//					c1.setLocalTranslation(getMapLocation(-1-j, i, 0f));
//					c2.setLocalTranslation(getMapLocation(units[0].length+j, i, 0f));
//					level.attachChild(c1);
//					level.attachChild(c2);
//				}
//			}
//			for (int i = 0; i < units[0].length; i++) {
//				for (int j = 0; j < thickness; j++) {
//					Spatial c2 = loader.loadSpatial("concrete", false, assets);
//					c2.setLocalTranslation(getMapLocation(i, units.length+j, 0f));
//					level.attachChild(c2);
//				}
//			}
//		}
	}
	private void createLevelBackground(int height, int thickness, int topmargin) {
//		AssetManager assets = getApplication().getAssetManager();
//		J3map bg = data.getSource().getJ3map("background");
//		if (bg == null || true) {
//			// texturing is sketchy
//			Quad q = new Quad(units[0].length, units.length);
//			Texture tex = assets.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
//			Material mat = new Material(assets, "Common/MatDefs/Light/Lighting.j3md");
//			tex.setWrap(Texture.WrapMode.Repeat);
//			mat.setTexture("DiffuseMap", tex);
//			q.scaleTextureCoordinates(new Vector2f(1/units[0].length, 1/units.length));
//			Geometry quad = new Geometry("quad", q);
//			quad.setMaterial(mat);
//			quad.setLocalTranslation(-.5f, -units.length-.5f, -.5f);
//			level.attachChild(quad);
//		}
//		else {
//			bg.forEachType(BackgroundElement.class, (property) -> {
//				Spatial spatial = assets.loadModel(property.getModel());
//				spatial.setLocalTranslation(getMapLocation(
//						property.getIndex().x,
//						property.getIndex().y+topmargin,
//						UNIT_SIZE));
//				Material m = new Material(assets, "Common/MatDefs/Light/Lighting.j3md");
//				m.setTexture("DiffuseMap", assets.loadTexture("Textures/Room Texture.png"));
//				//spatial.setMaterial(m);
//				background.attachChild(spatial);
//			});
//		}
	}
	private void createWorldBackground(float depth) {
//		AssetManager assets = getApplication().getAssetManager();
//		final float size = 4f;
//		final float distance = 40f;
//		Spatial front = assets.loadModel("Models/cityscape.j3o");
//		front.setLocalTranslation(getMapLocation(
//				units[units.length-1].length/2, units.length, -distance));
//		front.setLocalScale(size);
//		Material fm = new Material(assets, "Common/MatDefs/Light/Lighting.j3md");
//		fm.setBoolean("UseMaterialColors", true);
//		fm.setColor("Diffuse", new ColorRGBA(.1f, .1f, .1f, 1f));
//		front.setMaterial(fm);
//		background.attachChild(front);
//		Spatial back = front.clone(false);
//		back.move(5f, 5f, -20f);
//		back.setLocalScale(-size*1.2f, size*1.2f, size*1.2f);
//		Material bm = fm.clone();
//		bm.setColor("Diffuse", new ColorRGBA(0f, 0f, 0f, 1f));
//		bm.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
//		back.setMaterial(bm);
//		background.attachChild(back);
	}
	private void create3DWorld() {
		AssetManager assets = getApplication().getAssetManager();
		Geometry cube = new Geometry("inner_cube", new Box(10*UNIT_SIZE, 10*UNIT_SIZE, 10*UNIT_SIZE));
		cube.setLocalTranslation(9.5f*UNIT_SIZE, -(10.5f+TOP_MARGIN)*UNIT_SIZE, -10.5f*UNIT_SIZE);
		Material mat = new Material(assets, "Common/MatDefs/Light/Lighting.j3md");
		mat.setTexture("DiffuseMap", assets.loadTexture("Textures/concrete.png"));
		cube.setMaterial(mat);
		level.attachChild(cube);
	}
	private MapFace createMapFace(int index) {
		if (index != LevelState.UP) {
			return new MapFace(this, index);
		}
		else {
			return new FlatMapFace(this, index, TOP_MARGIN);
		}
	}
	private Vector3f getFaceTranslation(int index) {
		switch (index) {
			case 1: return new Vector3f(-UNIT_SIZE, 0f, -(faces[1].getMap()[0].length)*UNIT_SIZE);
			case 2: return new Vector3f((faces[0].getMap()[0].length-1)*UNIT_SIZE, 0f, -(faces[1].getMap()[0].length+1)*UNIT_SIZE);
			case 3: return new Vector3f(faces[0].getMap()[0].length*UNIT_SIZE, 0f, -UNIT_SIZE);
			case 4: return new Vector3f(0f, -TOP_MARGIN*UNIT_SIZE, -(faces[4].getMap().length)*UNIT_SIZE);
			default: return new Vector3f();
		}
	}
	private Quaternion getFaceRotation(int index) {
		switch (index) {
			case 1: return new Quaternion().fromAngleAxis(-FastMath.PI*0.5f, Vector3f.UNIT_Y);
			case 2: return new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
			case 3: return new Quaternion().fromAngleAxis(FastMath.PI*0.5f, Vector3f.UNIT_Y);
			case 4: return new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X);
			default: return new Quaternion();
		}
	}
	private float getFacingAngle(int index) {
		switch (index) {
			case 1: return FastMath.HALF_PI;
			case 2: return FastMath.PI;
			case 3: return FastMath.HALF_PI*3;
			default: return 0f;
		}
	}
	
	public LevelData getLevelData() {
		return data;
	}
	public boolean is3DFormat() {
		return is3D;
	}
	private Vector3f getMapLocation(float x, float y, float z) {
		return new Vector3f(x, -y, 0).multLocal(UNIT_SIZE).setZ(z);
	}
	private Vector3f getMapLocation(Point index, float z) {
		return getMapLocation(index.x, index.y, z);
	}
	private <T extends AppState> void onStateExists(Class<T> type, Consumer<T> command) {
		T state = getState(type);
		if (state != null) command.accept(state);
	}
	
	public static void registerUnitLoader(UnitLoader loader) {
		for (String str : loader.types()) {
			loaders.putIfAbsent(str, loader);
		}
	}
	public static UnitLoader getUnitLoader(String type) {
		if (type == null) return loaders.get(DEFAULT_LOADER);
		UnitLoader loader = getDedicatedUnitLoader(type);
		if (loader != null) return loader;
		else return loaders.get(DEFAULT_LOADER);
	}
	public static UnitLoader getDedicatedUnitLoader(String type) {
		assert type != null;
		return loaders.get(type);
	}
	
	public MapFace[] getFaces() {
		return faces;
	}
	public MapFace getFace(int face) {
		if (face < 0 || face >= faces.length) return null;
		return faces[face];
	}
	public FlatMapFace getFlatFace() {
		MapFace f = getFace(UP);
		if (f instanceof FlatMapFace) {
			return (FlatMapFace)f;
		}
		else {
			throw new NullPointerException("Could not locate flat face at the expected location!");
		}
	}
	public UnitControl[][] getUnitsForFace(int face) {
		return faces[face].getMap();
	}
	public UnitControl getUnit(Index3i index) {
		return faces[index.z].getUnit(index.x, index.y);
	}
	public UnitControl getUnit(int f, int x, int y) {
		return faces[f].getUnit(x, y);
	}
	
	@Override
	public ConcurrentLinkedQueue<LevelListener> getListeners() {
		return listeners;
	}
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
			case "r": if (isPressed) restart(); break;
			case "backspace": if (isPressed) {
				notifyListeners(l -> l.onQuit(this));
			} break;
			case "end": if (isPressed) {
				notifyListeners(l -> l.onSkimp(this));
			}
			break;
		}
	}	
	@Override
	public void onTimerFinish(Timer timer) {
		if (timer == endtimer) {
			endtimer.reset();
			for (LevelListener l : listeners) {
				l.onFailure(this);
			}
			client.onFailure(this);
		}
	}
	@Override
	public void notifyListeners(Consumer<LevelListener> foreach) {
		getListeners().forEach(foreach);
		foreach.accept(client);
	}
	@Override
	public void onJoyAxisEvent(JoyAxisEvent evt) {}
	@Override
	public void onJoyButtonEvent(JoyButtonEvent evt) {
		if (evt.isPressed()) {
			String id = evt.getButton().getLogicalId();
			switch (id) {
				case JoystickButton.BUTTON_10:
				case JoystickButton.BUTTON_11:
					restart();
					break;
				case JoystickButton.BUTTON_6:
				case JoystickButton.BUTTON_7:
					notifyListeners(l -> l.onQuit(this));
					break;
				case JoystickButton.BUTTON_8:
				case JoystickButton.BUTTON_9:
					notifyListeners(l -> l.onSkimp(this));
					break;
			}
		}
	}
	
}
