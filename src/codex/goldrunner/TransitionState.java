/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/BaseAppState.java to edit this template
 */
package codex.goldrunner;

import codex.jmeutil.Timer;
import codex.jmeutil.TimerListener;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import java.util.function.Consumer;

/**
 *  
 *  
 *
 * @author gary 
 */
public class TransitionState extends BaseAppState implements TimerListener {

    Timer timer = new Timer(.2f);
    Consumer<Application> between;

    @Override
    protected void initialize(Application app) {
        timer.addListener(this);
        timer.setCycleMode(Timer.CycleMode.ONCE);
        timer.pause();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        timer.update(tpf);
    }

    public void transition(Consumer<Application> between) {
        this.between = between;
        timer.reset();
        timer.start();
    }

    @Override
    public void onTimerFinish(Timer timer) {
        if (between != null) {
            between.accept(getApplication());
        }
        between = null;
    }

}
