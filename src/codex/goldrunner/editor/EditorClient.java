/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.goldrunner.editor;

import codex.goldrunner.game.management.LevelData;

/**
 *
 * @author gary
 */
public interface EditorClient {

    public abstract void saveNewLevelData(LevelData data);

    public abstract void onExit();

}
