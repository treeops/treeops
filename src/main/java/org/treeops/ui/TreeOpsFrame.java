package org.treeops.ui;

import static org.treeops.ui.util.FileChooserAction.save;
import static org.treeops.ui.util.GuiUtils.menuItem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;
import org.treeops.codegen.CodeGenerator;
import org.treeops.formats.FileInput;
import org.treeops.formats.Format;
import org.treeops.formats.Input;
import org.treeops.formats.InputParser;
import org.treeops.formats.TextInput;
import org.treeops.transform.Transformation;
import org.treeops.types.TypeExtractor;
import org.treeops.types.customization.Customization;
import org.treeops.ui.transform.Actions;
import org.treeops.ui.transform.TransformationAction;
import org.treeops.ui.util.FileChooserAction;
import org.treeops.ui.util.GuiUtils;
import org.treeops.utils.StopWatch;
import org.treeops.utils.XStreamUtils;

public class TreeOpsFrame extends JFrame {
	private static final Logger LOG = LoggerFactory.getLogger(TreeOpsFrame.class);

	private static final int MAX_NODES_SYNCHRONIZED = 20000;

	private Input input;
	private DataNode dataRoot;
	private SchemaNode schemaRoot;
	private List<Transformation> transformations = new ArrayList<>();
	private List<TransformationAction> xformActions = Actions.transformations(this);
	private List<TransformationAction> customizationActions = Actions.customizations(this);

	private JLabel statusLabel = new JLabel("");
	private JTabbedPane tabbedPane = new JTabbedPane();
	private SchemaPanel schemaPanel = new SchemaPanel(xformActions, customizationActions);
	private TransformationsPanel transformationPanel = new TransformationsPanel(row -> deleteTransformation(row));
	private TypesPanel typesPanel = new TypesPanel(path -> showPath(path));
	private OutputPanel outputPanel = new OutputPanel(() -> dataRoot, () -> transformations);
	private InputPanel inputPanel = new InputPanel(input -> inputReceived(input));

	public TreeOpsFrame() {
		super("TreeOps");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		schemaPanel.setTransformationAddedListener(t -> {
			transformations.add(t);
			transformationPanel.getTableModel().setList(transformations);
			reload();
		});

		schemaPanel.setShowTypeListener(path -> {
			typesPanel.selectType(path);
			tabbedPane.setSelectedComponent(typesPanel);
		});

		getContentPane().setLayout(new BorderLayout());

		JSplitPane inputOutputPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, outputPanel);

		inputOutputPanel.setOneTouchExpandable(true);

