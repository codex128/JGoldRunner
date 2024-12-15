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

import codex.goldrunner.game.replays.KeyFrame;
import codex.j3map.processors.J3mapPropertyProcessor;

/**
 *
 * @author gary
 */
public class KeyFrameProcessor implements J3mapPropertyProcessor<KeyFrame> {

    @Override
    public Class<KeyFrame> type() {
        return KeyFrame.class;
    }

    @Override
    public KeyFrame process(String str) {
        if (!str.startsWith(getPropertyIdentifier() + "(") || !str.endsWith(")")) {
            return null;
        }
        String[] args = str.substring(getPropertyIdentifier().length() + 1,
                str.length() - 1).split(",");
        String action = args[0].trim();
        float time = Float.parseFloat(args[1].trim());
        return new KeyFrame(action, time);
    }

    @Override
    public String[] export(KeyFrame property) {
        return new String[]{getPropertyIdentifier() + "(" + property.getAction() + "," + property.getExecutionTime() + ")"};
    }

    @Override
    public String getPropertyIdentifier() {
        return "KeyFrame";
    }

    @Override
    public KeyFrame[] createArray(int length) {
        return new KeyFrame[length];
    }

}
