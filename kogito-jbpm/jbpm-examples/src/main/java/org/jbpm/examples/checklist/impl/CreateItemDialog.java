/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.examples.checklist.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class CreateItemDialog extends JDialog {

    private static final long serialVersionUID = 510l;
    
    private String name;
    private String actors;
    private String groups;
    
    private JTextField nameTextField;
    private JTextField actorsTextField;
    private JTextField groupsTextField;
    
    public CreateItemDialog(JFrame owner, String actorId) {
    	super(owner);
        setSize(new Dimension(300, 160));
        setTitle("Create New Item");
        setModal(true);
        initializeComponent(actorId);
    }
    
    private void initializeComponent(String actorId) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        JLabel nameLabel = new JLabel("Name:");
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, c);
        
        nameTextField = new JTextField("Add name here ...");
        nameTextField.setPreferredSize(new Dimension(80, 20));
        nameTextField.setSize(new Dimension(80, 20));
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(nameTextField, c);
        
        JLabel actorsLabel = new JLabel("Actor(s):");
        c = new GridBagConstraints();
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(actorsLabel, c);
        
        actorsTextField = new JTextField(actorId);
        actorsTextField.setPreferredSize(new Dimension(80, 20));
        actorsTextField.setSize(new Dimension(80, 20));
        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(actorsTextField, c);
        
        JLabel groupsLabel = new JLabel("Group(s):");
        c = new GridBagConstraints();
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(groupsLabel, c);
        
        groupsTextField = new JTextField();
        groupsTextField.setPreferredSize(new Dimension(80, 20));
        groupsTextField.setSize(new Dimension(80, 20));
        c = new GridBagConstraints();
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(groupsTextField, c);
        
        JButton createItemButton = new JButton("OK");
        createItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                name = nameTextField.getText();
                actors = actorsTextField.getText();
                groups = groupsTextField.getText();
                setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridy = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(createItemButton, c);

        panel.doLayout();
    }
    
    public String getItemName() {
    	return name;
    }
    
    public String getActors() {
    	return actors;
    }
    
    public String getGroups() {
    	return groups;
    }    
}