		tabbedPane.add("Input/Output", inputOutputPanel);
		tabbedPane.add("Schema", schemaPanel);
		tabbedPane.add("Transformations", transformationPanel);
		tabbedPane.add("Types", typesPanel);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		initMenu();
		setSize(1000, 800);
		inputOutputPanel.setDividerLocation(getWidth() / 2);
		setLocationRelativeTo(null);
		initStatusBar();
	}

	private void deleteTransformation(int selectedRow) {
		SwingUtilities.invokeLater(() -> {
			transformations.remove(selectedRow);
			transformationPanel.getTableModel().setList(transformations);
			reload();
		});
	}

	private void showPath(String path) {
		schemaPanel.resetFindSchemaNode();
		schemaPanel.find(path);
		selectSchemaPanel();
	}

	private void selectSchemaPanel() {
		tabbedPane.setSelectedComponent(schemaPanel);
	}

	private void initStatusBar() {
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		for (Format format : Format.values()) {
			menuItem(fileMenu, "Load " + format.name() + "...", () -> FileChooserAction.loadFileOrDir(this, f -> parseAndShow(new FileInput(format, f))));
		}

		fileMenu.addSeparator();
		for (Format format : Format.values()) {
			menuItem(fileMenu, "Export " + format.name() + "...", () -> save(this, file -> format.getProcessor().write(dataRoot, file, transformations)));
		}
		fileMenu.addSeparator();

		menuItem(fileMenu, "Generate Java...", () -> generateJava());
		menuItem(fileMenu, "Export schema...", () -> save(this, file -> XStreamUtils.toFile(schemaRoot, file)));
		menuItem(fileMenu, "Load transformations...", () -> FileChooserAction.loadFile(this, f -> {
			transformations = XStreamUtils.readFile(f);
			reload();
		}));
		menuItem(fileMenu, "Export transformations...", () -> save(this, file -> XStreamUtils.toFile(transformations, file)));

		JMenu searchMenu = new JMenu("Search");
		menuBar.add(searchMenu);

		JMenuItem findMenu = menuItem(searchMenu, "Find schema node...", () -> findSchemaNode());
		findMenu.setAccelerator(KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		findMenu.setMnemonic(KeyEvent.VK_F);

		JMenu transformationsMenu = new JMenu("Transformations");
		menuBar.add(transformationsMenu);
		for (TransformationAction a : xformActions) {
			menuItem(transformationsMenu, a.getName(), () -> {
				selectSchemaPanel();
				schemaPanel.selectRootIfNothingSelected();
				schemaPanel.addTransformation(a);
			});
		}
	}

	private void generateJava() {
		String packageName = JOptionPane.showInputDialog(this, "Package name", "org.generated");
		if (packageName != null) {
			FileChooserAction.saveDir(this, dir -> CodeGenerator.generate(dir, packageName, schemaRoot, typesPanel.getTableModel().getList(), transformations));
		}
	}

	private void findSchemaNode() {
		selectSchemaPanel();
		schemaPanel.findSchemaNode();
	}

	private void reload() {
		parseAndShow(input);
	}

	private void parseAndShow(Input newInput) {
		try {
			StopWatch sw = new StopWatch();
			LOG.info("parsing " + newInput + "...");
			DataNode newRoot = InputParser.parse(newInput);
			this.input = newInput;
			show(newRoot);
			LOG.info("completed parsing " + newInput + " in " + sw.elapsedTime() + " sec");
		} catch (Exception ex) {
			GuiUtils.handleError(this, ex);
		}
	}

	private void show(DataNode newRoot) {
		newRoot = Transformation.runAll(transformations, newRoot);
		SchemaNode schemaRoot = SchemaExtractor.schema(newRoot);

		this.schemaRoot = schemaRoot;
		this.dataRoot = newRoot;
		this.schemaPanel.getTableModel().setList(schemaRoot.flatten(null));

		int numDataNodes = dataRoot.getNumDescendants();

		List<org.treeops.types.Type> types = extractTypes(schemaRoot);
		statusLabel.setText("Input " + input.getFormat() + " " + input.description() + ", " + numDataNodes + " nodes, " + schemaRoot.getNumDescendants() + " schema nodes, " + types.size() + " types");

		outputPanel.setSynchronized(numDataNodes < MAX_NODES_SYNCHRONIZED);
		outputPanel.needRefresh();

		typesPanel.getTableModel().setList(types);
	}

	private List<org.treeops.types.Type> extractTypes(SchemaNode schemaRoot) {
		List<org.treeops.types.Type> types = TypeExtractor.extract(schemaRoot);
		types = Customization.process(schemaRoot, types, Customization.list(transformations));
		Collections.sort(types, (t1, t2) -> t1.getName().toUpperCase().compareTo(t2.getName().toUpperCase()));
		return types;
	}

	private String inputReceived(TextInput input) {
		parseAndShow(new TextInput(input.getFormat(), input.getText()));
		outputPanel.refresh();
		return "";
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			GuiUtils.setSystemLookAndFeel();
			TreeOpsFrame f = new TreeOpsFrame();
			f.setLocationRelativeTo(null);
			f.setVisible(true);
			f.parseAndShow(new FileInput(Format.XML, new File("src/test/resources/books.xml")));
		});

	}

}
