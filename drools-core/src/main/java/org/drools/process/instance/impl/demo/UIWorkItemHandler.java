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

package org.drools.process.instance.impl.demo;

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

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class UIWorkItemHandler extends JFrame implements WorkItemHandler {

    private static final long serialVersionUID = 510l;
    
    private Map<WorkItem, WorkItemManager> workItems = new HashMap<WorkItem, WorkItemManager>();
    private JList workItemsList;
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
        
        workItemsList = new JList();
        workItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workItemsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    select();
                }
            }
        });
        workItemsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selectButton.setEnabled(getSelectedWorkItem() != null);
            }
        });
        reloadWorkItemsList();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(workItemsList, c);
        
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
        WorkItem workItem = getSelectedWorkItem();
        if (workItem != null) {
            UIWorkItemHandlerDialog dialog = new UIWorkItemHandlerDialog(UIWorkItemHandler.this, workItem);
            dialog.setVisible(true);
        }
    }
    
    public WorkItem getSelectedWorkItem() {
        int index = workItemsList.getSelectedIndex();
        if (index != -1) {
            Object selected = workItemsList.getModel().getElementAt(index);
            if (selected instanceof WorkItemWrapper) {
                return ((WorkItemWrapper) selected).getWorkItem();
            }
        }
        return null;
    }
    
    private void reloadWorkItemsList() {
        List<WorkItemWrapper> result = new ArrayList<WorkItemWrapper>();
        for (Iterator<WorkItem> iterator = workItems.keySet().iterator(); iterator.hasNext(); ) {
            WorkItem workItem = iterator.next();
            result.add(new WorkItemWrapper(workItem));
        }
        workItemsList.setListData(result.toArray());
    }
    
    public void complete(WorkItem workItem, Map<String, Object> results) {
        WorkItemManager manager = workItems.get(workItem);
        if (manager != null) {
            manager.completeWorkItem(workItem.getId(), results);
            workItems.remove(workItem);
            reloadWorkItemsList();
        }
        selectButton.setEnabled(getSelectedWorkItem() != null);
    }
    
    public void abort(WorkItem workItem) {
        WorkItemManager manager = workItems.get(workItem);
        if (manager != null) {
            manager.abortWorkItem(workItem.getId());
            workItems.remove(workItem);
            reloadWorkItemsList();
        }
        selectButton.setEnabled(getSelectedWorkItem() != null);
    }
    
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        workItems.remove(workItem);
        reloadWorkItemsList();
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        workItems.put(workItem, manager);
        reloadWorkItemsList();
    }

    private class WorkItemWrapper {
        
        private WorkItem workItem;
        
        public WorkItemWrapper(WorkItem workItem) {
            this.workItem = workItem;
        }
        
        public WorkItem getWorkItem() {
            return workItem;
        }
        
        public String toString() {
            return workItem.getName() + " [" + workItem.getId() + "]";
        }
    }
    
}
