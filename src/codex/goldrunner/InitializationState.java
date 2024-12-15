/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner;

import codex.goldrunner.util.J3mapLoader;
import codex.goldrunner.util.LongProcessor;
import codex.goldrunner.editor.LevelEditorState;
import codex.goldrunner.editor.PackageEditorState;
import codex.goldrunner.game.GameState;
import codex.goldrunner.game.LevelState;
import codex.goldrunner.game.management.LevelManager;
import codex.goldrunner.game.management.LevelPoolGenerator;
import codex.goldrunner.gui.AppStyles;
import codex.goldrunner.items.GoldControl;
import codex.goldrunner.profile.ProfileManager;
import codex.goldrunner.runners.*;
import codex.goldrunner.units.*;
import codex.goldrunner.util.BackgroundElementProcessor;
import codex.goldrunner.util.KeyFrameProcessor;
import codex.goldrunner.util.PointProcessor;
import codex.goldrunner.util.Vector3fProcessor;
import codex.j3map.J3mapFactory;
import codex.j3map.processors.*;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace.BroadphaseType;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.OptionPanelState;
import java.io.File;

/**
 *  
 *  
 *
 * @author gary 
 */
public class InitializationState extends BaseAppState {

    int init = 0;
    Class<? extends AppState> next;
    Node gui = new Node();
    AudioLibrary lib;
    BitmapText loadtext;
    String[] loadstrings = {"Loading", "......"};
    int loaddots = 0;
    AudioNode loadmusic;

    public InitializationState(Class<? extends AppState> next) {
        this.next = next;
    }

    @Override
    protected void initialize(Application app) {
        BitmapFont guiFont = app.getAssetManager().loadFont(
                "Interface/Fonts/console.fnt");
        loadtext = new BitmapText(guiFont);
        loadtext.setSize(guiFont.getCharSet().getRenderedSize());
        loadtext.setText(loadstrings[0]);
        loadtext.setLocalTranslation(10,
                app.getContext().getSettings().getHeight() - 10, 0);
        gui.attachChild(loadtext);
        getSimpleApp(app).getGuiNode().attachChild(gui);

        loadmusic = new AudioNode(app.getAssetManager(),
                "Sounds/music/TremLoadingloopl.wav", DataType.Stream);
        loadmusic.setPositional(false);
        loadmusic.setVolume(.1f);
        //loadmusic.play();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        System.out.println("remove loading gui");
        gui.removeFromParent();
        loadmusic.stop();
        //gui.setCullHint(Spatial.CullHint.Always);
    }

    @Override
    public void update(float tpf) {
        if (init < 0) {
            return;
        }
        loadtext.setText(loadstrings[0] + loadstrings[1].substring(0, loaddots));
        if (++loaddots > loadstrings[1].length()) {
            loaddots = 0;
        }
        if (load(init)) {
            init++;
        }
    }

    private boolean load(int index) {
        Application app = getApplication();
        switch (index) {
            case 0: initLemur(app); break;
            case 1: initExternalFiles(); break;
            case 2: initJ3map(app); break;
            case 3: initInputMappings(app); break;
            case 4: initMapLoaders(); break;
            case 5: initLightProbes(app); break;
            case 6: initAudioLibrary(app); break;
            case 7: return loadAudioLibrary(app);
            case 8: initAppStates(); break;
            case 9: initPostProcessors(app); break;
            default: endInitialization();
        }
        return true;
    }

    private void initLemur(Application app) {
        GuiGlobals.initialize(app);
        AppStyles.load(app.getAssetManager());
        GuiGlobals.getInstance().getStyles().setDefaultStyle(AppStyles.STYLE);
    }

    private void initExternalFiles() {
        File data = new File(GameGlobals.FileSystem.EXTERNALGAMEDATA);
        if (!data.exists()) {
            data.mkdir();
            GameGlobals.FileSystem.createDirectoryIn(data, "levels");
            GameGlobals.FileSystem.createDirectoryIn(data, "profile");
            GameGlobals.FileSystem.createDirectoryIn(data, "packages");
            GameGlobals.FileSystem.createDirectoryIn(data, "exports");
        }
        getApplication().getAssetManager().registerLocator(
                GameGlobals.FileSystem.EXTERNALGAMEDATA, FileLocator.class);
    }

