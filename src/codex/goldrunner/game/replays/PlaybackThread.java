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
package codex.goldrunner.game.replays;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Contains information about a particular element in the level.
 * 
 * @author gary
 */
public class PlaybackThread {
	
	PlaybackActor control;
	LinkedList<KeyFrame> keyframes = new LinkedList<>();
	KeyFrame next;
	int index = 0;
	float time = 0f;
	
	public PlaybackThread(PlaybackActor control, Collection<KeyFrame> keyframes) {
		this.control = control;
		this.keyframes.addAll(keyframes);
		restart();
	}
	public PlaybackThread(PlaybackActor control, KeyFrame... keyframes) {
		this.control = control;
		addAll(keyframes);
		restart();
	}
	
	public void restart() {
		if (!this.keyframes.isEmpty()) {
			next = this.keyframes.getFirst();
		}
		else {
			next = null;
		}
		index = 0;
	}
	public void update(float tpf) {
		if (next == null) return;
		if (time >= next.getExecutionTime()) {
			if (control.onKeyFrameEvent(next)) {
				index++;
				if (index < keyframes.size()) {
					next = keyframes.get(index);
				}
				else {
					next = null;
				}
				time = 0f;
				return;
			}
			time = next.getExecutionTime();
			return;
		}
		time += tpf;
	}
	
	public boolean isFinished() {
		return next == null;
	}	
	public PlaybackActor getControlledActor() {
		return control;
	}
	public KeyFrame getNextKeyframe() {
		return next;
	}
	public int getIndexOfNextKeyframe() {
		return index;
	}
	public float getTimeSinceLastKeyframe() {
		return time;
	}
	
	private void addAll(KeyFrame... keyframes) {
		for (KeyFrame kf : keyframes) {
			this.keyframes.addLast(kf);
		}
	}
	
}
