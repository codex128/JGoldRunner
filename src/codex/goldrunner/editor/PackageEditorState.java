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
import codex.goldrunner.game.management.LevelData;
import codex.goldrunner.game.management.LevelPackage;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BoxLayout;
import java.util.function.Consumer;

/**
 *
 * @author gary
 */
public class PackageEditorState extends BaseAppState {
	
	AppState client;
	LevelPackage edit;
	PackageActor actor;
	Node gui = new Node();
	Container actorgui = new Container();
	TextField name;
	TextField creator;
	ListBox<LevelData> levels = new ListBox<>();
	
	public PackageEditorState() {
		setEnabled(false);
	}

	@Override
	protected void initialize(Application app) {
		Container c1 = new Container();
		gui.attachChild(c1);
		c1.setLayout(new BoxLayout(Axis.Y, FillMode.Even));
		c1.setLocalTranslation(0, GameGlobals.getWindowSize().y, 0);
		c1.setPreferredSize(new Vector3f(GameGlobals.getWindowSize().x,
				GameGlobals.getWindowSize().y, 0));
		
		Container details = c1.addChild(new Container());
		details.addChild(name);
		details.addChild(creator);
		
		Container c2 = c1.addChild(new Container());
		c2.setLayout(new BoxLayout(Axis.X, FillMode.Even));
		
		Container options = c2.addChild(new Container());
		options.addChild(new Button("Swap Levels")).addClickCommands((source) -> {
			//if (levels.getSelectedItem() == null) return;
			openActor(new LevelSwapActor(this));
		});
		options.addChild(new Button("Save")).addClickCommands((source) -> {
			save();
		});
		options.addChild(new Button("Exit")).addClickCommands((source) -> {
			close();
		});
		
		Container lvls = c2.addChild(new Container());
		lvls.addChild(levels);
		levels.addClickCommands((Command<ListBox>) (ListBox source) -> {
			notifyActor(a -> a.onLevelSelected(levels.getSelectedItem()));
		});
		
		c2.addChild(actorgui);
	}
	@Override
	protected void cleanup(Application app) {
		
	}
	@Override
	protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(gui);
	}
	@Override
	protected void onDisable() {
		gui.removeFromParent();
	}
	@Override
	public void update(float tpf) {
		
	}
	
	public void open(AppState client, LevelPackage edit) {
		this.client = client;
		this.edit = edit;
		levels.getModel().addAll(this.edit.getLevels());
		setEnabled(true);
	}
	public void close() {
		levels.getModel().clear();
		setEnabled(false);
		client.setEnabled(true);
	}
	public void save() {
		edit.replaceAllLevels(levels.getModel());
	}
	
	public AppState getClient() {
		return client;
	}
	public PackageActor getActor() {
		return actor;
	}
	public ListBox<LevelData> getLevels() {
		return levels;
	}
	
	public void openActor(PackageActor actor) {
		assert actor != null;
		if (this.actor != null) closeActor();
		this.actor = actor;
		this.actor.open();
		actorgui.addChild(this.actor.getGui());
	}
	public void closeActor() {
		if (actor == null) return;
		actor.close();
		actor.getGui().removeFromParent();
	}
	private void notifyActor(Consumer<PackageActor> notify) {
		if (actor != null) {
			notify.accept(actor);
		}
	}
	
}
