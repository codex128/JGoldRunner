/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner.game.management;

import codex.goldrunner.GameGlobals;
import codex.goldrunner.SettingsState;
import codex.goldrunner.TransitionState;
import codex.goldrunner.editor.EditorClient;
import codex.goldrunner.editor.LevelEditorState;
import codex.goldrunner.editor.PackageEditorState;
import codex.goldrunner.game.GameState;
import codex.goldrunner.gui.DoublecheckListener;
import codex.goldrunner.gui.DoublecheckPopup;
import codex.goldrunner.gui.FileBrowser;
import codex.goldrunner.gui.FileBrowserListener;
import codex.goldrunner.profile.ProfileManager;
import codex.goldrunner.util.SnowflakeFactory;
import codex.j3map.J3map;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.DynamicInsetsComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.style.ElementId;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;

/**
 *  
 *  
 *
 * @author gary 
 */
public class LevelManager extends BaseAppState implements EditorClient,
		DoublecheckListener, FileBrowserListener {	
    
	private static boolean allow_save = true;
	
	SnowflakeFactory sff;
	Container gui = new Container();
	ListBox<LevelPackage> packages = new ListBox<>();
	ListBox<LevelData> levels = new ListBox<>();
	Container packageEditor = new Container();
	LevelPackage editing;
	FileBrowser browser = new FileBrowser();
	DoublecheckPopup doublechecker = new DoublecheckPopup();
	
	public LevelManager() {
		setEnabled(false);
	}
	
    @Override
    protected void initialize(Application app) {
		J3map index;
		if (GameGlobals.FileSystem.getFile(GameGlobals.FileSystem.EXTERNALGAMEDATA, "index.j3map").exists()) {
			index = load(J3map.openJ3map(app.getAssetManager().loadAsset("index.j3map")));
		}
		else {
			index = load(J3map.openJ3map(app.getAssetManager().loadAsset("Levels/index/index.j3map")));
		}
		// initialize the snowflake factory used to produce unique level file names
		if (index.propertiesExist(J3map.createChecker("sf_id", Long.class))) {
			sff = new SnowflakeFactory(index.getProperty(Long.class, "sf_id"));
		}
		else sff = new SnowflakeFactory();
		
		AppSettings set = app.getContext().getSettings();
		gui.setLocalTranslation(0, set.getHeight(), 0);
		gui.setPreferredSize(new Vector3f(set.getWidth(), set.getHeight(), 0f));
		
		doublechecker.addListener(this);
		browser.addListener(this);
		browser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (!pathname.isHidden() || pathname.getName().equals(".goldrunnerdata"))
						&& (pathname.isDirectory() || pathname.getName().endsWith(".j3map"));
			}
		});
		browser.setLocalTranslation(100, 400, 10);
		browser.setPreferredSize(new Vector3f(300, 350, 0));
		browser.getListBox().setVisibleItems(10);
		
		createListGui();
		createOptionsGui();
		createPackageEditorGui();
		createMoreGui();
		
		Container c = new Container();
		c.setLocalTranslation(540, 30, 0);
		gui.attachChild(c);
		Button reload = c.addChild(new Button("Reload Default"));
		reload.addClickCommands((Button source) -> {
			J3map def = J3map.openJ3map(app.getAssetManager().loadAsset("Levels/index/index.j3map"));
			load(def);
		});
    }
    @Override
    protected void cleanup(Application app) {
        save();
		//getApplication().getInputManager().removeRawInputListener(packages);
		//getApplication().getInputManager().removeRawInputListener(levels);
    } 
    @Override
    protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(gui);
		loadLevelsToList();
    }
    @Override
    protected void onDisable() {        
		((SimpleApplication)getApplication()).getGuiNode().detachChild(gui);
    }
    @Override
    public void update(float tpf) { 
        //TODO: implement behavior during runtime
    }
	
	private void createListGui() {
		Container lists = gui.addChild(new Container());
		SpringGridLayout layout = new SpringGridLayout(Axis.X, Axis.Y);
		lists.setLayout(layout);
		layout.addChild(0, 0, new Label("Packages", new ElementId("window.title.label")));
		layout.addChild(1, 0, new Label("Levels", new ElementId("window.title.label")));
		layout.addChild(0, 1, packages);
		layout.addChild(1, 1, levels);
		packages.setVisibleItems(10);
		packages.addClickCommands((ListBox source) -> {
			loadLevelsToList();
		});
		levels.setVisibleItems(10);
	}
	private void createOptionsGui() {
		// layout
		Container options = gui.addChild(new Container());
		options.setInsetsComponent(new DynamicInsetsComponent(.5f, .5f, .5f, .5f));
		SpringGridLayout layout = new SpringGridLayout(Axis.X, Axis.Y);
		options.setLayout(layout);
		//layout.addChild(0, 0, packagedash);
		//layout.addChild(1, 0, lvlsdash);
		//SpringGridLayout packagelayout = new SpringGridLayout(Axis.X, Axis.Y);
		//packagedash.setLayout(packagelayout);
		//SpringGridLayout lvlslayout = new SpringGridLayout(Axis.X, Axis.Y);
		//lvlsdash.setLayout(lvlslayout);
		// buttons
		layout.addChild(0, 0, new Button("Play Package!")).addClickCommands((Button source) -> {
			if (packages.getSelectedItem() != null) {
				setEnabled(false);
				GameState game = getStateManager().getState(GameState.class);
				LevelPool pool = new LevelPool();
				if (levels.getSelectedItem() == null) {
					pool.add(packages.getSelectedItem());
				}
				else {
					boolean start = false;
					LevelPackage temp = new LevelPackage();
					for (LevelData data : packages.getSelectedItem().getLevels()) {
						if (levels.getSelectedItem() == data) {
							start = true;
						}
						if (start) {
							temp.addLevel(data);
						}
					}
					pool.add(temp);
				}
				game.setLevelPool(pool);
				getStateManager().getState(TransitionState.class).transition((app) -> {
					game.setEnabled(true);
				});
			}
		});		
		layout.addChild(0, 1, new Button("Create Package")).addClickCommands((Button source) -> {
			openPackageEditor();
		});		
		layout.addChild(1, 1, new Button("Create New Level")).addClickCommands((Button source) -> {
			if (packages.getSelectedItem() != null) {
				setEnabled(false);
				getStateManager().getState(TransitionState.class).transition((appl) -> {
					getStateManager().getState(LevelEditorState.class)
							.createNewLevel(this);
				});
			}
		});
		layout.addChild(0, 3, new Button("Delete Package")).addClickCommands((Button source) -> {
			if (packages.getSelectedItem() == null ||
					doublechecker.isActive()) {
				return;
			}
			getState(PopupState.class).showModalPopup(doublechecker, new ColorRGBA(0f, 0f, 0f, .5f));
			doublechecker.setLocalTranslation(300, 300, 10);
			doublechecker.setExplaination("Do you want to delete this package? It cannot be undone!");
			doublechecker.setYesButtonText("Yes, delete it!");
			doublechecker.setNoButtonText("No, keep it!");
			doublechecker.setCommand((src) -> {
				if (packages.getSelectedItem() == null) return;
				packages.getSelectedItem().deleteSource();
				packages.getModel().remove(packages.getSelectedItem());
				//System.out.println("");
				levels.getModel().clear();
			});
		});
		layout.addChild(0, 2, new Button("Edit Package")).addClickCommands((Button source) -> {
			if (packages.getSelectedItem() == null) return;
			//openPackageEditor(packages.getSelectedItem());
			getState(PackageEditorState.class)
					.open(this, packages.getSelectedItem());
			setEnabled(false);
		});
		layout.addChild(1, 0, new Button("Play Level!")).addClickCommands((Button source) -> {
			if (levels.getSelectedItem() != null) {
				setEnabled(false);
				GameState game = getStateManager().getState(GameState.class);
				LevelPool pool = new LevelPool();
				pool.add(levels.getSelectedItem());
				game.setLevelPool(pool);
				getStateManager().getState(TransitionState.class).transition((appl) -> {
					game.setEnabled(true);
				});
			}
		});
		layout.addChild(1, 2, new Button("Edit Level")).addClickCommands((Button source) -> {
			if (levels.getSelectedItem() != null) {
				setEnabled(false);
				getStateManager().getState(TransitionState.class).transition((appl) -> {
					LevelEditorState editor = getState(LevelEditorState.class);
					editor.importLevel(this, levels.getSelectedItem());
					//editor.setEnabled(true);
				});
			}
		});
		layout.addChild(1, 3, new Button("Delete Level")).addClickCommands((Button source) -> {
			if (packages.getSelectedItem() == null ||
					doublechecker.isActive()) {
				return;
			}
			getState(PopupState.class).showPopup(doublechecker);
			doublechecker.setLocalTranslation(300, 300, 10);
			doublechecker.setExplaination("Do you want to delete this level? It cannot be undone!");
			doublechecker.setYesButtonText("Yes, delete it!");
			doublechecker.setNoButtonText("No, keep it!");
			doublechecker.setCommand((src) -> {
				levels.getSelectedItem().deleteSource();
				packages.getSelectedItem().removeLevel(levels.getSelectedItem());
				loadLevelsToList();
			});
		});
		layout.addChild(0, 5, new Button("Import Package")).addClickCommands((source) -> {
			getState(PopupState.class).showModalPopup(browser, new ColorRGBA(0f, 0f, 0f, .5f));
			browser.open(new File(System.getProperty("user.home")));
		});
		layout.addChild(0, 6, new Button("Export Package")).addClickCommands((source) -> {
			if (packages.getSelectedItem() == null) return;
			export(packages.getSelectedItem());
		});
	}
	private void createPackageEditorGui() {
		packageEditor.setLocalTranslation(200, 350, 5);
		SpringGridLayout grid = new SpringGridLayout(Axis.Y, Axis.X);
		packageEditor.setLayout(grid);
		//packagecreator.setPreferredSize(new Vector3f(200, 200, 0));
		//packageEditor.setInsets(new Insets3f(10, 10, 10, 10));
		packageEditor.addChild(new Label("Create New Package",
				new ElementId("window.title.label")));
		packageEditor.addChild(new TextField("new")).setName("name");
		packageEditor.addChild(new ActionButton(new CallMethodAction(
				this, "confirmPackage"))).setText("Confirm");
		packageEditor.addChild(new ActionButton(new CallMethodAction(
				this, "closePackageEditor"))).setText("Cancel");
	}
	private void createMoreGui() {
		Container more = gui.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y)));
		more.addChild(new Button("Options")).addClickCommands((Button source) -> {
			setEnabled(false);
			getStateManager().getState(TransitionState.class).transition((app) -> {
				getStateManager().getState(SettingsState.class).setEnabled(true);
			});
		});
		more.addChild(new Button("Profiles")).addClickCommands((Button source) -> {
			setEnabled(false);
			getStateManager().getState(TransitionState.class).transition((app) -> {
				getStateManager().getState(ProfileManager.class).setEnabled(true);
			});
		});
	}
	
	protected void openPackageEditor() {
		openPackageEditor(null);
	}
	protected void openPackageEditor(LevelPackage editing) {
		this.editing = editing;
		TextField name = GameGlobals.getChild(packageEditor,
				TextField.class, "name");
		if (this.editing == null) {
			name.setText("new");
		}
		else {
			name.setText(this.editing.getName());
		}
		getState(PopupState.class).showPopup(packageEditor);
	}
	protected void confirmPackage() {
		String name = GameGlobals.getChild(
					packageEditor, TextField.class, "name").getText();
		if (editing == null) {
			LevelPackage pack = new LevelPackage();
			pack.setName(name);
			packages.getModel().add(pack);
		}
		else {
			editing.setName(name);
		}
		closePackageEditor();
	}
	protected void closePackageEditor() {
		editing = null;
		getState(PopupState.class).closePopup(packageEditor);
	}
		
	public Collection<LevelPackage> getPackages() {
		return packages.getModel();
	}
	
	protected void loadLevelsToList() {
		levels.getModel().clear();
		if (packages.getSelectedItem() == null) return;
		for (LevelData data : packages.getSelectedItem().getLevels()) {
			levels.getModel().add(data);
		}
	}
	private J3map load(J3map index) {
		String[] order = index.getStringArray("order");
		for (String name : order) {
			LevelPackage pack = new LevelPackage(J3map.openJ3map(
					getApplication().getAssetManager().loadAsset(name)));
			pack.load(getApplication().getAssetManager());
			packages.getModel().add(pack);
		}
		return index;
	}
	private void save() {
		// todo: do not export unchanged levels
		try {
			J3map index = new J3map();
			String[] order = new String[packages.getModel().size()];
			int i = 0;
			for (LevelPackage pack : packages.getModel()) {
				pack.save(sff);
				order[i++] = pack.getSaveLocation();
			}
			index.store("order", order);
			index.store("sf_id", sff.getNextId());
			index.export(GameGlobals.FileSystem.getFilePath(GameGlobals.FileSystem.EXTERNALGAMEDATA, "index.j3map"));
		}
		catch (IOException ex) {
			throw new NullPointerException("An error occured while exporting!");
		}
	}
	private void export(LevelPackage pack) {
		if (!allow_save) return;
		File exports = GameGlobals.FileSystem.getFile(GameGlobals.FileSystem.EXTERNALGAMEDATA, "exports");
		File[] same = exports.listFiles((File dir, String name) -> {
			return name.startsWith(pack.getName()+" (v");
		});
		File destination = GameGlobals.FileSystem.getFile(exports.getPath(), pack.getName()+" (v"+same.length+").j3map");
		try {
			pack.export().export(destination);
		} catch (IOException ex) {
			throw new NullPointerException("An error occured while exporting a level package!");
		}
	}

	@Override
	public void saveNewLevelData(LevelData data) {
		packages.getSelectedItem().addLevel(data);
	}
	@Override
	public void onExit() {
		setEnabled(true);
		loadLevelsToList();
	}
	@Override
	public void onButton(DoublecheckPopup popup) {
		getState(PopupState.class).closePopup(popup);
	}
	@Override
	public void onFileChosen(FileBrowser browser, File file) {
		LevelPackage pack = new LevelPackage(new J3map(file));
		pack.load(getApplication().getAssetManager());
		packages.getModel().add(pack);
		onBrowserCanceled(browser);
	}
	@Override
	public void onBrowserCanceled(FileBrowser browser) {
		getState(PopupState.class).closePopup(browser);
		browser.close();
	}
	
	public static boolean savingAllowed() {
		return allow_save;
	}
	
}
