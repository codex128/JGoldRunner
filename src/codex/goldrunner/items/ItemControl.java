/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package codex.goldrunner.items;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author gary
 */
public class ItemControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    ItemCarrier carrier;
    ParticleEmitter emitter;

    @Override
    protected void controlUpdate(float tpf) {
        if (emitter != null && emitter.getParent() != null
                && emitter.getNumVisibleParticles() == 0) {
            emitter.removeFromParent();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        ItemControl control = new ItemControl();
        //TODO: copy parameters to new Control
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }

    public boolean attach(ItemCarrier carrier) {
        if (carrier.canAcceptItem(this) && (this.carrier == null
                || this.carrier.canReleaseItem())) {
            if (this.carrier != null) {
                this.carrier.releaseItem();
            }
            carrier.acceptItem(this);
            spatial.setCullHint(Spatial.CullHint.Inherit);
            this.carrier = carrier;
            return true;
        }
        return false;
    }

    public boolean detach() {
        if (carrier != null && carrier.canReleaseItem()) {
            spatial.setCullHint(Spatial.CullHint.Never);
            carrier.releaseItem();
            carrier = null;
            return true;
        }
        return false;
    }

    protected void initParticleEmitter(AssetManager assets) {
        emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
        emitter.setMaterial(assets.loadMaterial("Materials/effects/goldmagic.j3m"));
        emitter.setImagesX(2);
        emitter.setImagesY(2); // 3x3 texture animation
        emitter.setRotateSpeed(4);
        emitter.setSelectRandomImage(true);
        emitter.setGravity(0f, 0f, 0f);
        emitter.setLowLife(.2f);
        emitter.setHighLife(.2f);
        emitter.setNumParticles(1);
        emitter.setParticlesPerSec(0);
    }

    public ParticleEmitter releaseParticleEffect() {
        if (emitter != null) {
            if (spatial.getParent() != null) {
                spatial.getParent().attachChild(emitter);
            }
            emitter.setLocalTransform(spatial.getLocalTransform());
            emitter.emitAllParticles();
        }
        return emitter;
    }

}
