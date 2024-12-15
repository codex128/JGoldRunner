/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.dev;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gary
 */
public class PlaytestLog {

    private final LinkedList<String> session = new LinkedList<>();
    File log;
    long begin;

    public PlaytestLog(File log) {
        this.log = log;
        begin = System.currentTimeMillis();
    }

    public void log(String str) {
        session.add(time(since()) + ": " + str);
    }

    public void write() throws IOException {
        if (!log.exists() || !log.isFile()) {
            return;
        }
        FileWriter writer = new FileWriter(log);
        for (String str : session) {
            writer.write(str + "\n");
        }
        writer.close();
        session.clear();
    }

    private long since() {
        return System.currentTimeMillis() - begin;
    }

    private String time(long time) {
        long seconds = time / 1000 % 60;
        long minutes = time / 1000 / 60;
        long hours = time / 1000 / 60 / 60;
        return (hours != 0 ? hours + ":" : "") + minutes + ":" + seconds;
    }

}
