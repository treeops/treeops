package org.treeops.ui.transform;

import static org.treeops.ui.util.GuiUtils.ask;
import static org.treeops.ui.util.GuiUtils.select;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import org.treeops.SchemaNode;
import org.treeops.codegen.CodeGenerator;
import org.treeops.transform.AppendChildTransformation;
import org.treeops.transform.DeleteTransformation;
import org.treeops.transform.FilterTransformation;
import org.treeops.transform.GroupTransformation;
import org.treeops.transform.IndexingTransformation;
import org.treeops.transform.InsertChildTransformation;
import org.treeops.transform.InsertParentTransformation;
import org.treeops.transform.MoveSiblingsDownTransformation;
import org.treeops.transform.MoveToSiblingTransformation;
import org.treeops.transform.MoveUpBecomeParentTransformation;
import org.treeops.transform.MoveUpTransformation;
import org.treeops.transform.RemoveDuplicatesTransformation;
import org.treeops.transform.RenameTransformation;
import org.treeops.transform.ReorderTransformation;
import org.treeops.transform.Transformation;
import org.treeops.transform.ValueHolderTransformation;
import org.treeops.types.customization.ChangeTypeCustomization;
import org.treeops.types.customization.MakeEnumCustomization;
import org.treeops.types.customization.MoveToSuperTypeCustomization;
import org.treeops.types.customization.SetSuperTypeCustomization;
import org.treeops.types.customization.XmlAttributeCustomization;
import org.treeops.utils.Utils;

public class Actions {

	public static List<TransformationAction> transformations(JFrame frame) {
		List<TransformationAction> res = new ArrayList<>();

		add(res, "Delete", n -> new DeleteTransformation(n.getPath()));
		add(res, "Filter", n -> new FilterTransformation(n.getPath()));

		add(res, "Rename", n -> ask(frame, Utils.last(n.getPath()), "Rename to", input -> new RenameTransformation(n.getPath(), input)));
		add(res, "Regular expression", n -> RegExpDialog.showDialog(n, frame));
		add(res, "Indexing", n -> new IndexingTransformation(n.getPath()));
		add(res, "Append child name",
				n -> select(frame, n.listChildPaths(false), "Choose child", "Append child name", input -> new AppendChildTransformation(n.getPath(), org.treeops.utils.Utils.fromPath(input), true)));

		add(res, "Move Up", n -> new MoveUpTransformation(n.getPath()));
		add(res, "Move Up as Parent", n -> new MoveUpBecomeParentTransformation(n.getPath()));
		add(res, "Move Siblings Down", n -> new MoveSiblingsDownTransformation(n.getPath()));
		add(res, "Move To Sibling", n -> select(frame, siblingNodesAndChildren(n), "Choose sibling", "Sibling", input -> moveToSibling(n, input)));

		add(res, "Insert parent", n -> ask(frame, Utils.last(n.getPath()), "Name for new parent", input -> new InsertParentTransformation(n.getPath(), input)));
		add(res, "Insert child ", n -> ask(frame, Utils.last(n.getPath()), "New child name", input -> new InsertChildTransformation(n.getPath(), input)));
		add(res, "Insert child for all nodes matching Regex", n -> InsertChildForAllNodesMatchingRegexDialog.showDialog(n, frame));

		add(res, "Group", n -> select(frame, n.listChildPaths(false), "Choose child", "Group", input -> new GroupTransformation(n.getPath(), org.treeops.utils.Utils.fromPath(input), "Group")));
		add(res, "Remove duplicates", n -> new RemoveDuplicatesTransformation(n.getPath()));

		add(res, "Reorder up", n -> new ReorderTransformation(n.getPath(), true));
		add(res, "Reorder down", n -> new ReorderTransformation(n.getPath(), false));

		add(res, "To Value", n -> new ValueHolderTransformation(n.getPath(), true));
		add(res, "To Structural", n -> new ValueHolderTransformation(n.getPath(), false));
		return res;
	}

	private static void add(List<TransformationAction> res, String name, Function<SchemaNode, Transformation> factory) {
		res.add(new TransformationAction(name, factory));
	}

	private static Transformation moveToSibling(SchemaNode n, String input) {
		List<String> siblingPath = new ArrayList<>(n.getParent().getPath());
		siblingPath.addAll(org.treeops.utils.Utils.fromPath(input));
		return new MoveToSiblingTransformation(n.getPath(), siblingPath);
	}

	private static List<String> siblingNodesAndChildren(SchemaNode n) {
		List<String> res = new ArrayList<>();
		for (SchemaNode sn : SchemaNode.getSiblings(n)) {
			res.addAll(sn.flatten(true).stream().map(c -> String.join("/", n.getParent().relativePath(c))).collect(Collectors.toList()));
		}
		return res;
	}

	public static List<TransformationAction> customizations(JFrame frame) {
		List<TransformationAction> res = new ArrayList<>();
		add(res, "Change type", n -> ask(frame, Utils.last(n.getPath()), "Type", input -> new ChangeTypeCustomization(n.getPath(), input)));
		add(res, "Make enum", n -> ask(frame, CodeGenerator.capitalizeFirst(Utils.last(n.getPath())), "Enum name", input -> new MakeEnumCustomization(n.getPath(), input)));
		add(res, "Change super type", n -> ask(frame, "Super", "Super Type", input -> new SetSuperTypeCustomization(n.getPath(), input)));
		add(res, "Move to super type", n -> new MoveToSuperTypeCustomization(n.getPath()));
		add(res, "Join mutually exclusive", n -> JoinMutuallyExlusiveDialog.showDialog(n, frame));
		add(res, "XML attribute", n -> new XmlAttributeCustomization(n.getPath()));
		return res;
	}

}
