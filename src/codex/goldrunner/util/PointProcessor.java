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
import java.awt.Point;

/**
 *
 * @author gary
 */
public class PointProcessor implements J3mapPropertyProcessor<Point> {

    @Override
    public String getPropertyIdentifier() {
        return "Point";
    }

    @Override
    public Class<Point> type() {
        return Point.class;
    }

    @Override
    public Point process(String str) {
        if (!str.startsWith(getPropertyIdentifier() + "(") || !str.endsWith(")")) {
            return null;
        }
        String[] args = str.substring(getPropertyIdentifier().length() + 1,
                str.length() - 1).split(",");
        int i = 0;
        for (String arg : args) {
            args[i++] = arg.trim();
        }
        return new Point(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    @Override
    public String[] export(Point property) {
        return new String[]{getPropertyIdentifier() + "(" + property.x + "," + property.y + ")"};
    }

}
