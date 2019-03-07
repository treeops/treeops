package org.treeops.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import org.treeops.types.CompositeType;
import org.treeops.types.EnumType;
import org.treeops.types.Type;
import org.treeops.ui.util.GuiUtils;

public class TypesPanel extends JPanel {
	private static final String EMPTY_PANEL = "EMPTY_PANEL";
	private static final String ENUM_PANEL = "ENUM_PANEL";
	private static final String COMPOSITE_PANEL = "COMPOSITE_PANEL";
	private static final String SIMPLE_PANEL = "SIMPLE_PANEL";

	private TypesTableModel typesTableModel = new TypesTableModel();

	private JTable typesTable = new JTable(typesTableModel);

	private CardLayout detailsCardLayout = new CardLayout();
	private JPanel detailsPanel = new JPanel(detailsCardLayout);

	private EnumPanel enumPanel = new EnumPanel();
	private CompositePanel compositePanel = new CompositePanel();
	private SimplePanel simplePanel = new SimplePanel();
	private JLabel emptyPanel = new JLabel("Please select type");
	private JList<String> pathsList = new JList<>();
	private JSplitPane detailsAndPathsPanel;

	private final Consumer<String> pathSelectedConsumer;
	private class EnumPanel extends JPanel {
		JTextField typeName = new JTextField();

		EnumPanel() {
			super(new GridBagLayout());
			typeName.setEditable(false);
			setBorder(new TitledBorder("Details"));
			GuiUtils.addLabelTextRows(new JLabel[]{new JLabel("Name")}, new JComponent[]{typeName}, this);
		}
	}

	private class CompositePanel extends JPanel {
		JTextField typeName = new JTextField();
		JTextField superTypeName = new JTextField();
		VariablesTableModel variablesTableModel = new VariablesTableModel();
		JTable variablesTable = new JTable(variablesTableModel);

		CompositePanel() {
			super(new BorderLayout());
			typeName.setEditable(false);
			superTypeName.setEditable(false);
			setBorder(new TitledBorder("Details"));
			JPanel controls = new JPanel(new GridBagLayout());
			GuiUtils.addLabelTextRows(new JLabel[]{new JLabel("Name"), new JLabel("Super type")}, new JComponent[]{typeName, superTypeName}, controls);

			add(controls, BorderLayout.NORTH);
			add(new JScrollPane(variablesTable), BorderLayout.CENTER);

			GuiUtils.setWidth(90, variablesTable.getColumnModel().getColumn(2));
			GuiUtils.setWidth(90, variablesTable.getColumnModel().getColumn(3));

			variablesTable.getSelectionModel().addListSelectionListener(x -> variableSelected(x));
		}

	}

	private class SimplePanel extends JPanel {
		JTextField typeName = new JTextField();

		SimplePanel() {
			super(new BorderLayout());
			typeName.setEditable(false);
			setBorder(new TitledBorder("Details"));
			JPanel controls = new JPanel(new GridBagLayout());
			GuiUtils.addLabelTextRows(new JLabel[]{new JLabel("Name")}, new JComponent[]{typeName}, controls);

			add(controls, BorderLayout.NORTH);
		}

	}

