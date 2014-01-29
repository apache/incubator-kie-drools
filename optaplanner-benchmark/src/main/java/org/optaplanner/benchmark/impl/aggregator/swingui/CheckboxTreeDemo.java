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

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * This class serves just as a demonstration of usage of CheckboxTree class.
 * 
 */
public class CheckboxTreeDemo {
    
    public static void main(String[] args) {
        File rootFile = null;
        if (args.length == 1) {
            rootFile = new File(args[0]);
        } else {
            throw new IllegalArgumentException("Root file/directory not supplied.");
        }
        DefaultMutableTreeNode rootNode = scan(rootFile);
        CheckboxTree tree = new CheckboxTree(rootNode);
        
        JFrame frame = new JFrame("CheckboxTreeDemo");
        JScrollPane scrollPane = new JScrollPane(tree);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
        
    }
    
    private static DefaultMutableTreeNode scan(File file) {
        CustomCheckbox checkbox = new CustomCheckbox(file.getName());
        DefaultMutableTreeNode treeNde = new DefaultMutableTreeNode(checkbox);
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                MutableTreeNode nod = scan(child);
                if (nod != null) {
                    treeNde.add(nod);
                }
            }
        }
        return treeNde;
    }
}
