/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner;

import com.jme3.app.Application;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Container;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gary
 */
public class GameGlobals {
	
	public static final String GAME_MUSIC = "Sounds/music/DST-RailJet-LongSeamlessLoop.ogg";
	public static Point window_size;
	
	
	public static class FileSystem {
		
		public static final String EXTERNALGAMEDATA =
				getFilePath(System.getProperty("user.home"), ".goldrunnerdata");
		private static final Logger LOG = Logger.getLogger(GameGlobals.class.getName());
		
		public static boolean externalDataExists() {
			File external = new File(EXTERNALGAMEDATA);
			return external.exists();
		}
		public static File createFileIn(File directory, String name) {
			assert directory.isDirectory() && directory.exists();
			File file = getFile(directory.getPath(), name);
			try {
				file.createNewFile();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
			return file;
		}
		public static File createDirectoryIn(File directory, String name) {
			assert directory.isDirectory() && directory.exists();
			File file = getFile(directory.getPath(), name);
			file.mkdir();
			return file;
		}
		/**
		 * Fetches the file at the path.
		 * Is system independent.
		 * @param path
		 * @return 
		 */
		public static File getFile(String... path) {
			return new File(getFilePath(path));
		}
		/**
		 * Constructs a file path in accordance to the system.
		 * @param path
		 * @return 
		 */
		public static String getFilePath(String... path) {
			String p = "";
			for (int i = 0; i < path.length; i++) {
				p += path[i];
				if (i < path.length-1) {
					p += File.separator;
				}
			}
			return p;
		}
		/**
		 * Replaces all non-conforming forward-slashes with conforming back-slashes.
		 * @param path path, unaffected
		 * @return 
		 */
		public static String conformFilePath(String path) {
			if (File.separatorChar == '/') return ""+path;
			else return path.replace('/', File.separatorChar);
		}
	}
	
	public static class Gui {
		
		public static Vector3f getWindowSize(Application app) {
			AppSettings set = app.getContext().getSettings();
			return new Vector3f(set.getWidth(), set.getHeight(), 0f);
		}		
		public static Container createBackgroundContainer(AppSettings as, float z) {
			Container background = new Container();
			background.setLocalTranslation(0, as.getHeight(), z);
			background.setPreferredSize(new Vector3f(as.getWidth(), as.getHeight(), 0));
			return background;
		}
	}
	
	public static class Math {
		
		public static int signum(float value) {
			if (value > 0f) return 1;
			else if (value < 0f) return -1;
			else return 0;
		}
		public static float constrain(float value, float min, float max) {
			if (value < min) return min;
			else if (value > max) return max;
			else return value;
		}
		
	}
	
	
	
	public static <T extends Spatial> T getChild(Node parent, Class<T> type, String name) {
		Spatial spatial = parent.getChild(name);
		if (spatial != null && type.isAssignableFrom(spatial.getClass())) {
			return (T)spatial;
		}
		else {
			throw new NullPointerException("Spatial is not of "+type.getName());
		}
	}
	public static void applyHexDirectionalLighting(Spatial spatial, ColorRGBA color) {
		spatial.addLight(new DirectionalLight(new Vector3f(1f, 0f, 0f), color));
		spatial.addLight(new DirectionalLight(new Vector3f(-1f, 0f, 0f), color));
		spatial.addLight(new DirectionalLight(new Vector3f(0f, 1f, 0f), color));
		spatial.addLight(new DirectionalLight(new Vector3f(0f, -1f, 0f), color));
		spatial.addLight(new DirectionalLight(new Vector3f(0f, 0f, 1f), color));
		spatial.addLight(new DirectionalLight(new Vector3f(0f, 0f, -1f), color));
	}
	
	public static void setWindowSize(Point size) {
		window_size = size;
	}
	public static Point getWindowSize() {
		return window_size;
	}
	
}
