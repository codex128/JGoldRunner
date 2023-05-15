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

import codex.j3map.J3map;
import com.jme3.input.MouseInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.ListBox;

/**
 *
 * @author gary
 */
public class UnitEditor extends MapEditor {
	
	ListBox<UnitTypeSelection> types = new ListBox<>();
	BoxSelector boxselect = new BoxSelector();
	
	public UnitEditor(LevelEditorState editor) {
		super(editor);
		initialize();
	}
	
	@Override
	protected void initialize() {
		types.setVisibleItems(12);
		boxselect.initializeBoxGeometry(getAssets(), ColorRGBA.Magenta);
		getToolsGui().addChild(types);
		J3map source = getSelectionSource().getJ3map("units");
		source.forEachType(String[].class, (property) -> {
			types.getModel().add(new UnitTypeSelection(
					property[0], property[1], property[2]));
		});
	}
	@Override
	protected void onEnable() {
		getEditor().getGridNode().attachChild(boxselect.getBoxGeometry());
	}
	@Override
	protected void onDisable() {
		boxselect.getBoxGeometry().removeFromParent();
		boxselect.clear();
	}
	
	@Override
	public void mouseButtonEvent(MouseButtonEvent mbe, Spatial sptl, Spatial sptl1) {
		if (!boxselect.active() && mbe.isPressed()) {
			Slot slot = getEditor().getSlotBySpatial(sptl);
			if (slot != null) {
				boxselect.placePrimarySlot(slot);
			}
		}
		else if (boxselect.active() && mbe.isReleased()) {
			if (mbe.getButtonIndex() == MouseInput.BUTTON_LEFT &&
					types.getSelectedItem() != null) {
				String key = types.getSelectedItem().key;
				if (!key.equals("hero")) {
					boxselect.forEachContained(getSlots(), (slot) -> {
						if (slot.getUnit() == null ||
								!slot.getUnit().getKey().equals(key)) {
							editor.setSlotUnit(slot, new Unit(types.getSelectedItem().key));
						}
					});
				}
				else {
					getEditor().setHeroSlot(boxselect.getSlots()[1]);
				}
			}
			else if (mbe.getButtonIndex() == MouseInput.BUTTON_RIGHT) {
				boxselect.forEachContained(getSlots(), (slot) -> {
					editor.setSlotUnit(slot, null);
				});
			}
			boxselect.clear();
		}
	}
	@Override
	public void mouseEntered(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
		if (boxselect.active()) {
			Slot slot = getEditor().getSlotBySpatial(sptl);
			if (slot != null) {
				boxselect.placeSecondarySlot(slot);
			}
		}
	}
	
	
	private static class UnitTypeSelection {
		String name;
		String key;
		String character;		
		private UnitTypeSelection(String name, String key, String character) {
			this.name = name;
			this.key = key;
			this.character = character;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
}
