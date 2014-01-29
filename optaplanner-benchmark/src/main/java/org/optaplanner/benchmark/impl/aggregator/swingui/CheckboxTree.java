/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.benchmark.impl.aggregator.swingui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class CheckboxTree extends JTree {

    public CheckboxTree(DefaultMutableTreeNode root) {
        super(root);
        addMouseListener(new CheckboxTreeMouseListener(this));
        setCellRenderer(new CheckboxTreeCellRenderer());
    }

    private static class CheckboxTreeMouseListener extends MouseAdapter {

        CheckboxTree tree;

        CheckboxTreeMouseListener(CheckboxTree tree) {
            this.tree = tree;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                CustomCheckbox checkbox = (CustomCheckbox) currentNode.getUserObject();
                switch (checkbox.getStatus()) {
                    case CHECKED: {
                        checkbox.setStatus(CustomCheckbox.UNCHECKED);
                        selectChildren(currentNode, CustomCheckbox.UNCHECKED);
                        TreeNode[] ancestorNodes = currentNode.getPath();
                        for (int i = ancestorNodes.length - 2; i >= 0; i--) {
                            DefaultMutableTreeNode ancestorNode = (DefaultMutableTreeNode) ancestorNodes[i];
                            CustomCheckbox ancestorCheckbox = (CustomCheckbox) ancestorNode.getUserObject();
                            if (checkChildren(ancestorNode, CustomCheckbox.UNCHECKED)) {
                                ancestorCheckbox.setStatus(CustomCheckbox.UNCHECKED);
                            } else {
                                ancestorCheckbox.setStatus(CustomCheckbox.MIXED);
                            }
                        }
                        break;
                    }
                    case UNCHECKED: {
                        checkbox.setStatus(CustomCheckbox.CHECKED);
                        selectChildren(currentNode, CustomCheckbox.CHECKED);
                        TreeNode[] ancestorNodes = currentNode.getPath();
                        for (int i = ancestorNodes.length - 2; i >= 0; i--) {
                            DefaultMutableTreeNode ancestorNode = (DefaultMutableTreeNode) ancestorNodes[i];
                            CustomCheckbox ancestorCheckbox = (CustomCheckbox) ancestorNode.getUserObject();
                            if (checkChildren(ancestorNode, CustomCheckbox.CHECKED)) {
                                ancestorCheckbox.setStatus(CustomCheckbox.CHECKED);
                            } else {
                                ancestorCheckbox.setStatus(CustomCheckbox.MIXED);
                            }
                        }
                        break;
                    }
                    case MIXED: {
                        checkbox.setStatus(CustomCheckbox.CHECKED);
                        selectChildren(currentNode, CustomCheckbox.CHECKED);
                        TreeNode[] ancestorNodes = currentNode.getPath();
                        for (int i = ancestorNodes.length - 2; i >= 0; i--) {
                            DefaultMutableTreeNode ancestorNode = (DefaultMutableTreeNode) ancestorNodes[i];
                            CustomCheckbox ancestorCheckbox = (CustomCheckbox) ancestorNode.getUserObject();
                            if (checkChildren(ancestorNode, CustomCheckbox.CHECKED)) {
                                ancestorCheckbox.setStatus(CustomCheckbox.CHECKED);
                            } else {
                                break;
                            }
                        }
                    }
                }
                tree.treeDidChange();
            }
        }

        private void selectChildren(DefaultMutableTreeNode parent, CustomCheckbox.CheckboxStatus status) {
            Enumeration children = parent.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
                CustomCheckbox checkbox = (CustomCheckbox) child.getUserObject();
                checkbox.setStatus(status);
                selectChildren(child, status);
            }
        }
        
        private boolean checkChildren(DefaultMutableTreeNode parent, CustomCheckbox.CheckboxStatus status) {
            boolean childrenCheck = true;
            Enumeration children = parent.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
                CustomCheckbox checkbox = (CustomCheckbox) child.getUserObject();
                if (!checkbox.getStatus().equals(status)) {
                    childrenCheck = false;
                    break;
                }
            }
            return childrenCheck;
        }
    }
    
    private static class CheckboxTreeCellRenderer implements TreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            CustomCheckbox checkbox = (CustomCheckbox) node.getUserObject();
            // TODO visual part
            return checkbox;
        }
        
    }
}
