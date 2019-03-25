package org.treeops.ui.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileChooserUtil {

	private static final JFileChooser LOAD_CHOOSER = fileChooser();
	private static final JFileChooser SAVE_CHOOSER = fileChooser();
	private static final JFileChooser SAVE_DIR_CHOOSER = fileChooser();

	public static JFileChooser fileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		return fc;
	}

	public static void save(JFrame frame, FileChooserAction fileAction) {
		if (SAVE_CHOOSER.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				fileAction.fileAction(SAVE_CHOOSER.getSelectedFile());
			} catch (Exception ex) {
				GuiUtils.handleError(frame, ex);
			}
		}
	}

	public static void saveDir(JFrame frame, FileChooserAction fileAction) {
		SAVE_DIR_CHOOSER.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (SAVE_DIR_CHOOSER.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				fileAction.fileAction(SAVE_DIR_CHOOSER.getSelectedFile());
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
		LOAD_CHOOSER.setFileSelectionMode(mode);

		if (LOAD_CHOOSER.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				fileAction.fileAction(LOAD_CHOOSER.getSelectedFile());
			} catch (Exception ex) {
				GuiUtils.handleError(frame, ex);
			}
		}
	}

}
