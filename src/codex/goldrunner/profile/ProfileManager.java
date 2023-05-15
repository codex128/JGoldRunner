/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner.profile;

import codex.goldrunner.GameGlobals;
import codex.goldrunner.TransitionState;
import codex.goldrunner.game.management.LevelManager;
import codex.goldrunner.gui.DoublecheckListener;
import codex.goldrunner.gui.DoublecheckPopup;
import codex.j3map.J3map;
import codex.jmeutil.listen.Listenable;
import codex.jmeutil.Timer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.PasswordField;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.style.ElementId;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 *  
 *  
 *
 * @author gary 
 */
public class ProfileManager extends BaseAppState
		implements Listenable<ProfileListener>, DoublecheckListener {
	
	J3map master;
	Profile current, editing;
	Container gui = new Container();
	ListBox<Profile> profiles = new ListBox<>();
	Container publicInfo = new Container();
	Label loginNotice = new Label("You are not logged in!");
	Container loginInfo = new Container();
	Container login = new Container();
	ActionButton loginButton;
	Label user = new Label("", new ElementId("window.title.label"));
	Container edit = new Container();
	LinkedList<ProfileListener> listeners = new LinkedList<>();
	DoublecheckPopup doublecheck = new DoublecheckPopup();
	
	public ProfileManager() {
		setEnabled(false);
	}
	
    @Override
    protected void initialize(Application app) {
		doublecheck.addListener(this);
		Vector3f windowsize = GameGlobals.Gui.getWindowSize(app);
		gui.setLocalTranslation(0f, windowsize.getY(), 0f);
		gui.setPreferredSize(windowsize);
		Container upper = gui.addChild(
				new Container(new BoxLayout(Axis.X, FillMode.Even)));
		Container list = upper.addChild(new Container());
		list.addChild(new Label("Profiles", new ElementId("window.title.label")));
		list.addChild(profiles).addClickCommands((ListBox source) -> {
			displayProfileInfo(profiles.getSelectedItem());
		});
		Container profileInfo = upper.addChild(new Container());
		profileInfo.addChild(publicInfo).setLayout(new SpringGridLayout(Axis.X, Axis.Y));
		publicInfo.addChild(new Label("Username: "), 0, 0);
		publicInfo.addChild(new Label(""), 1, 0).setName("username");
		publicInfo.addChild(new Label("Rank: "), 0, 1);
		publicInfo.addChild(new Label(""), 1, 1).setName("rank");
		publicInfo.addChild(new Label("Attempts: "), 0, 2);
		publicInfo.addChild(new Label(""), 1, 2).setName("attempts");
		publicInfo.addChild(new Label("Time Played: "), 0, 3);
		publicInfo.addChild(new Label(""), 1, 3).setName("playtime");
		publicInfo.setAlpha(0f);
		profileInfo.addChild(loginNotice);
		profileInfo.addChild(loginInfo);
		Container privateButtons = loginInfo.addChild(new Container());
		privateButtons.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
		privateButtons.addChild(new Button("Edit")).addClickCommands((source) -> {
			popupProfileEditor(current);
		});
		privateButtons.addChild(new ActionButton(new CallMethodAction(
				this, "motionDeleteProfile")), 1, 0).setText("Delete");
		loginInfo.setAlpha(0f);
		Container profbuttons = gui.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y)));
		//profbuttons.addChild(new ActionButton(
		//		new CallMethodAction(this, "popupLoginPanel"))).setText("Log In");
		loginButton = profbuttons.addChild(new ActionButton(
				new CallMethodAction(this, "popupLoginPanel")));
		loginButton.setText("Log In");
		profbuttons.addChild(new ActionButton(new CallMethodAction(
				this, "popupProfileEditor"))).setText("Create Profile");
		profbuttons.addChild(new ActionButton(
				new CallMethodAction(this, "exit"))).setText("Exit");
		// login popup panel
		login.setLocalTranslation(100f, windowsize.y-100f, 1f);
		login.setAlpha(1f);
		//login.setPreferredSize(new Vector3f(150f, 200f, 0f));
		login.addChild(user);
		Container pass = login.addChild(new Container(
				new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Last)));
		pass.addChild(new Label("Password: "), 0, 0);
		PasswordField passinput = pass.addChild(new PasswordField(""), 1, 0);
		passinput.setName("password");
		passinput.setPreferredWidth(100f);
		Container buttons = login.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y)));
		buttons.addChild(new ActionButton(
				new CallMethodAction(this, "closeLoginPanel"))).setText("Cancel");
		buttons.addChild(new ActionButton(
				new CallMethodAction(this, "checkPassword"))).setText("Log In");
		// profile creation panel
		edit.setLocalTranslation(100f, windowsize.y-100f, 1f);
		//edit.setPreferredSize(new Vector3f(150f, 200f, 0f));
		Container entry = edit.addChild(
				new Container(new SpringGridLayout(Axis.X, Axis.Y)));
		entry.addChild(new Label("Username: "), 0, 0).setName("username label");
		entry.addChild(new TextField(""), 1, 0).setName("username");
		entry.addChild(new Label("Password: "), 0, 1).setName("password1");
		entry.addChild(new PasswordField(""), 1, 1).setName("password");
		entry.addChild(new Label("Confirm: "), 0, 2).setName("password2");
		entry.addChild(new PasswordField(""), 1, 2).setName("confirm password");
		Container createbuttons = edit.addChild(
				new Container(new SpringGridLayout(Axis.X, Axis.Y)));
		createbuttons.addChild(new ActionButton(new CallMethodAction(
				this, "closeProfileEditor")), 0, 0).setText("Cancel");
		createbuttons.addChild(new ActionButton(new CallMethodAction(
				this, "confirmProfile")), 1, 0).setText("Create");
		// load
		load();
	}
    @Override
    protected void cleanup(Application app) {
		export();
	}
    @Override
    protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(gui);
		if (profiles.getSelectedItem() != null) {
			displayProfileInfo(profiles.getSelectedItem());
		}
	}
    @Override
    protected void onDisable() {
		((SimpleApplication)getApplication()).getGuiNode().detachChild(gui);
	}
    @Override
    public void update(float tpf) {}
	
	protected void exit() {
		setEnabled(false);
		getState(TransitionState.class).transition((app) -> {
			getState(LevelManager.class).setEnabled(true);
		});
	}
	
	protected void popupLoginPanel() {
		if (profiles.getSelectedItem() == null) return;
		if (current == null) {
			user.setText(profiles.getSelectedItem().getUsername());
			GameGlobals.getChild(login, TextField.class, "password").setText("");
			getState(PopupState.class).showPopup(login);
		}
		else {
			setCurrentProfile(null);
		}
	}
	protected void checkPassword() {
		if (profiles.getSelectedItem().tryPassword(
				GameGlobals.getChild(login, TextField.class, "password").getText())) {
			setCurrentProfile(profiles.getSelectedItem());
			closeLoginPanel();
		}
	}
	protected void closeLoginPanel() {
		user.setText("");
		GameGlobals.getChild(login, TextField.class, "password").setText("");
		getState(PopupState.class).closePopup(login);
	}
	
	protected void popupProfileEditor() {
		popupProfileEditor(null);
	}
	protected void popupProfileEditor(Profile profile) {
		editing = profile;
		if (editing != null) {
			GameGlobals.getChild(edit, TextField.class, "username").setText(editing.getUsername());
			//GameGlobals.getChild(edit, Label.class, "username label").setText("Username: ");
			GameGlobals.getChild(edit, Label.class, "password1").setText("New Password: ");
			//GameGlobals.getChild(edit, Label.class, "password2").setText("Confirm: ");
		}		
		else {
			GameGlobals.getChild(edit, TextField.class, "username").setText("");
			//GameGlobals.getChild(edit, Label.class, "username label").setText("Username: ");
			GameGlobals.getChild(edit, Label.class, "password1").setText("Password: ");
			//GameGlobals.getChild(edit, Label.class, "password2").setText("Confirm: ");
		}
		GameGlobals.getChild(edit, TextField.class, "password").setText("");
		GameGlobals.getChild(edit, TextField.class, "confirm password").setText("");
		getState(PopupState.class).showPopup(edit);
	}
	protected void confirmProfile() {
		String username = GameGlobals.getChild(edit, TextField.class, "username").getText();
		String password = GameGlobals.getChild(edit, TextField.class, "password").getText();
		String confirm = GameGlobals.getChild(edit, TextField.class, "confirm password").getText();
		if (editing == null) {
			if (password.equals(confirm) && !containsUsername(username)) {
				createProfile(generateProfileSource(username, password));
				closeProfileEditor();
			}
		}
		else {
			editing.getSource().overwrite("username", username);
			if (!password.isEmpty() && password.equals(confirm)) {
				editing.getSource().overwrite("password", password);
			}
			closeProfileEditor();
		}
	}
	protected void closeProfileEditor() {
		getState(PopupState.class).closePopup(edit);
	}
	
	protected void motionDeleteProfile() {
		if (current == null) return;
		getState(PopupState.class).showPopup(doublecheck);
		doublecheck.setLocalTranslation(100, 300, 10);
		doublecheck.setExplaination("Do you really want to delete this profile? This cannot be undone!");
		doublecheck.setCommand((source) -> {
			if (profiles.getSelectedItem() == current) {
				hideProfileInfo();
			}
			profiles.getModel().remove(current);
			setCurrentProfile(null);
		});
	}
	
	public void load() {
		AssetManager assets = getApplication().getAssetManager();
		if (GameGlobals.FileSystem.getFile(GameGlobals.FileSystem.EXTERNALGAMEDATA, "profiles.j3map").exists()) {
			master = J3map.openJ3map(assets.loadAsset("profiles.j3map"));
			master.forEachType(J3map.class, (property) -> {
				profiles.getModel().add(new Profile(property));
			});
		}
	}
	public void export() {
		if (master != null) master.clear();
		else master = new J3map();
		int n = 0;
		for (Profile p : profiles.getModel()) {
			master.store("profile"+(n++), p.getSource());
		}
		try {
			master.export(GameGlobals.FileSystem.getFilePath(GameGlobals.FileSystem.EXTERNALGAMEDATA, "profiles.j3map"));
		} catch (IOException ex) {
			throw new NullPointerException("An error occured while exporting profiles!");
		}
	}
	
	public J3map generateProfileSource(String username, String password) {
		J3map source = new J3map();
		source.store("username", username);
		source.store("password", password);
		source.store("attempts", 0);
		source.store("playtime", 0);
		return source;
	}
	public Profile createProfile(J3map source) {
		Profile p = new Profile(source);
		profiles.getModel().add(p);
		notifyListeners(l -> l.onProfileAdded(p));
		current = p;
		return p;
	}
	public Profile getCurrentProfile() {
		return current;
	}
	public void setCurrentProfile(Profile profile) {
		current = profile;
		notifyListeners(l -> l.onCurrentProfileChange(current));
		if (current != null) {
			loginNotice.setText("You are logged in as "+current.getUsername());
			loginInfo.setAlpha(1f);
			loginButton.setText("Log Out");
		}
		else {
			loginNotice.setText("You are not logged in!");
			loginInfo.setAlpha(0f);
			loginButton.setText("Log In");
		}
	}
	public void deleteProfile(Profile profile) {
		profiles.getModel().remove(profile);
		notifyListeners(l -> l.onProfileDeleted(profile));
		if (current == profile) {
			setCurrentProfile(null);
		}
	}	
	public boolean containsUsername(String name) {
		for (Profile p : profiles.getModel()) {
			if (p.getUsername().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void onLoggedIn(Consumer<Profile> command) {
		if (current != null) {
			command.accept(current);
		}
	}
	
	private void displayProfileInfo(Profile profile) {
		GameGlobals.getChild(publicInfo, Label.class, "username")
				.setText(profile.getUsername());
		GameGlobals.getChild(publicInfo, Label.class, "rank")
				.setText("not implemented");
		GameGlobals.getChild(publicInfo, Label.class, "attempts")
				.setText(""+profile.getAttempts());
		int sec = Timer.secondsToLimitedSeconds(profile.getTimePlayed());
		int min = Timer.secondsToMinutes(profile.getTimePlayed(), true);
		int hours = Timer.secondsToHours(profile.getTimePlayed(), false);
		GameGlobals.getChild(publicInfo, Label.class, "playtime")
				.setText(hours+" hour"+(hours == 1 ? "" : "s")+", "+min+" minute"+(min == 1 ? "" : "s")+", "+sec+" second"+(sec == 1 ? "" : "s"));
		publicInfo.setAlpha(1f);
	}
	private void hideProfileInfo() {
		publicInfo.setAlpha(0f);
	}
	
	@Override
	public Collection<ProfileListener> getListeners() {
		return listeners;
	}
	@Override
	public void onButton(DoublecheckPopup popup) {
		getState(PopupState.class).closePopup(popup);
	}
	
}
