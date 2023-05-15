package codex.goldrunner;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import java.awt.Point;

/**
 * This is the Main Class of the Game.
 * Only initialization should be done here.
 * 
 * @author codex
 */
public class Main extends SimpleApplication {
	
	/**
	 * Hello world!!!!.
	 * @param args 
	 */
	
    public static void main(String[] args) {
        Main app = new Main();
		AppSettings settings = new AppSettings(true);
		settings.setTitle("JGoldRunner");
		settings.setFrameRate(60);
		//settings.setResolution(640, 540); // 640, 480
		settings.setWidth(640);
		settings.setHeight(480);
		settings.setUseJoysticks(true);
		//settings.setGammaCorrection(false);
		app.setSettings(settings);
		app.setDisplayFps(false);
		app.setDisplayStatView(false);
		//app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {		
		flyCam.setMoveSpeed(0);
		flyCam.setEnabled(false);
		int width = getContext().getSettings().getWidth();
		int height = getContext().getSettings().getHeight();
		GameGlobals.setWindowSize(new Point(width, height));
		stateManager.attach(new InitializationState(MenuState.class));
    }
    @Override
    public void simpleUpdate(float tpf) {}
    @Override
    public void simpleRender(RenderManager rm) {}
	
}