    private void initJ3map(Application app) {
        app.getAssetManager().registerLoader(J3mapLoader.class, "j3map");
        J3mapFactory.registerAllProcessors(
                ArrayProcessor.class,
                BooleanProcessor.class,
                StringProcessor.class,
                StringArrayProcessor.class,
                FloatProcessor.class,
                LongProcessor.class,
                Vector3fProcessor.class,
                PointProcessor.class,
                KeyFrameProcessor.class,
                BackgroundElementProcessor.class,
                IntegerProcessor.class);
    }

    private void initInputMappings(Application app) {
        // all occurances of the four direction goes up, right, down, left (clockwise order)
        InputManager im = app.getInputManager();
        im.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        im.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        im.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        im.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        im.addMapping("d", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("a", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("r", new KeyTrigger(KeyInput.KEY_R));
        im.addMapping("g", new KeyTrigger(KeyInput.KEY_G));
        im.addMapping("x", new KeyTrigger(KeyInput.KEY_X));
        im.addMapping("space", new KeyTrigger(KeyInput.KEY_SPACE));
        im.addMapping("backspace", new KeyTrigger(KeyInput.KEY_BACK));
        im.addMapping("end", new KeyTrigger(KeyInput.KEY_END));
        im.addMapping("lmb", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        im.addMapping("rmb", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        im.addMapping("mmb", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        im.addMapping("x-pos", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        im.addMapping("y-pos", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        im.addMapping("x-neg", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        im.addMapping("y-neg", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
    }

    private void initMapLoaders() {
        LevelState.registerUnitLoader(new UnitControl() {});
        LevelState.registerUnitLoader(new BrickControl());
        LevelState.registerUnitLoader(new LadderControl());
        LevelState.registerUnitLoader(new BarControl());
        LevelState.registerUnitLoader(new ConcreteControl());
        LevelState.registerUnitLoader(new EscapeLadderControl());
        LevelState.registerUnitLoader(new GoalControl());
        LevelState.registerUnitLoader(new PlatformControl());
        LevelState.registerUnitLoader(new HeroControl());
        LevelState.registerUnitLoader(new EnemyControl());
        LevelState.registerUnitLoader(new GoldControl());
    }

    private void initLightProbes(Application app) {
        Node probeNode = (Node) app.getAssetManager().loadModel("Scenes/defaultProbe.j3o");
        LightProbe probe = (LightProbe) probeNode.getLocalLightList().iterator().next();
        getSimpleApp(app).getRootNode().addLight(probe);
    }

    private void initAudioLibrary(Application app) {
        lib = new AudioLibrary();
        getStateManager().attach(lib);
        //lib.addAudio("Game", "Sounds/music/DST-RailJet-LongSeamlessLoop.ogg");
        lib.addAudio("Game", "Sounds/music/awake10_megaWall.wav");
        lib.addAudio("Menu", "Sounds/music/contemplation 2.ogg");
    }

    private boolean loadAudioLibrary(Application app) {
        lib.loadNext();
        return !lib.audioInBucket();
    }

    private void initAppStates() {
        BulletAppState bulletapp = new BulletAppState(BroadphaseType.DBVT);
        bulletapp.setDebugEnabled(true);
        bulletapp.setDebugViewPorts(getApplication().getRenderManager().createPostView("debug", getApplication().getCamera()));
        getStateManager().attachAll(
                bulletapp,
                new OptionPanelState(),
                new TransitionState(),
                new LevelManager(),
                new LevelPoolGenerator(),
                new ProfileManager(),
                SettingsState.getInstance(),
                new MenuState(),
                new GameState(),
                new LevelEditorState(),
                new PackageEditorState());
    }

    private void initPostProcessors(Application app) {
    }

    private void endInitialization() {
        setEnabled(false);
        getStateManager().getState(TransitionState.class).transition((app) -> {
            getApplication().getStateManager().getState(next).setEnabled(true);
        });
        init = -2;
    }

    private SimpleApplication getSimpleApp(Application app) {
        return (SimpleApplication) app;
    }

}
