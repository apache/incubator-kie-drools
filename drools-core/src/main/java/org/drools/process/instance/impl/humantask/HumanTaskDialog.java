/**
 * Copyright 2010 JBoss Inc
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

package org.drools.process.instance.impl.humantask;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.drools.runtime.process.WorkItem;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class HumanTaskDialog extends JDialog {
    
    private static final long serialVersionUID = 400L;
    
    private HumanTaskHandler handler;
    private WorkItem workItem;
    private JTextField resultNameField;
    private JTextField resultValueField;
    private List<Result> results = new ArrayList<Result>();
    private JList resultList;
    private JButton removeResultButton;
    private JButton completeButton;
    private JButton abortButton;
    
    public HumanTaskDialog(HumanTaskHandler handler, WorkItem workItem) {
        super(handler, "Execute Human Task", true);
        this.handler = handler;
        this.workItem = workItem;
        setSize(new Dimension(400, 400));
        initializeComponent();
    }
    
    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        // Parameters
        JLabel nameLabel = new JLabel("Name");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(nameLabel, c);
        String taskName = (String) workItem.getParameter("TaskName");
        JTextField nameField = new JTextField(
            taskName == null ? "" : taskName);
        nameField.setEditable(false);
        c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(nameField, c);
        
        JLabel priorityLabel = new JLabel("Priority");
        c = new GridBagConstraints();
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(priorityLabel, c);
        String priority = (String) workItem.getParameter("Priority");
        JTextField priorityField = new JTextField(
            priority == null ? "" : priority);
        priorityField.setEditable(false);
        c = new GridBagConstraints();
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(priorityField, c);
        
        JLabel commentLabel = new JLabel("Comment");
        c = new GridBagConstraints();
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(commentLabel, c);
        String comment = (String) workItem.getParameter("Comment");
        JTextArea params = new JTextArea(
            comment == null ? "" : comment);
        params.setEditable(false);
        c = new GridBagConstraints();
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(params, c);
        
        int additionalParameters = 0;
        for (Map.Entry<String, Object> entry: workItem.getParameters().entrySet()) {
            String name = entry.getKey();
            if (!"TaskName".equals(name)
                    && !"Priority".equals(name)
                    && !"Comment".equals(name)
                    && !"ActorId".equals(name)) {
                additionalParameters++;
                JLabel label = new JLabel(name);
                c = new GridBagConstraints();
                c.gridy = 2 + additionalParameters;
                c.anchor = GridBagConstraints.WEST;
                c.insets = new Insets(5, 5, 5, 5);
                panel.add(label, c);
                JTextField field = new JTextField(
                    workItem.getParameter(name).toString());
                field.setEditable(false);
                c = new GridBagConstraints();
                c.gridy = 2 + additionalParameters;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = new Insets(5, 5, 5, 5);
                panel.add(field, c);
            }
        }
        
        // Result Panel
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridBagLayout());
        resultPanel.setBorder(new TitledBorder("Results"));
        
        JLabel resultNameLabel = new JLabel("Name");
        c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        resultPanel.add(resultNameLabel, c);
        resultNameField = new JTextField();
        c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        resultPanel.add(resultNameField, c);
        JLabel resultValueLabel = new JLabel("Value");
        c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        resultPanel.add(resultValueLabel, c);
        resultValueField = new JTextField();
        c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        resultPanel.add(resultValueField, c);
        JButton addResultButton = new JButton("Add");
        addResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addResult();
            }
        });
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        resultPanel.add(addResultButton, c);
        
        resultList = new JList();
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                removeResultButton.setEnabled(resultList.getSelectedIndex() != -1);
            }
        });
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(resultList);
        resultPanel.add(scrollPane, c);
        removeResultButton = new JButton("Remove");
        removeResultButton.setEnabled(false);
        removeResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeResult();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(5, 5, 5, 5);
        resultPanel.add(removeResultButton, c);
        
        c = new GridBagConstraints();
        c.gridy = 3 + additionalParameters;
        c.gridwidth = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(resultPanel, c);
        
        
        // Buttom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        completeButton = new JButton("Complete");
        completeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                complete();
            }
        });
        c = new GridBagConstraints();
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        bottomPanel.add(completeButton, c);

        abortButton = new JButton("Abort");
        abortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                abort();
            }
        });
        c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        bottomPanel.add(abortButton, c);
        
        c = new GridBagConstraints();
        c.gridy = 4 + additionalParameters;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(bottomPanel, c);
    }
    
    private void addResult() {
        String name = resultNameField.getText();
        String value = resultValueField.getText();
        if ("".equals(name) || "".equals(value)) {
            JOptionPane.showMessageDialog(this,
                "Name or value of result may not be null!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Result result = new Result(name, value);
        if (results.contains(result)) {
            JOptionPane.showMessageDialog(this,
                "Cannot add result more than once!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        results.add(result);
        reloadResultList();
        resultNameField.setText("");
        resultValueField.setText("");
    }
    
    private void reloadResultList() {
        resultList.setListData(results.toArray());
    }
    
    private void removeResult() {
        int index = resultList.getSelectedIndex();
        if (index != -1) {
            results.remove(index);
            reloadResultList();
        }
    }
    
    private void complete() {
        Map<String, Object> resultMap = null;
        if (results.size() > 0) {
            resultMap = new HashMap<String, Object>();
            for (Result result: results) {
                resultMap.put(result.getName(), result.getValue());
            }
        }
        handler.complete(workItem, resultMap);
        dispose();
    }
    
    private void abort() {
        handler.abort(workItem);
        dispose();
    }
    
    public static class Result {
        private String name;
        private Object value;
        public Result(String name, Object value) {
            this.name = name;
            this.value = value;
        }
        public String getName() {
            return this.name;
        }
        public Object getValue() {
            return this.value;
        }
        public String toString() {
            return this.name + " = " + this.value;
        }
        public boolean equals(Object o) {
            if (o instanceof Result) {
                return ((Result) o).getName().equals(this.name);
            }
            return false;
        }
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
