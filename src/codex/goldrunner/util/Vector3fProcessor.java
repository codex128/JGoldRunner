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

import codex.j3map.processors.J3mapPropertyProcessor;
import com.jme3.math.Vector3f;

/**
 *
 * @author gary
 */
public class Vector3fProcessor implements J3mapPropertyProcessor<Vector3f> {

	@Override
	public String getPropertyIdentifier() {
		return "Vector3f";
	}
	@Override
	public Class<Vector3f> type() {
		return Vector3f.class;
	}
	@Override
	public Vector3f process(String str) {
		if (!str.startsWith(getPropertyIdentifier()+"(") || !str.endsWith(")")) {
			return null;
		}
		String[] args = str.substring(getPropertyIdentifier().length()+1,
				str.length()-1).split(",");
		int i = 0;
		for (String arg : args) {
			args[i++] = arg.trim();
		}
		float x = Float.parseFloat(args[0]);
		float y = Float.parseFloat(args[1]);
		float z = Float.parseFloat(args[2]);
		return new Vector3f(x, y, z);
	}
	@Override
	public String[] export(Vector3f property) {
		return new String[]{
			getPropertyIdentifier()+"("
				+property.x+","+property.y+","+property.z+")"
		};
	}
	
}
