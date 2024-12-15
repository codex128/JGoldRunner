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
package codex.goldrunner.effects;

import com.jme3.light.SpotLight;
import com.jme3.math.Vector3f;

/**
 *
 * @author gary
 */
public class SearchLight extends SpotLight {

    Vector3f lookat;

    public SearchLight(Vector3f position, Vector3f lookat) {
        super(position, new Vector3f());
        lookAt(lookat);
    }

    public Vector3f lookAt(Vector3f lookat) {
        setDirection(getDirectionTo(getPosition(), lookat));
        return getDirection();
    }

    public static Vector3f getDirectionTo(Vector3f here, Vector3f lookat) {
        return lookat.subtract(here).normalizeLocal();
    }

}
