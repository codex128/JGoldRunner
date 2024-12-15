/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.profile;

import codex.j3map.J3map;

/**
 *
 * @author gary
 */
public class Profile {

    private J3map source;

    public Profile(J3map source) {
        this.source = source;
    }

    public String getUsername() {
        return source.getString("username");
    }

    public int getEstimatedSkill() {
        return source.getInteger("skill");
    }

    public int getAttempts() {
        return source.getInteger("attempts", 0);
    }

    public int getTimePlayed() {
        return source.getInteger("playtime");
    }

    public boolean tryPassword(String guess) {
        return guess.equals(source.getString("password"));
    }

    public void add(String key, int num) {
        Integer value = source.getInteger(key);
        if (value == null) {
            source.store(key, num);
        } else {
            source.overwrite(key, value + num);
        }
    }

    protected J3map getSource() {
        return source;
    }

    public J3map getSettingsSource() {
        return source.getJ3map("settings");
    }

    public void setSettingsSource(J3map settings) {
        source.overwrite("settings", settings);
    }

    @Override
    public String toString() {
        return getUsername();
    }

}
