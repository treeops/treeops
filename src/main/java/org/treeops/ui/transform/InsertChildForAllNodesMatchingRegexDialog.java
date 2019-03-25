package org.treeops.ui.transform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.xml.ws.Holder;

import org.treeops.SchemaNode;
import org.treeops.transform.InsertChildForAllMatchingRegexTransformation;
import org.treeops.transform.Transformation;
import org.treeops.ui.util.GuiUtils;

public abstract class InsertChildForAllNodesMatchingRegexDialog extends JDialog {
	private JTextField expressionTextField = new JTextField();
	private JTextField valueNodeNameTextField = new JTextField();

	public InsertChildForAllNodesMatchingRegexDialog(Frame owner) {
		super(owner, true);
		setTitle("Regular Expression");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> okPressed());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> close());

		JPanel controlsPanel = new JPanel(new GridBagLayout());

		GuiUtils.addLabelTextRows(new JLabel[]{new JLabel("Regular expression"), new JLabel("New value node name")}, new JTextField[]{expressionTextField, valueNodeNameTextField}, controlsPanel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(controlsPanel, BorderLayout.CENTER);
		panel.add(buttonsPanel, BorderLayout.SOUTH);
		setContentPane(panel);

		getRootPane().registerKeyboardAction(e -> close(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().registerKeyboardAction(e -> okPressed(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		pack();
		setMinimumSize(new Dimension(300, 20));
	}

	private void okPressed() {
		selected(expressionTextField.getText(), valueNodeNameTextField.getText());
		close();
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	public abstract void selected(String regex, String valueNodeName);

	public static Transformation showDialog(SchemaNode n, JFrame frame) {
		Holder<InsertChildForAllMatchingRegexTransformation> holder = new Holder<>();
		InsertChildForAllNodesMatchingRegexDialog d = new InsertChildForAllNodesMatchingRegexDialog(frame) {
			@Override
			public void selected(String regex, String valueNodeName) {
				holder.value = new InsertChildForAllMatchingRegexTransformation(n.getPath(), regex, valueNodeName);
			}
		};
		d.setLocationRelativeTo(frame);

		d.setVisible(true);
		return holder.value;
	}

	public static void main(String[] args) {

		final InsertChildForAllNodesMatchingRegexDialog dialog = new InsertChildForAllNodesMatchingRegexDialog(null) {

			@Override
			public void selected(String regex, String replacement) {
				//ignore event
			}
		};
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
	}

}
