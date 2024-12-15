/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.dev;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author gary
 */
public class AppearanceTest extends SimpleApplication implements ActionListener {

    Vector2f margin = new Vector2f(3, 5);
    Point next = new Point(0, 0);
    int col = 20;

    public static void main(String[] args) {
        new AppearanceTest().start();
    }

    @Override
    public void simpleInitApp() {
        initLighting();
        initUserInput();
        flyCam.setMoveSpeed(30);

        /**
         * Write text on the screen (HUD)
         */
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("+");
        helloText.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() / 2, 0);
        guiNode.attachChild(helloText);

        addAnimatedModel("Models/runners/hero.j3o", "climb:2", "run:2", "hang:4");
        addAnimatedModel("Models/runners/enemy.j3o", "climb:2", "run:2", "hang:4");
        addAnimatedModel("Models/items/gold.j3o", "float");
        addModel("Models/units/bar.j3o");
        addModel("Models/units/brick.j3o");
        addModel("Models/units/escapeladder.j3o")
                .setMaterial(material("Materials/units/ladder.j3m"));
        addModel("Models/units/ladder.j3o")
                .setMaterial(material("Materials/units/ladder.j3m"));
    }

    @Override
    public void simpleUpdate(float tpf) {

    }

    private void initLighting() {
        Node probeNode = (Node) assetManager.loadModel("Scenes/defaultProbe.j3o");
        LightProbe probe = (LightProbe) probeNode.getLocalLightList().iterator().next();
        rootNode.addLight(probe);
        rootNode.addLight(new DirectionalLight(new Vector3f(1, 1, 1)));
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -1, -1)));
        rootNode.addLight(new AmbientLight(ColorRGBA.Gray));
    }

    private void initUserInput() {
        inputManager.addMapping("lmb", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "lmb");
    }

    private Spatial addModel(Spatial spatial) {
        Vector3f location = new Vector3f(next.x * margin.x, next.y * margin.y, 0);
        if (++next.x >= col) {
            next.y++;
            next.x = 0;
        }
        spatial.setLocalTranslation(location);
        rootNode.attachChild(spatial);
        return spatial;
    }

    private Spatial addModel(String name) {
        return addModel(assetManager.loadModel(name));
    }

    private Spatial addAnimatedModel(String name, String... actions) {
        Spatial spatial = addModel(name);
        AnimComposer anim = spatial.getControl(AnimComposer.class);
        if (anim != null) {
            anim.setCurrentAction(constructTestAction(anim, actions));
        }
        return spatial;
    }

    private Spatial addTexturedMesh(Mesh shape, String material) {
        Geometry g = new Geometry("primitive", shape);
        g.setMaterial(assetManager.loadMaterial(material));
        return addModel(g);
    }

    private String constructTestAction(AnimComposer anim, String... actions) {
        if (actions.length == 0) {
            return null;
        }
        ArrayList<Tween> tweens = new ArrayList<>();
        for (int i = 0; i < actions.length; i++) {
            String[] arr = actions[i].split(":");
            if (arr.length == 2) {
                Integer num = Integer.parseInt(arr[1]);
                for (int j = 0; j < num; j++) {
                    tweens.add(anim.action(arr[0]));
                }
            } else {
                tweens.add(anim.action(arr[0]));
            }
        }
        final String actname = "all actions";
        tweens.add(Tweens.callMethod(anim, "setCurrentAction", actname));
        anim.actionSequence(actname, tweens.toArray(new Tween[0]));
        return actname;
    }

    private Material material(String name) {
        return assetManager.loadMaterial(name);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("lmb") && isPressed) {
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            CollisionResults res = new CollisionResults();
            rootNode.collideWith(ray, res);
            if (res.size() > 0) {
                Vector3f contact = res.getClosestCollision().getContactPoint();
                Vector3f normal = res.getClosestCollision().getContactNormal();
                cam.setLocation(contact.add(normal.mult(3)));
                //cam.lookAt(normal.negate(), Vector3f.UNIT_Y);
            }
        }
    }

}
