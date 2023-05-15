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
package codex.goldrunner.gui;

import codex.jmeutil.listen.Listenable;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.component.BoxLayout;
import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class FileBrowser extends Container implements Listenable<FileBrowserListener> {
	
	LinkedList<File> history = new LinkedList<>();
	ListBox<FileOption> files = new ListBox();
	File displaying;
	LinkedList<FileBrowserListener> listeners = new LinkedList<>();
	FileFilter filter;
	
	public FileBrowser() {
		initializeGui();
	}
	public FileBrowser(File base) {
		initializeGui();
		open(base);
	}	
	
	public void open(File base) {
		displaying = base;
		listContainedFiles(displaying);
	}
	public void close() {
		files.getModel().clear();
		history.clear();
		displaying = null;
	}
	public void setFileFilter(FileFilter filter) {
		this.filter = filter;
	}
	
	public LinkedList<File> getHistory() {
		return history;
	}
	public ListBox<FileOption> getListBox() {
		return files;
	}
	public File getDisplayedFile() {
		return displaying;
	}
	public FileFilter getFileFilter() {
		return filter;
	}
	
	private void initializeGui() {
		setLayout(new BoxLayout(Axis.Y, FillMode.Proportional));
		addChild(files);
		addChild(new Button("Open")).addClickCommands((source) -> {
			FileOption selected = files.getSelectedItem();
			if (selected == null);
			else if (selected.file.isDirectory()) {
				history.addLast(displaying);
				listContainedFiles(selected.file);
			}
			else {
				notifyListeners(l -> l.onFileChosen(this, selected.file));
			}
		});
		addChild(new Button("Back up")).addClickCommands((source) -> {
			if (history.isEmpty()) return;
			listContainedFiles(history.removeLast());
		});
		addChild(new Button("Cancel")).addClickCommands((source) -> {
			notifyListeners(l -> l.onBrowserCanceled(this));
		});
	}
	private void listContainedFiles(File file) {
		if (!file.isDirectory()) return;
		displaying = file;
		files.getModel().clear();
		File[] contained;
		if (filter != null) {
			contained = file.listFiles(filter);
		}
		else {
			contained = file.listFiles();
		}
		for (File f : contained) {
			files.getModel().add(new FileOption(f));
		}
	}

	@Override
	public Collection<FileBrowserListener> getListeners() {
		return listeners;
	}
	
	
	public static class FileOption {
		File file;
		private FileOption(String path) {
			file = new File(path);
		}
		private FileOption(File file) {
			this.file = file;
		}
		public File getFile() {
			return file;
		}
		@Override
		public String toString() {
			return (file.isDirectory() ? "[folder]" : "")+" "+file.getName();
		}
	}
	
}
