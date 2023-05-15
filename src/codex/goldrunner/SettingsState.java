/*
 * The MIT License
 *
 * Copyright 2022 gary.
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
package codex.goldrunner;

import codex.goldrunner.game.management.LevelManager;
import codex.goldrunner.profile.Profile;
import codex.goldrunner.profile.ProfileListener;
import codex.goldrunner.profile.ProfileManager;
import codex.goldrunner.runners.EnemyControl;
import codex.goldrunner.util.RangedIntModel;
import codex.j3map.J3map;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;

/**
 *
 * @author gary
 */
public class SettingsState extends BaseAppState
		implements ProfileListener {
	
	private static final SettingsState instance = new SettingsState();
	
	Container window = new Container();
	Checkbox boosting = new Checkbox("Enable Boosting");
	Checkbox enableMusic = new Checkbox("Enable Music");
	Checkbox displayTotalTime = new Checkbox("Speedrun Display");
	Checkbox extremeMode = new Checkbox("Enable ExpertMode");
	Checkbox sluggishMode = new Checkbox("Enable SluggishMode");
	Slider minEnemyIntel = new Slider(new RangedIntModel(0, EnemyControl.MAX_INTEL, 0));
	Slider joystickX = new Slider();
	Label joystickDisplayX = new Label("");
	Slider joystickY = new Slider();
	Label joystickDisplayY = new Label("");
	Label loginNotice = new Label("You are not logged in!");
	J3map defset;
	
	public SettingsState() {
		setEnabled(false);
	}
	
	@Override
	protected void initialize(Application app) {
		defset = createDefaultSettings();
		window.setLocalTranslation(100, 400, 0);
		window.addChild(new Label("General Settings", new ElementId("window.title.label")));
		Container general = window.addChild(new Container());
			general.addChild(boosting);
			general.addChild(enableMusic);
			general.addChild(displayTotalTime);
			general.addChild(extremeMode);
			general.addChild(sluggishMode);
			Container intel = general.addChild(new Container());
				intel.setLayout(new BoxLayout(Axis.X, FillMode.Even));
				intel.setBackground(null);
				intel.addChild(minEnemyIntel);
				intel.addChild(new Label("Minimum Intelligence"));
		window.addChild(new Label("Joystick Settings", new ElementId("window.title.label")));
		Container joystick = window.addChild(
				new Container(new SpringGridLayout(Axis.X, Axis.Y)));
			joystick.addChild(new Label("Tilt Sensitivity X: "), 0, 1);
			joystick.addChild(joystickX, 1, 1).getModel().setMinimum(.01);
			joystick.addChild(joystickDisplayX, 2, 1);
			joystickX.getModel().setMaximum(.99);
			joystick.addChild(new Label("Tilt Sensitivity Y: "), 0, 2);
			joystick.addChild(joystickY, 1, 2).getModel().setMinimum(.01);
			joystick.addChild(joystickDisplayY, 2, 2);
			joystickY.getModel().setMaximum(.99);
		loadFromSource(defset);
		Container buttons = window.addChild(
				new Container(new SpringGridLayout(Axis.X, Axis.Y)));
			ActionButton save = buttons.addChild(
					new ActionButton(new CallMethodAction(this, "save")));
			ActionButton revert = buttons.addChild(
					new ActionButton(new CallMethodAction(this, "load")));
			ActionButton exit = buttons.addChild(
					new ActionButton(new CallMethodAction(this, "exit")));
		save.setText("Save");
		save.setName("save");
		revert.setText("Revert");
		revert.setName("revert");
		exit.setText("Exit");
		exit.setName("exit");
		getState(ProfileManager.class).addListener(this);
		load();
	}
	@Override
	protected void cleanup(Application app) {}
	@Override
	protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(window);
		Profile current = getState(ProfileManager.class).getCurrentProfile();
		//load();
		if (current == null) {
			getButton(window, "save").setAlpha(.5f);
			window.addChild(loginNotice);
		}
		else getButton(window, "save").setAlpha(1f);
	}
	@Override
	protected void onDisable() {
		((SimpleApplication)getApplication()).getGuiNode().detachChild(window);
		window.removeChild(loginNotice);
	}
	@Override
	public void update(float tpf) {
		joystickDisplayX.setText(extractDigits(joystickX.getModel().getValue(), 4));
		joystickDisplayY.setText(extractDigits(joystickY.getModel().getValue(), 4));
	}
	
	protected void save() {
		Profile current = getState(ProfileManager.class).getCurrentProfile();
		if (current != null) {
			current.setSettingsSource(settingsToSource());
		}
	}
	protected void load() {
		Profile current = getState(ProfileManager.class).getCurrentProfile();
		if (current != null && current.getSettingsSource() != null) {
			loadFromSource(current.getSettingsSource());
		}
		else {
			loadFromSource(defset);
		}
	}
	protected void exit() {
		setEnabled(false);
		getState(TransitionState.class).transition((app) -> {
			getState(LevelManager.class).setEnabled(true);
		});
	}
	public J3map createDefaultSettings() {
		J3map set = new J3map();
		set.store("boosting", true);
		set.store("music", true);
		set.store("display_total_time", false);
		set.store("extreme_mode", false);
		set.store("sluggish_mode", false);
		set.store("min_enemy_intel", 0);
		set.store("joystickX", .6f);
		set.store("joystickY", .8f);
		return set;
	}
	public J3map settingsToSource() {
		J3map set = new J3map();
		set.store("boosting", boosting.getModel().isChecked());
		set.store("music", enableMusic.getModel().isChecked());
		set.store("display_total_time", displayTotalTime.getModel().isChecked());
		set.store("extreme_mode", extremeModeEnabled());
		set.store("sluggishMode", sluggishModeEnabled());
		set.store("min_enemy_intel", getMinimumEnemyIntelligence());
		set.store("joystickX", (float)joystickX.getModel().getValue());
		set.store("joystickY", (float)joystickY.getModel().getValue());
		return set;
	}
	
	private void loadFromSource(J3map source) {
		boosting.getModel().setChecked(source.getBoolean("boosting", true));
		enableMusic.getModel().setChecked(source.getBoolean("music", true));
		displayTotalTime.getModel().setChecked(source.getBoolean("display_total_time", false));
		extremeMode.getModel().setChecked(source.getBoolean("extreme_mode", false));
		sluggishMode.getModel().setChecked(source.getBoolean("sluggish_mode", false));
		minEnemyIntel.getModel().setValue(source.getInteger("min_enemy_intel", 0));
		joystickX.getModel().setValue(source.getFloat("joystickX", .6f));
		joystickY.getModel().setValue(source.getFloat("joystickY", .8f));
	}	
	private Button getButton(Container parent, String name) {
		Spatial button = parent.getChild(name);
		if (button != null && button instanceof Button) {
			return (Button)button;
		}
		else {
			throw new NullPointerException("Spatial is not a Button!");
		}
	}
	private String extractDigits(double value, int digits) {
		String str = ""+value;
		String out = "";
		for (int i = 0; i < str.length() && i < digits; i++) {
			out += str.charAt(i);
		}
		return out;
	}
	
	public boolean boostingEnabled() {
		return boosting.getModel().isChecked();
	}
	public boolean musicEnabled() {
		return enableMusic.getModel().isChecked();
	}
	public boolean displayTotalTime() {
		return displayTotalTime.getModel().isChecked();
	}
	public boolean extremeModeEnabled() {
		return extremeMode.getModel().isChecked();
	}
	public boolean sluggishModeEnabled() {
		return sluggishMode.getModel().isChecked();
	}
	public int getMinimumEnemyIntelligence() {
		return (int)minEnemyIntel.getModel().getValue();
	}
	public double getJoystickSensitivityX() {
		return 1-joystickX.getModel().getValue();
	}
	public double getJoystickSensitivityY() {
		return 1-joystickY.getModel().getValue();
	}
	
	@Override
	public void onCurrentProfileChange(Profile profile) {
		load();
	}
	
	public static SettingsState getInstance() {
		return instance;
	}
	
}
