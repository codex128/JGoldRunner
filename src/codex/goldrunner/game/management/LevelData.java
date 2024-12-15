/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.game.management;

import codex.goldrunner.GameGlobals;
import codex.goldrunner.util.SnowflakeFactory;
import codex.j3map.J3map;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author gary
 */
public class LevelData {

    // todo: whenever level formats are changed, increase this by 1.
    public static final int VERSION = 2;

    J3map source;

    public LevelData(J3map source) {
        setSource(source);
    }

    public void setSource(J3map source) {
        assert source != null;
        this.source = source;
    }

    public J3map getSource() {
        return source;
    }

    public String getSaveLocation() {
        return source.getString("save");
    }

    public String getName() {
        return source.getString("name");
    }

    public void deleteSource() {
        if (source.propertyExists("save")) {
            File save = GameGlobals.FileSystem.getFile(GameGlobals.FileSystem.EXTERNALGAMEDATA, getSaveLocation());
            if (save.exists()) {
                save.delete();
            }
        }
    }

    public J3map export() {
        J3map ex = source.clone();
        ex.delete("save");
        return ex;
    }

    public void save(SnowflakeFactory sff) throws IOException {
        if (!source.propertyExists("save")) {
            source.store("save", "levels/GR" + sff.getNextId() + ".j3map");
        }
        File save = GameGlobals.FileSystem.getFile(GameGlobals.FileSystem.EXTERNALGAMEDATA, getSaveLocation());
        source.export(save);
    }

    @Override
    public String toString() {
        return source.getString("name");
    }

}
