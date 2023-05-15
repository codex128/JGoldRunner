/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package codex.goldrunner.gui;

import codex.jmeutil.listen.Listenable;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.style.ElementId;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class DoublecheckPopup extends Container
		implements Listenable<DoublecheckListener> {
	
	Label title = new Label("Are you sure?", new ElementId("window.title.label"));
	Label explaination = new Label("Do you really want to do that?");
	Container buttons = new Container();
	ActionButton yes;
	ActionButton no;
	LinkedList<DoublecheckListener> listeners = new LinkedList<>();
	Command<DoublecheckPopup> command = (popup) -> {};
	
	public DoublecheckPopup() {
		initGui();
	}
	public DoublecheckPopup(String name) {
		super(name);
		initGui();
	}
	
	private void initGui() {
		addChild(title);
		addChild(explaination);
		addChild(buttons);
		buttons.setLayout(new BoxLayout(Axis.X, FillMode.Even));
		no = buttons.addChild(
				new ActionButton(new CallMethodAction(this, "no")));
		yes = buttons.addChild(
				new ActionButton(new CallMethodAction(this, "yes")));
		no.setText("No");
		yes.setText("Yes");
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
	public void setExplaination(String explaination) {
		this.explaination.setText(explaination);
	}
	public void setYesButtonText(String text) {
		yes.setText(text);
	}
	public void setNoButtonText(String text) {
		no.setText(text);
	}
	public void setCommand(Command<DoublecheckPopup> command) {
		this.command = command;
	}
	
	public void yes() {
		command.execute(this);
		notifyListeners(l -> {
			l.onButton(this);
			l.onYesButton(this);
		});
	}
	public void no() {
		notifyListeners(l -> {
			l.onButton(this);
			l.onNoButton(this);
		});
	}
	
	public String getTitle() {
		return title.getText();
	}
	public String getExplaination() {
		return explaination.getText();
	}
	public String getYesButtonText() {
		return yes.getText();
	}
	public String getNoButtonText() {
		return no.getText();
	}
	public Command<DoublecheckPopup> getCommand() {
		return command;
	}
	public boolean isActive() {
		return getParent() != null;
	}

	@Override
	public Collection<DoublecheckListener> getListeners() {
		return listeners;
	}	
	
}
