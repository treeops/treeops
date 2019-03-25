package org.treeops.ui.transform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.xml.ws.Holder;

import org.treeops.SchemaNode;
import org.treeops.transform.SortKey;
import org.treeops.transform.SortTransformation;
import org.treeops.transform.Transformation;
import org.treeops.utils.Utils;

public abstract class SortDialog extends JDialog {

	private List<SortKey> sortKeys = new ArrayList<>();
	private JList<String> childrenCombo = new JList<>();
	private JList<String> keysCombo = new JList<>();
	private final SchemaNode n;
	private final List<SchemaNode> childNodes;

	public SortDialog(Frame owner, SchemaNode n) {
		super(owner, true);
		this.n = n;
		childNodes = n.flatten(false);

		setTitle("Sort");

		JButton ascendingButton = new JButton("Ascending");
		ascendingButton.addActionListener(e -> sort(true));
		JButton descendingButton = new JButton("Descending");
		descendingButton.addActionListener(e -> sort(false));

		JPanel addSortKeyButtonsPanel = new JPanel();
		addSortKeyButtonsPanel.add(ascendingButton);
		addSortKeyButtonsPanel.add(descendingButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> okPressed());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> close());

		JPanel selectPanel = new JPanel();
		selectPanel.setBorder(new TitledBorder("Children"));
		selectPanel.setLayout(new BorderLayout());
		selectPanel.add(new JScrollPane(childrenCombo), BorderLayout.CENTER);
		selectPanel.add(addSortKeyButtonsPanel, BorderLayout.SOUTH);

		JPanel keysPanel = new JPanel();
		keysPanel.setBorder(new TitledBorder("Keys"));
		keysPanel.setLayout(new BorderLayout());
		keysPanel.add(new JScrollPane(keysCombo), BorderLayout.CENTER);

		JSplitPane controlsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, selectPanel, keysPanel);
		controlsPanel.setDividerLocation(200);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(controlsPanel, BorderLayout.CENTER);
		panel.add(buttonsPanel, BorderLayout.SOUTH);
		setContentPane(panel);

		getRootPane().registerKeyboardAction(e -> close(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().registerKeyboardAction(e -> okPressed(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		childrenCombo.setListData(childNodes.stream().map(c -> n.relativePathText(c)).collect(Collectors.toList()).toArray(new String[]{}));

		pack();
		setMinimumSize(new Dimension(300, 400));
	}

	private void sort(boolean ascending) {
		if (childrenCombo.getSelectedIndex() > -1) {
			SchemaNode child = childNodes.get(childrenCombo.getSelectedIndex());
			sortKeys.add(new SortKey(ascending, n.relativePath(child)));
		}
		refreshSortKeys();
	}

	private void refreshSortKeys() {
		keysCombo.setListData(sortKeys.stream().map(k -> (Utils.path(k.getPath()) + (k.isAscending() ? "" : " desc."))).collect(Collectors.toList()).toArray(new String[]{}));
	}

	private void okPressed() {
		selected(sortKeys);
		close();
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	public abstract void selected(List<SortKey> keys);

	public static Transformation showDialog(SchemaNode n, JFrame frame) {
		Holder<SortTransformation> holder = new Holder<>();
		SortDialog d = new SortDialog(frame, n) {
			@Override
			public void selected(List<SortKey> keys) {
				holder.value = new SortTransformation(n.getPath(), keys);
			}
		};
		d.setLocationRelativeTo(frame);

		d.setVisible(true);
		return holder.value;
	}

	public static void main(String[] args) {
		SchemaNode r = new SchemaNode(null, "x");
		new SchemaNode(r, "a");
		new SchemaNode(r, "b");
		new SchemaNode(r, "c");

		final SortDialog dialog = new SortDialog(null, r) {

			@Override
			public void selected(List<SortKey> keys) {
				//ignore
			}

		};
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
	}

}
