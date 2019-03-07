package org.treeops.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.treeops.formats.Format;
import org.treeops.formats.TextInput;

public class InputPanel extends JPanel {
	private JTextArea textArea = new JTextArea();

	public InputPanel(Function<TextInput, String> loader) {
		super();

		setLayout(new BorderLayout());

		setBorder(BorderFactory.createTitledBorder("Text Input"));

		add(new JScrollPane(textArea), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout());

		JPanel currentInputPanel = new JPanel();
		currentInputPanel.setLayout(new BoxLayout(currentInputPanel, BoxLayout.LINE_AXIS));

		add(buttonPanel, BorderLayout.SOUTH);

		for (Format format : Format.values()) {
			JButton button = new JButton("Load as " + format.name() + " text");
			button.addActionListener(e -> loader.apply(new TextInput(format, textArea.getText())));
			buttonPanel.add(button);
		}
	}

}
