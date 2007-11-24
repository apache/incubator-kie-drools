package org.drools.ruleflow.common.instance.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.drools.ruleflow.common.instance.WorkItem;
import org.drools.ruleflow.common.instance.WorkItemHandler;
import org.drools.ruleflow.common.instance.WorkItemManager;

public class UIWorkItemHandler extends JFrame implements WorkItemHandler {

    private Map taskInstances = new HashMap();
    private JList taskInstancesList;
    private JButton selectButton;
    
    public UIWorkItemHandler() {
        setSize(new Dimension(400, 300));
        setTitle("Work Items");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initializeComponent();
    }
    
    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        taskInstancesList = new JList();
        taskInstancesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskInstancesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    select();
                }
            }
        });
        taskInstancesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selectButton.setEnabled(getSelectedTaskInstance() != null);
            }
        });
        reloadTaskInstancesList();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(taskInstancesList, c);
        
        selectButton = new JButton("Select");
        selectButton.setEnabled(false);
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                select();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(selectButton, c);
    }
    
    private void select() {
        WorkItem taskInstance = getSelectedTaskInstance();
        if (taskInstance != null) {
            UIWorkItemHandlerDialog dialog = new UIWorkItemHandlerDialog(UIWorkItemHandler.this, taskInstance);
            dialog.setVisible(true);
        }
    }
    
    public WorkItem getSelectedTaskInstance() {
        int index = taskInstancesList.getSelectedIndex();
        if (index != -1) {
            Object selected = taskInstancesList.getModel().getElementAt(index);
            if (selected instanceof TaskInstanceWrapper) {
                return ((TaskInstanceWrapper) selected).getTaskInstance();
            }
        }
        return null;
    }
    
    private void reloadTaskInstancesList() {
        List result = new ArrayList();
        for (Iterator iterator = taskInstances.keySet().iterator(); iterator.hasNext(); ) {
            WorkItem taskInstance = (WorkItem) iterator.next();
            result.add(new TaskInstanceWrapper(taskInstance));
        }
        taskInstancesList.setListData(result.toArray());
    }
    
    public void complete(WorkItem taskInstance, Map results) {
        WorkItemManager manager = (WorkItemManager) taskInstances.get(taskInstance);
        if (manager != null) {
            manager.completeWorkItem(taskInstance.getId(), results);
            taskInstances.remove(taskInstance);
            reloadTaskInstancesList();
        }
        selectButton.setEnabled(getSelectedTaskInstance() != null);
    }
    
    public void abort(WorkItem taskInstance) {
        WorkItemManager manager = (WorkItemManager) taskInstances.get(taskInstance);
        if (manager != null) {
            manager.abortWorkItem(taskInstance.getId());
            taskInstances.remove(taskInstance);
            reloadTaskInstancesList();
        }
        selectButton.setEnabled(getSelectedTaskInstance() != null);
    }
    
    public void abortWorkItem(WorkItem taskInstance,
            WorkItemManager manager) {
        taskInstances.remove(taskInstance);
        reloadTaskInstancesList();
    }

    public void executeWorkItem(WorkItem taskInstance,
            WorkItemManager manager) {
        taskInstances.put(taskInstance, manager);
        reloadTaskInstancesList();
    }

    private class TaskInstanceWrapper {
        
        private WorkItem taskInstance;
        
        public TaskInstanceWrapper(WorkItem taskInstance) {
            this.taskInstance = taskInstance;
        }
        
        public WorkItem getTaskInstance() {
            return taskInstance;
        }
        
        public String toString() {
            return taskInstance.getName() + " [" + taskInstance.getId() + "]";
        }
    }
    
}
