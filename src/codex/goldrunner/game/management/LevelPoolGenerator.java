/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner.game.management;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *  
 *  
 *
 * @author gary 
 */
public class LevelPoolGenerator extends BaseAppState {
	
	LevelManager lm;
    
    @Override
    protected void initialize(Application app) {
		lm = getState(LevelManager.class);
	}
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {} 
    @Override
    public void update(float tpf) {}
	
	public LevelPool generateRandomPool(int length) {
		LevelPool pool = new LevelPool();
		ArrayList<LevelData> available = new ArrayList<>();
		for (LevelPackage pack : lm.getPackages()) {
			available.addAll(pack.getLevels());
		}
		while (!available.isEmpty() && length != 0) {
			pool.add(available.remove(ThreadLocalRandom.current().nextInt(available.size())));
			length--;
		}
		return pool;
	}
	public LevelPool generateRandomPool(int length, LevelPackage... packages) {
		LevelPool pool = new LevelPool();
		ArrayList<LevelData> available = new ArrayList<>();
		for (LevelPackage pack : packages) {
			available.addAll(pack.getLevels());
		}
		while (!available.isEmpty() && length != 0) {
			pool.add(available.remove(ThreadLocalRandom.current().nextInt(available.size())));
			length--;
		}
		return pool;
	}
	
}
