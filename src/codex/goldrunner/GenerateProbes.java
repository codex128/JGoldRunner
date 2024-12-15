/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner;

import com.jme3.app.SimpleApplication;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.LightProbe;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author codex
 */
public class GenerateProbes extends SimpleApplication {
    
    private long frame = 0;
    private EnvironmentCamera envCam;
    private final File target = new File(System.getProperty("user.home") + "/probe.j3o");
    
    public static void main(String[] args) {
        new GenerateProbes().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        Texture tex = assetManager.loadTexture("Textures/lagoon_south.jpg");
        tex.setWrap(Texture.WrapMode.Repeat);
        Spatial sky = SkyFactory.createSky(assetManager, tex, tex, tex, tex, tex, tex);
        rootNode.attachChild(sky);
        
        envCam = new EnvironmentCamera();
        stateManager.attach(envCam);
        
    }
    @Override
    public void simpleUpdate(float tpf) {
        if (++frame == 5) {
            System.out.println("starting rendering of light probe...");
            LightProbeFactory.makeProbe(envCam, rootNode, EnvMapUtils.GenerationType.Fast, new JobProgressAdapter<LightProbe>() {
                @Override
                public void done(LightProbe result) {
                    System.out.println("finished rendering light probe");
                    rootNode.addLight(result);
                    try {
                        BinaryExporter.getInstance().save(rootNode, target);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    stop();
                }
            });
        }
    }
    
}
