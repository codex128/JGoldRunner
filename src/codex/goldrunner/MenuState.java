/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner;

import codex.goldrunner.game.management.LevelManager;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioNode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;

/**
 *  
 *  
 *
 * @author gary 
 */
public class MenuState extends BaseAppState {
	
	Node gui = new Node();
	AudioNode music;
	
	
	public MenuState() {
		setEnabled(false);
	}
	
	
    @Override
    protected void initialize(Application app) {
		AppSettings as = app.getContext().getSettings();
		Container background = GameGlobals.Gui.createBackgroundContainer(as, -5);
		QuadBackgroundComponent quad = new QuadBackgroundComponent(
				app.getAssetManager().loadTexture("Textures/renders/render1.png"));
		/*quad.getMaterial().getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		quad.getMaterial().getMaterial().setTransparent(true);
		background.setQueueBucket(RenderQueue.Bucket.Transparent);*/
		background.setBackground(quad);
		gui.attachChild(background);
		
		Container title = new Container();
		BorderLayout bl = new BorderLayout();
		title.setLayout(bl);
		Label lbl = bl.addChild(BorderLayout.Position.East, new Label("GoldRunner"));
		lbl.setFont(app.getAssetManager().loadFont("Interface/Fonts/Main.fnt"));
		lbl.setFontSize(50);
		lbl.setColor(ColorRGBA.White);
		Vector3f size = title.getPreferredSize();
		title.setLocalTranslation(as.getWidth()-size.x+2, as.getHeight(), 0);
		QuadBackgroundComponent qbc = new QuadBackgroundComponent(
				app.getAssetManager().loadTexture("Textures/gradient.png"));
		title.setBackground(qbc);
		gui.attachChild(title);
		
		BoxLayout layout = new BoxLayout(Axis.Y, FillMode.Even);
		Container menu = new Container();
		menu.setLayout(layout);
		menu.setLocalTranslation(530, 400, 0);
		menu.setBackground(null);
		menu.setLocalScale(2);
		gui.attachChild(menu);
		
		Button play = menu.addChild(new Button("Start"));
		play.addClickCommands((Button source) -> {
			setEnabled(false);
			getStateManager().getState(TransitionState.class).transition((appl) -> {
				getStateManager().getState(LevelManager.class).setEnabled(true);
			});
		});
		
		music = getStateManager().getState(AudioLibrary.class)
				.createAudioNode("Menu");
		music.setPositional(false);
		music.setPitch(.8f);
		//gui.attachChild(music);
		music.setLooping(true);
    }
    @Override
    protected void cleanup(Application app) {
        //TODO: clean up what you initialized in the initialize method,        
        //e.g. remove all spatials from rootNode
    }
    @Override
    protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(gui);
		music.play();
    }
    @Override
    protected void onDisable() {
		gui.removeFromParent();
		music.stop();
    }
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime    
    }
	
}
