package org.treeops.ui.transform;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
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
import org.treeops.transform.RegExpTransformation;
import org.treeops.transform.Transformation;

public abstract class RegExpDialog extends JDialog {
	private JTextField expressionTextField = new JTextField();
	private JTextField replacementTextField = new JTextField();

	public RegExpDialog(Frame owner) {
		super(owner, true);
		setTitle("Regular Expression");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> okPressed());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> close());

		JPanel controlsPanel = new JPanel(new GridBagLayout());

		addLabelTextRows(new JLabel[]{new JLabel("Regular Expression"), new JLabel("Replacement")}, new JTextField[]{expressionTextField, replacementTextField}, controlsPanel);

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
		selected(expressionTextField.getText(), replacementTextField.getText());
		close();
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	public abstract void selected(String regex, String replacement);

	public static Transformation showDialog(SchemaNode n, JFrame frame) {
		Holder<RegExpTransformation> holder = new Holder<>();
		RegExpDialog d = new RegExpDialog(frame) {
			@Override
			public void selected(String regex, String replacement) {
				holder.value = new RegExpTransformation(n.getPath(), regex, replacement);
			}
		};
		d.setLocationRelativeTo(frame);

		d.setVisible(true);
		return holder.value;
	}

	public static void main(String[] args) {

		final RegExpDialog dialog = new RegExpDialog(null) {

			@Override
			public void selected(String regex, String replacement) {

			}
		};
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
	}

	private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(textFields[i], c);
		}
	}

}
