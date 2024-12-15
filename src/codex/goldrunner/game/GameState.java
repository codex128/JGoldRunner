/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner.game;

import codex.goldrunner.GameGlobals;
import codex.goldrunner.MenuState;
import codex.goldrunner.SettingsState;
import codex.goldrunner.game.management.LevelPool;
import codex.goldrunner.profile.ProfileManager;
import codex.jmeutil.Timer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.DynamicInsetsComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import java.awt.Point;

/**
 *  
 *  
 *
 * @author gary 
 */
public class GameState extends BaseAppState implements LevelListener {

    LevelPool pool;
    LevelState level;
    Node gui = new Node("game state gui");
    Node gamegui = new Node("in game gui");
    Node resultsgui = new Node("other gui");
    Container attemptInfo = new Container();
    Label attemptText = new Label("");
    Label timetext = new Label("");
    Label nametext = new Label("");
    Transform start = new Transform();
    Transform end = new Transform();
    Timer playtime = new Timer(-1f);
    Timer movedelay = new Timer(.5f);
    float percmoved = 0f;
    float movespeed = .05f;
    int attempt = 0;
    int totalattempts = 0;
    int totaltime = 0;
    boolean skimped = false;
    boolean ingame = false;

    public GameState() {
        setEnabled(false);
    }

    @Override
    protected void initialize(Application app) {
        gamegui.attachChild(attemptInfo);
        attemptInfo.setBackground(new QuadBackgroundComponent(
                new ColorRGBA(0f, 0f, 0f, .75f)));
        attemptInfo.setInsetsComponent(
                new DynamicInsetsComponent(.5f, .5f, .5f, .5f));
        float width = 85;
        float height = 20f;
        attemptInfo.setPreferredSize(new Vector3f(width, height, 0f));
        attemptInfo.addChild(attemptText);
        attemptText.setTextHAlignment(HAlignment.Center);
        attemptText.setTextVAlignment(VAlignment.Center);
        attemptText.setColor(ColorRGBA.White);
        Point size = GameGlobals.getWindowSize();
        start.setScale(4f);
        end.setScale(1f);
        start.setTranslation(size.x / 2 - width / 2 * start.getScale().x,
                size.y / 4 + height / 2 * start.getScale().y, 5f);
        end.setTranslation(5f, size.y - 5, 5f);
        movedelay.setCycleMode(Timer.CycleMode.ONCE);
        Container timeinfo = new Container();
        gamegui.attachChild(timeinfo);
        timeinfo.setBackground(new QuadBackgroundComponent(
                new ColorRGBA(0f, 0f, 0f, .75f)));
        float w = 65f;
        float h = 20f;
        timeinfo.setLocalTranslation(size.x - w, size.y, 0f);
        timeinfo.setPreferredSize(new Vector3f(w, h, 0f));
        timeinfo.setInsetsComponent(new DynamicInsetsComponent(
                .5f, .5f, .5f, .5f));
        timeinfo.addChild(timetext);
        timetext.setTextHAlignment(HAlignment.Center);
        timetext.setTextVAlignment(VAlignment.Center);
        timetext.setFontSize(17);
        timetext.setColor(ColorRGBA.White);
        gamegui.attachChild(nametext);
        nametext.setTextHAlignment(HAlignment.Center);
        nametext.setTextVAlignment(VAlignment.Top);
        nametext.setLocalTranslation(size.x / 2f, size.y - 2f, 0f);

        Container results = new Container();
        resultsgui.attachChild(results);
        results.setLocalTranslation(200, 400, 0);
        results.addChild(new Label("")).setName("attempt info");
        results.addChild(new Button("Try Again")).addClickCommands((source) -> {
            pool.refresh();
            closeResults();
            openGame();
        });
        results.addChild(new Button("Menu")).addClickCommands((source) -> {
            setEnabled(false);
            getState(MenuState.class).setEnabled(true);
        });
    }

    @Override
    protected void cleanup(Application app) {
        //getStateManager().detach(level);
    }

    @Override
    protected void onEnable() {
        ((SimpleApplication) getApplication()).getGuiNode().attachChild(gui);
        openGame();
    }

    @Override
    protected void onDisable() {
        gui.removeFromParent();
        gui.detachAllChildren();
        closeGame();
        setAttemptNum(1);
        totalattempts = 1;
        totaltime = 0;
    }

