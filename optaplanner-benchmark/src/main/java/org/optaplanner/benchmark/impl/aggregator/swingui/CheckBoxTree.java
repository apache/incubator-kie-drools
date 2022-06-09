package org.optaplanner.benchmark.impl.aggregator.swingui;

import static org.optaplanner.benchmark.impl.aggregator.swingui.MixedCheckBox.MixedCheckBoxStatus.CHECKED;
import static org.optaplanner.benchmark.impl.aggregator.swingui.MixedCheckBox.MixedCheckBoxStatus.MIXED;
import static org.optaplanner.benchmark.impl.aggregator.swingui.MixedCheckBox.MixedCheckBoxStatus.UNCHECKED;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;

public class CheckBoxTree extends JTree {

    private static final Color TREE_SELECTION_COLOR = UIManager.getColor("Tree.selectionBackground");

    private Set<DefaultMutableTreeNode> selectedSingleBenchmarkNodes = new HashSet<>();

    public CheckBoxTree(DefaultMutableTreeNode root) {
        super(root);
        addMouseListener(new CheckBoxTreeMouseListener(this));
        setCellRenderer(new CheckBoxTreeCellRenderer());
        setToggleClickCount(0);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public Set<DefaultMutableTreeNode> getSelectedSingleBenchmarkNodes() {
        return selectedSingleBenchmarkNodes;
    }

    public void setSelectedSingleBenchmarkNodes(Set<DefaultMutableTreeNode> selectedSingleBenchmarkNodes) {
        this.selectedSingleBenchmarkNodes = selectedSingleBenchmarkNodes;
    }

    public void expandNodes() {
        expandSubtree(null, true);
    }

    public void collapseNodes() {
        expandSubtree(null, false);
    }

    private void expandSubtree(TreePath path, boolean expand) {
        if (path == null) {
            TreePath selectionPath = getSelectionPath();
            path = selectionPath == null ? new TreePath(treeModel.getRoot()) : selectionPath;
        }
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Enumeration children = currentNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            TreePath expandedPath = path.pathByAddingChild(child);
            expandSubtree(expandedPath, expand);
        }
        if (expand) {
            expandPath(path);
        } else if (path.getParentPath() != null) {
            collapsePath(path);
        }
    }

    public void updateHierarchyCheckBoxStates() {
        for (DefaultMutableTreeNode currentNode : selectedSingleBenchmarkNodes) {
            resolveNewCheckBoxState(currentNode, CHECKED, MIXED);
        }
        treeDidChange();
    }

    private void resolveNewCheckBoxState(DefaultMutableTreeNode currentNode, MixedCheckBox.MixedCheckBoxStatus newStatus,
            MixedCheckBox.MixedCheckBoxStatus mixedStatus) {
        MixedCheckBox checkBox = (MixedCheckBox) currentNode.getUserObject();
        checkBox.setStatus(newStatus);
        selectChildren(currentNode, newStatus);
        TreeNode[] ancestorNodes = currentNode.getPath();
        // examine ancestors, don't lose track of most recent changes - bottom-up approach
        for (int i = ancestorNodes.length - 2; i >= 0; i--) {
            DefaultMutableTreeNode ancestorNode = (DefaultMutableTreeNode) ancestorNodes[i];
            MixedCheckBox ancestorCheckbox = (MixedCheckBox) ancestorNode.getUserObject();
            if (checkChildren(ancestorNode, newStatus)) {
                ancestorCheckbox.setStatus(newStatus);
            } else {
                if (mixedStatus == null) {
                    break;
                }
                ancestorCheckbox.setStatus(mixedStatus);
            }
        }
    }

    private void selectChildren(DefaultMutableTreeNode parent, MixedCheckBox.MixedCheckBoxStatus status) {
        MixedCheckBox box = (MixedCheckBox) parent.getUserObject();
        if (box.getBenchmarkResult() instanceof SingleBenchmarkResult) {
            if (status == CHECKED) {
                selectedSingleBenchmarkNodes.add(parent);
            } else if (status == UNCHECKED) {
                selectedSingleBenchmarkNodes.remove(parent);
            }
        }
        Enumeration children = parent.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            MixedCheckBox childCheckBox = (MixedCheckBox) child.getUserObject();
            childCheckBox.setStatus(status);
            selectChildren(child, status);
        }
    }

    private boolean checkChildren(DefaultMutableTreeNode parent, MixedCheckBox.MixedCheckBoxStatus status) {
        boolean childrenCheck = true;
        Enumeration children = parent.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            MixedCheckBox checkBox = (MixedCheckBox) child.getUserObject();
            if (checkBox.getStatus() != status) {
                childrenCheck = false;
                break;
            }
        }
        return childrenCheck;
    }

    private class CheckBoxTreeMouseListener extends MouseAdapter {

        private CheckBoxTree tree;
        private double unlabeledMixedCheckBoxWidth;

        public CheckBoxTreeMouseListener(CheckBoxTree tree) {
            this.tree = tree;
            unlabeledMixedCheckBoxWidth = new MixedCheckBox().getPreferredSize().getWidth();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                MixedCheckBox checkBox = (MixedCheckBox) currentNode.getUserObject();
                // ignore clicks on checkbox's label - enables to select it without changing the state
                if (e.getX() - tree.getPathBounds(path).getX() > unlabeledMixedCheckBoxWidth) {
                    return;
                }
                switch (checkBox.getStatus()) {
                    case CHECKED:
                        resolveNewCheckBoxState(currentNode, UNCHECKED, MIXED);
                        break;
                    case UNCHECKED:
                        resolveNewCheckBoxState(currentNode, CHECKED, MIXED);
                        break;
                    case MIXED:
                        resolveNewCheckBoxState(currentNode, CHECKED, null);
                        break;
                    default:
                        throw new IllegalStateException("The status (" + checkBox.getStatus() + ") is not implemented.");
                }
                tree.treeDidChange();
            }
        }

    }

    private static class CheckBoxTreeCellRenderer implements TreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            MixedCheckBox checkBox = (MixedCheckBox) node.getUserObject();
            checkBox.setBackground(selected ? TREE_SELECTION_COLOR : Color.WHITE);
            return checkBox;
        }

    }
}
