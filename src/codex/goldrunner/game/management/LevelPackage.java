/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.game.management;

import codex.goldrunner.GameGlobals;
import codex.goldrunner.util.SnowflakeFactory;
import codex.j3map.J3map;
import com.jme3.asset.AssetManager;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class LevelPackage {
	
	J3map source;
	LinkedList<LevelData> levels = new LinkedList<>();
	int index = -1;
	
	public LevelPackage() {
		source = new J3map();
		loadDefaultData();
	}
	public LevelPackage(J3map source) {
		this.source = source;
		//importLevelsFromSource();
	}
	
	private void loadDefaultData() {
		source.store("name", "new");
	}
	public void load(AssetManager assets) {
		if (!importLevelsFromSource()) {
			String[] order = source.getStringArray("levels");
			for (String name : order) {
				addLevel(new LevelData(J3map.openJ3map(assets.loadAsset(name))));
			}
		}
	}
	public void addLevel(LevelData level) {
		levels.add(level);
	}
	public void replaceAllLevels(Collection<LevelData> lvls) {
		levels.clear();
		levels.addAll(lvls);
	}
	protected boolean removeLevel(LevelData level) {
		return levels.remove(level);
	}
	protected boolean contains(LevelData level) {
		return levels.contains(level);
	}
	
	public LevelData getNextLevel() {
		if (++index >= levels.size()) return null;
		return levels.get(index);
	}
	public LevelData getCurrentLevel() {
		if (index < 0 || index >= levels.size()) return null;
		return levels.get(index);
	}
	public LevelData getLevelAtIndex(int index) {
		assert index >= 0 && index < levels.size();
		return levels.get(index);
	}
	public void reset() {
		index = -1;
	}
	public boolean complete() {
		return index >= size();
	}	
	
	public void setName(String name) {
		source.overwrite("name", name);
	}
	
	public int size() {
		return levels.size();
	}
	public J3map getSource() {
		return source;
	}
	public String getName() {
		return source.getString("name");
	}
	public LinkedList<LevelData> getLevels() {
		return levels;
	}
	public String getSaveLocation() {
		return source.getString("save");
	}
	
	public void deleteSource() {
		if (source.propertiesExist(J3map.createChecker("save", String.class))) {
			File save = GameGlobals.FileSystem.getFile(GameGlobals.FileSystem.EXTERNALGAMEDATA, getSaveLocation());
			if (save.exists()) save.delete();
		}
		for (LevelData data : levels) {
			data.deleteSource();
		}
	}
	public void save(SnowflakeFactory sff) throws IOException {
		String[] l = new String[levels.size()];
		int i = 0;
		for (LevelData data : levels) {
			data.save(sff);
			l[i++] = data.getSaveLocation();
		}
		if (!source.propertyExists("save")) {
			source.store("save", "packages/GRP"+sff.getNextId()+".j3map");
		}
		source.overwrite("levels", l);
		source.export(GameGlobals.FileSystem.getFilePath(GameGlobals.FileSystem.EXTERNALGAMEDATA, getSaveLocation()));
	}
	public J3map export() {
		J3map ex = source.clone();
		ex.delete("save");
		ex.delete("levels");
		J3map lvls = new J3map();
		ex.store("ex_levels", lvls);
		int i = 1;
		for (LevelData lv : levels) {
			lvls.store("level"+(i++), lv.export());
		}
		return ex;
	}
	public boolean importLevelsFromSource() {
		return source.onPropertyExists("ex_levels",
				J3map.class, Boolean.class, false, (lvls) -> {
			lvls.forEachType(J3map.class, (property) -> {
				addLevel(new LevelData(property));
			});
			source.delete("ex_levels");
			return true;
		});
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