    @Override
    public void update(float tpf) {
        movedelay.update(tpf);
        playtime.update(tpf);
        if (!skimped) {
            timetext.setText(timeToString(playtime.timeToSeconds()));
        } else {
            timetext.setText("x:xx");
        }
        if (!movedelay.isRunning()) {
            if ((percmoved += movespeed) >= 1f) {
                percmoved = 1f;
                attemptInfo.setLocalTransform(end);
            } else {
                lerp(attemptInfo, start, end, percmoved);
            }
        }
    }

    public void setLevelPool(LevelPool pool) {
        this.pool = pool;
    }

    private void setAttemptNum(int n) {
        attempt = n;
        attemptText.setText("Attempt " + (!skimped ? attempt : "[X]"));
        if (!skimped) {
            attemptInfo.setLocalTransform(start);
            percmoved = 0f;
            movedelay.reset();
            movedelay.start();
        }
    }

    private void makeAttempt() {
        getState(ProfileManager.class).onLoggedIn((profile) -> {
            profile.add("attempts", 1);
        });
        setAttemptNum(attempt + 1);
    }

    private void makeFirstAttempt() {
        attempt = 0;
        makeAttempt();
    }

    private void addTotalAttempts(int n) {
        totalattempts += n;
    }

    private void applyPlayTime() {
        getState(ProfileManager.class).onLoggedIn((profile) -> {
            profile.add("playtime", playtime.timeToSeconds());
        });
        totaltime += playtime.timeToSeconds();
        playtime.reset();
    }

    private void lerp(Spatial spatial, Transform start, Transform end, float perc) {
        spatial.setLocalTranslation(end.getTranslation()
                .subtract(start.getTranslation()).multLocal(perc)
                .addLocal(start.getTranslation()));
//		spatial.setLocalRotation(new Quaternion()
//				.slerp(start.getRotation(), end.getRotation(), perc));
        spatial.setLocalScale(end.getScale()
                .subtract(start.getScale()).multLocal(perc)
                .add(start.getScale()));
    }

    private void openGame() {
        gui.attachChild(gamegui);
        level = new LevelState(this, pool.cycle(0));
        nametext.setText(level.getLevelData().getName());
        getStateManager().attach(level);
        ingame = true;
        skimped = false;
        playtime.reset();
        playtime.start();
        makeFirstAttempt();
    }

    private void loadNextLevel() {
        level.load(this, pool.cycle(0));
        nametext.setText(level.getLevelData().getName());
    }

    private void closeGame() {
        gamegui.removeFromParent();
        if (ingame) {
            getStateManager().detach(level);
        }
        playtime.pause();
        applyPlayTime();
        ingame = false;
    }

    private void openResults() {
        gui.attachChild(resultsgui);
        GameGlobals.getChild(resultsgui, Label.class, "attempt info")
                .setText("You made " + totalattempts + " attempt" + (totalattempts == 1 ? "" : "s") + " and took " + timeToString(totaltime) + " to beat " + pool.getTotalLevels() + " levels!");
    }

    private void closeResults() {
        resultsgui.removeFromParent();
    }

    private String timeToString(int sec) {
        int seconds = Timer.secondsToLimitedSeconds(sec);
        int minutes = Timer.secondsToMinutes(sec, false);
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    @Override
    public void onFailure(LevelState level) {
        level.restart();
    }

    @Override
    public void onVictory(LevelState level) {
        if (!pool.hasNextLevel()) {
            closeGame();
            openResults();
        } else {
            loadNextLevel();
        }
        if (!SettingsState.getInstance().displayTotalTime()) {
            applyPlayTime();
            playtime.start();
        }
        setAttemptNum(1);
    }

    @Override
    public void onRestart(LevelState level) {
        //applyPlayTime();
        makeAttempt();
        addTotalAttempts(1);
        level.load(this, level.getLevelData());
    }

    @Override
    public void onSkimp(LevelState level) {
        skimped = true;
        getState(ProfileManager.class).onLoggedIn((profile) -> {
            profile.add("skimps", 1);
        });
        if (!pool.hasNextLevel()) {
            closeGame();
            openResults();
            addTotalAttempts(1);
        } else {
            loadNextLevel();
            if (!SettingsState.getInstance().displayTotalTime()) {
                applyPlayTime();
                playtime.start();
            }
            makeAttempt();
            addTotalAttempts(1);
        }
    }

    @Override
    public void onQuit(LevelState level) {
        setEnabled(false);
        getStateManager().getState(MenuState.class).setEnabled(true);
    }

    @Override
    public void onError(LevelState level, String reason) {
        getState(OptionPanelState.class).show(
                "A Fatal Error has Occured!", "Reason: " + reason);
        onQuit(level);
    }

}
