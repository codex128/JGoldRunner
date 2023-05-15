/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *  
 *  
 *
 * @author gary 
 */
public class AudioLibrary extends BaseAppState {
    
	HashMap<String, Audio> audio = new HashMap<>();
	LinkedList<Audio> loadbucket = new LinkedList<>();
	
    @Override
    protected void initialize(Application app) {
        //It is technically safe to do all initialization and cleanup in the         
        //onEnable()/onDisable() methods. Choosing to use initialize() and         
        //cleanup() for this is a matter of performance specifics for the         
        //implementor.        
        //TODO: initialize your AppState, e.g. attach spatials to rootNode    
    }
    @Override
    protected void cleanup(Application app) {
        //TODO: clean up what you initialized in the initialize method,        
        //e.g. remove all spatials from rootNode    
    } 
    @Override
    protected void onEnable() {
        //Called when the state is fully enabled, ie: is attached and         
        //isEnabled() is true or when the setEnabled() status changes after the         
        //state is attached.    
    }    
    @Override
    protected void onDisable() {
        //Called when the state was previously enabled but is now disabled         
        //either because setEnabled(false) was called or the state is being         
        //cleaned up.
    }
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime    
    }
	
	public void addAudio(String name, String source) {
		Audio aud = new Audio(source);
		audio.put(name, aud);
		loadbucket.add(aud);
	}
	public AudioData getAudioData(String name) {
		return audio.get(name).data;
	}
	public AudioNode createAudioNode(String name) {
		return new AudioNode(audio.get(name).data, new AudioKey());
	}
	
	public void loadAudioBucket() {
		for (Audio aud : loadbucket) {
			aud.load(getApplication().getAssetManager());
		}
		loadbucket.clear();
	}
	public void loadNext() {
		if (loadbucket.isEmpty()) return;
		loadbucket.getFirst().load(getApplication().getAssetManager());
		loadbucket.removeFirst();
	}
	public boolean audioInBucket() {
		return !loadbucket.isEmpty();
	}
	
	
	private static class Audio {
		String source;
		AudioData data;
		Audio(String source) {
			this.source = source;
		}
		void load(AssetManager assets) {
			data = assets.loadAudio(source);
		}
	}
	
}
