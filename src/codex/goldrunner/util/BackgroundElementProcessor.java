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

import codex.goldrunner.game.BackgroundElement;
import codex.j3map.processors.J3mapPropertyProcessor;
import java.awt.Point;

/**
 *
 * @author gary
 */
public class BackgroundElementProcessor implements J3mapPropertyProcessor<BackgroundElement> {

    @Override
    public String getPropertyIdentifier() {
        return "BackgroundElement";
    }

    @Override
    public Class<BackgroundElement> type() {
        return BackgroundElement.class;
    }

    @Override
    public BackgroundElement process(String str) {
        if (!str.startsWith(getPropertyIdentifier() + "(") || !str.endsWith(")")) {
            return null;
        }
        String[] args = str.substring(
                getPropertyIdentifier().length() + 1, str.length() - 1).split(",");
        if (args.length != 3) {
            throw new IllegalArgumentException("J3map BackgroundElement processor requires 3 arguments!");
        }
        int i = 0;
        for (String arg : args) {
            args[i++] = arg.trim();
        }
        String path = args[0].substring(1, args[0].length() - 1);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        return new BackgroundElement(path, new Point(x, y));
    }

    @Override
    public String[] export(BackgroundElement property) {
        return new String[]{
            getPropertyIdentifier() + "("
            + "\"" + property.getModel() + "\","
            + property.getIndex().x + ","
            + property.getIndex().y + ")"
        };
    }

}