	public TypesPanel(Consumer<String> pathSelectedConsumer) {
		super();
		this.pathSelectedConsumer = pathSelectedConsumer;
		this.setLayout(new BorderLayout());

		pathsList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doubleClickedOnPath();
				}

			}
		});
		JPanel pathsPanel = new JPanel(new BorderLayout());
		pathsPanel.setBorder(BorderFactory.createTitledBorder("Paths"));
		pathsPanel.add(new JScrollPane(pathsList), BorderLayout.CENTER);

		JScrollPane typesPane = new JScrollPane(typesTable);

		detailsPanel.add(emptyPanel, EMPTY_PANEL);
		detailsPanel.add(enumPanel, ENUM_PANEL);
		detailsPanel.add(compositePanel, COMPOSITE_PANEL);
		detailsPanel.add(simplePanel, SIMPLE_PANEL);

		detailsAndPathsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, detailsPanel, pathsPanel);
		detailsAndPathsPanel.setOneTouchExpandable(true);
		detailsAndPathsPanel.setDividerLocation(450);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, typesPane, detailsAndPathsPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(450);

		Dimension minimumSize = new Dimension(100, 50);
		typesPane.setMinimumSize(minimumSize);
		detailsPanel.setMinimumSize(minimumSize);

		this.add(splitPane, BorderLayout.CENTER);

		typesTable.getSelectionModel().addListSelectionListener(e -> typeSelected(e));
		selectRowOnClick();

		GuiUtils.setWidth(50, typesTable.getColumnModel().getColumn(2));
		GuiUtils.setWidth(50, typesTable.getColumnModel().getColumn(3));
		selectDetails(EMPTY_PANEL, null);
	}

	public void variableSelected(ListSelectionEvent x) {
		if (x.getValueIsAdjusting()) {
			return;
		}
		int selectedRow = compositePanel.variablesTable.getSelectedRow();
		if (selectedRow > -1) {
			displayPath(compositePanel.variablesTableModel.getList().get(selectedRow).getPaths());
		}
	}

	private void doubleClickedOnPath() {
		if (pathsList.getSelectedValue() != null) {
			pathSelectedConsumer.accept(pathsList.getSelectedValue());
		}
	}

	public void addButton(JPanel buttonsPane, String text, Runnable runnable) {
		JButton b = new JButton(text);
		buttonsPane.add(b);
		b.addActionListener(e -> runnable.run());
	}

	private synchronized void typeSelected(ListSelectionEvent e) {
		int selectedRow = typesTable.getSelectedRow();
		Type type = (selectedRow < 0) ? null : typesTableModel.getList().get(selectedRow);
		if (type != null) {
			if (type instanceof EnumType) {
				setControlsEnum((EnumType) type);
				selectDetails(ENUM_PANEL, type);
			} else if (type instanceof CompositeType) {
				setControlsComposite((CompositeType) type);
				selectDetails(COMPOSITE_PANEL, type);
			} else {
				setControlsSimple(type);
				selectDetails(SIMPLE_PANEL, type);
			}
		} else {
			selectDetails(EMPTY_PANEL, type);
		}
	}

	private void setControlsSimple(Type type) {
		simplePanel.typeName.setText(type.getName());
	}

	private void setControlsEnum(EnumType type) {
		enumPanel.typeName.setText(type.getName());
	}

	private void setControlsComposite(CompositeType type) {
		compositePanel.typeName.setText(type.getName());
		compositePanel.variablesTableModel.setList(type.getVariables());
		if (type.getSuperType() != null) {
			compositePanel.superTypeName.setText(type.getSuperType());
		}
	}

	private void selectDetails(String selected, Type type) {
		typeChanged(type);

		detailsCardLayout.show(detailsPanel, selected);
	}

	private void typeChanged(Type type) {
		if (type == null) {
			pathsList.setListData(new String[]{});
			detailsAndPathsPanel.setDividerLocation(pathHeight());
		} else {
			displayPath(type.getPaths());
		}
	}

	private int pathHeight() {
		return this.getHeight() - 50;
	}

	private void displayPath(List<List<String>> paths) {
		int height = pathHeight();
		pathsList.setListData(paths.stream().map(l -> String.join("/", l)).collect(Collectors.toList()).toArray(new String[]{}));
		detailsAndPathsPanel.setDividerLocation(Math.max(height - (paths.size() * 20), height / 2));
	}

	private void selectRowOnClick() {
		typesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				int currentRow = typesTable.rowAtPoint(event.getPoint());
				if ((currentRow > -1) && (currentRow < typesTableModel.getList().size())) {
					typesTable.setRowSelectionInterval(currentRow, currentRow);
				}
			}
		});
	}

	public TypesTableModel getTableModel() {
		return typesTableModel;
	}

	public JTable getTable() {
		return typesTable;
	}

	public void selectType(List<String> path) {
		Type type = CompositeType.findType(path, typesTableModel.getList());
		if (type != null) {
			int row = typesTableModel.getList().indexOf(type);
			typesTable.getSelectionModel().setSelectionInterval(row, row);
		}
	}

}
