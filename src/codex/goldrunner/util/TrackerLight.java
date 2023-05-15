/*
 * The MIT License
 *
 * Copyright 2023 gary.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package codex.goldrunner.util;

import codex.goldrunner.GameGlobals;
import codex.jmeutil.math.FDomain;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author gary
 */
public class TrackerLight extends SpotLight {
	
	Spatial spatial;
	Vector3f track = new Vector3f();
	float lag = 0f;
	boolean wander = false;
	FDomain wanderX = new FDomain(0f, 20f);
	FDomain wanderY = new FDomain(0f, 20f);
	
	public TrackerLight() {
		super();
	}
	public TrackerLight(Vector3f position) {
		super(position, new Vector3f(0f, 0f, 1f));
	}
	public TrackerLight(Vector3f position, ColorRGBA color) {
		super(position, new Vector3f(0f, 0f, 1f), color);
	}
	public TrackerLight(Vector3f position, Vector3f direction) {
		super(position, direction);
	}
	public TrackerLight(Vector3f position, Vector3f direction, ColorRGBA color) {
		super(position, direction, color);
	}
	
	public void updateDirection(float tpf) {
		if (spatial == null) return;
		//track.addLocal(lerp(track, getIdealTrackingLocation(), lag));
		track.set(getIdealTrackingLocation());
		setDirection(track.subtract(getPosition()).normalizeLocal());
	}
	private Vector3f lerp(Vector3f vec, Vector3f target, float scalar) {
		return target.subtract(vec).multLocal(scalar);
	}
	
	public void track(Spatial spatial) {
		this.spatial = spatial;
		if (this.spatial != null) {
			track.set(getIdealTrackingLocation());
		}
	}
	public Spatial getSpatialTracking() {
		return spatial;
	}
	public Vector3f getCurrentTrackingLocation() {
		return track;
	}
	public Vector3f getIdealTrackingLocation() {
		return spatial.getWorldTranslation();
	}
	
}
