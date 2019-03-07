package org.treeops.ui.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public interface FileChooserAction {
	void fileAction(File f) throws Exception;

	static JFileChooser loadChooser = fileChooser();
	static JFileChooser saveChooser = fileChooser();
	static JFileChooser saveDirChooser = fileChooser();

	public static JFileChooser fileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		return fc;
	}

	public static void save(JFrame frame, FileChooserAction fileAction) {
		if (saveChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				fileAction.fileAction(saveChooser.getSelectedFile());
			} catch (Exception ex) {
				GuiUtils.handleError(frame, ex);
			}
		}
	}

	public static void saveDir(JFrame frame, FileChooserAction fileAction) {
		saveDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (saveDirChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				fileAction.fileAction(saveDirChooser.getSelectedFile());
			} catch (Exception ex) {
				GuiUtils.handleError(frame, ex);
			}
		}
	}

	public static void loadFile(JFrame frame, FileChooserAction fileAction) {
		internalLoad(frame, fileAction, JFileChooser.FILES_ONLY);
	}

	public static void loadFileOrDir(JFrame frame, FileChooserAction fileAction) {
		internalLoad(frame, fileAction, JFileChooser.FILES_AND_DIRECTORIES);
	}

	static void internalLoad(JFrame frame, FileChooserAction fileAction, int mode) {
		loadChooser.setFileSelectionMode(mode);

		if (loadChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				fileAction.fileAction(loadChooser.getSelectedFile());
			} catch (Exception ex) {
				GuiUtils.handleError(frame, ex);
			}
		}
	}

}
