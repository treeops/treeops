package org.treeops.ui.transform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.xml.ws.Holder;

import org.treeops.SchemaNode;
import org.treeops.transform.Transformation;
import org.treeops.types.customization.JoinMutuallyExclusiveCustomization;
import org.treeops.ui.util.GuiUtils;
import org.treeops.utils.Utils;

public abstract class JoinMutuallyExlusiveDialog extends JDialog {

	private JTextField nameTextField = new JTextField();
	private List<JCheckBox> checkboxes = new ArrayList<>();
	private final List<String> list;

	public JoinMutuallyExlusiveDialog(Frame owner, List<String> list) {
		super(owner, true);
		this.list = list;

		list.stream().forEach(name -> checkboxes.add(new JCheckBox(name)));

		setTitle("Join Mutually Exclusive");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> okPressed());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> close());

		JPanel controlsPanel = new JPanel(new GridBagLayout());
		GuiUtils.addLabelTextRows(new JLabel[]{new JLabel("Name")}, new JTextField[]{nameTextField}, controlsPanel);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		for (int i = 0; i < checkboxes.size(); i++) {

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			controlsPanel.add(checkboxes.get(i), c);
		}

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

		List<String> selected = selectedNames();

		selected(selected, nameTextField.getText());
		close();
	}

	private List<String> selectedNames() {
		List<String> selected = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i);
			if (checkboxes.get(i).isSelected()) {
				selected.add(name);
			}
		}
		return selected;
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	public abstract void selected(List<String> selectedChildren, String name);

	public static Transformation showDialog(SchemaNode n, JFrame frame) {

		List<String> list = n.getChildren().stream().map(c -> c.getName()).collect(Collectors.toList());

		Holder<JoinMutuallyExclusiveCustomization> holder = new Holder<>();
		JoinMutuallyExlusiveDialog d = new JoinMutuallyExlusiveDialog(frame, list) {
			@Override
			public void selected(List<String> selected, String name) {
				holder.value = new JoinMutuallyExclusiveCustomization(n.getPath(), name, selected);
			}
		};
		d.setLocationRelativeTo(frame);

		d.setVisible(true);
		return holder.value;
	}

	public static void main(String[] args) {

		final JoinMutuallyExlusiveDialog dialog = new JoinMutuallyExlusiveDialog(null, Utils.list("a", "b", "c")) {

			@Override
			public void selected(List<String> selectedChildren, String name) {
			}

		};
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
	}

}
