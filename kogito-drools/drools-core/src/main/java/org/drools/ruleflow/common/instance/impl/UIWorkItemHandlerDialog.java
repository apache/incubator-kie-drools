package org.drools.ruleflow.common.instance.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.drools.ruleflow.common.instance.WorkItem;

public class UIWorkItemHandlerDialog extends JDialog {
    
    private UIWorkItemHandler handler;
    private WorkItem taskInstance;
    private JButton completeButton;
    private JButton abortButton;
    
    public UIWorkItemHandlerDialog(UIWorkItemHandler handler, WorkItem taskInstance) {
        super(handler, "Execute Work Item", true);
        this.handler = handler;
        this.taskInstance = taskInstance;
        setSize(new Dimension(400, 300));
        initializeComponent();
    }

    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        JTextArea params = new JTextArea();
        params.setText(getParameters());
        params.setEditable(false);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(params, c);
        
        completeButton = new JButton("Complete");
        completeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                complete();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(completeButton, c);

        abortButton = new JButton("Abort");
        abortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                abort();
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(abortButton, c);
    }
    
    private String getParameters() {
        String result = "";
        if (taskInstance.getParameters() != null) {
            for (Iterator iterator = taskInstance.getParameters().entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                result += entry.getKey() + " = " + entry.getValue() + "\n";
            }
        }
        return result;
    }
    
    private void complete() {
        handler.complete(taskInstance, null);
        dispose();
    }
    
    private void abort() {
        handler.abort(taskInstance);
        dispose();
    }
}
