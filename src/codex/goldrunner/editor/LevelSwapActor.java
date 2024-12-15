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
package codex.goldrunner.editor;

import codex.goldrunner.game.management.LevelData;
import codex.goldrunner.gui.ObjectLabel;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Label;

/**
 *
 * @author gary
 */
public class LevelSwapActor extends PackageActor {

    ObjectLabel<LevelData> swap1 = new ObjectLabel<>();
    ObjectLabel<LevelData> swap2 = new ObjectLabel<>();

    public LevelSwapActor(PackageEditorState editor) {
        super(editor);
        initializeGui();
    }

    @Override
    protected void initializeGui() {
        getGui().addChild(new Label("Swap"));
        getGui().addChild(swap1);
        getGui().addChild(new Label("with"));
        getGui().addChild(swap2);
        getGui().addChild(new Button("Swap")).addClickCommands((source) -> {
            if (swap1.getObject() != null && swap2.getObject() != null) {
                swapLevels(swap1.getObject(), swap2.getObject());
                getEditor().closeActor();
            }
        });
        getGui().addChild(new Button("Cancel")).addClickCommands((source) -> {
            getEditor().closeActor();
        });
    }

    @Override
    protected void onLevelSelected(LevelData level) {
        if (swap1.getObject() == null) {
            swap1.setObject(level);
        } else if (swap2.getObject() == null && swap1.getObject() != level) {
            swap2.setObject(level);
        }
    }

    @Override
    protected void close() {
        clearLevelsSwapping();
    }

    private void swapLevels(LevelData l1, LevelData l2) {
        if (l1 == null || l2 == null || l1 == l2) {
            return;
        }
        int i1 = getEditor().getLevels().getModel().indexOf(l1);
        int i2 = getEditor().getLevels().getModel().indexOf(l2);
        if (i1 < 0 || i2 < 0) {
            return;
        }
        if (i1 < i2) {
            moveLevel(l1, i2);
            moveLevel(l2, i1);
        } else {
            moveLevel(l2, i1);
            moveLevel(l1, i2);
        }
    }

    private void moveLevel(LevelData level, int index) {
        getEditor().getLevels().getModel().remove(level);
        getEditor().getLevels().getModel().add(index, level);
    }

    private void clearLevelsSwapping() {
        swap1.setObject(null);
        swap2.setObject(null);
    }

}
