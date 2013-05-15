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

package org.jbpm.examples.checklist.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistItem.Status;
import org.jbpm.examples.checklist.ChecklistManager;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.test.JBPMHelper;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.task.api.UserGroupCallback;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ChecklistUI extends JFrame {

    private static final long serialVersionUID = 510l;
    
    private static ImageIcon[] ICONS = { 
    	new ImageIcon(ChecklistUI.class.getResource("/checklist/check32.png")),
    	new ImageIcon(ChecklistUI.class.getResource("/checklist/inprogress32.png")),
    	new ImageIcon(ChecklistUI.class.getResource("/checklist/abort32.png"))
    };
    
    private ChecklistManager checklistManager;
	List<ChecklistItem> items = null;
    
    private JComboBox<String> contexts;
    private JTable itemTable;
    private JTextField userNameTextField;
    
    public ChecklistUI() {
        setSize(new Dimension(400, 300));
        setTitle("Checklist");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initializeComponent();
        
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
        DefaultRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
		environment.setUserGroupCallback(new UserGroupCallback() {
			public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
				List<String> result = new ArrayList<String>();
				if ("actor4".equals(userId)) {
					result.add("group1");
				}
				return result;
			}
			public boolean existsUser(String arg0) {
				return true;
			}
			public boolean existsGroup(String arg0) {
				return true;
			}
		});
		environment.addAsset(
			ResourceFactory.newClassPathResource("checklist/SampleChecklistProcess.bpmn"),
			ResourceType.BPMN2);
		checklistManager = new DefaultChecklistManager(environment);
    }
    
    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        JButton createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                create();
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(createButton, c);
        
        contexts = new JComboBox<String>();
        contexts.setPreferredSize(new Dimension(80, 24));
        contexts.setSize(new Dimension(80, 24));
        c = new GridBagConstraints();
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(contexts, c);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                refresh();
            }
        });
        c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(refreshButton, c);
        
        itemTable = new JTable(1, 5);
        itemTable.setRowHeight(30);
        itemTable.setShowHorizontalLines(false);
        itemTable.setShowVerticalLines(false);
        itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = e.getFirstIndex();
				if (index >= 0) {
					ChecklistItem item = items.get(index);
					if (item.getStatus() == Status.Created) {
						String actorId = getActorId();
						try {
							checklistManager.claimTask(actorId, item.getTaskId());
							checklistManager.completeTask(actorId, item.getTaskId());
						} catch (Throwable t) {
							// Do nothing
						}
						refresh();
					} else if (item.getStatus() == Status.Reserved) {
						String actorId = getActorId();
						if (item.getActors().equals(actorId)) {
							try {
								checklistManager.completeTask(actorId, item.getTaskId());
							} catch (Throwable t) {
								// Do nothing
							}
							refresh();
						}
					}
				}
			}
		});
        // TODO:
        // default width of columns
        // icons for state
        // not-editable
        // no selection
        // (scratch for aborted?)
        // replace refresh, create, etc. by icon
        c = new GridBagConstraints();
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(itemTable, c);
        
        JLabel nameLabel = new JLabel("Logged in as:");
        c = new GridBagConstraints();
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, c);
        
        userNameTextField = new JTextField("actor1");
        userNameTextField.setPreferredSize(new Dimension(80, 20));
        userNameTextField.setSize(new Dimension(80, 20));
        c = new GridBagConstraints();
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(userNameTextField, c);
        
        JButton createItemButton = new JButton("+");
        createItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                createNewItem();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        panel.add(createItemButton, c);

        panel.doLayout();
    }
    
    private String getActorId() {
    	return userNameTextField.getText();
    }
    
    private void refresh() {
    	Long processInstanceId = getSelectedProcessInstance();
    	if (processInstanceId != null) {
    		List<ChecklistContextConstraint> contexts = null;
    		items = checklistManager.getTasks(processInstanceId, contexts);
    	} else {
    		items = new ArrayList<ChecklistItem>();
    	}
    	DefaultTableModel tableModel = new DefaultTableModel(items.size(), 4);
    	for (int i = 0; i < items.size(); i++) {
    		ChecklistItem item = items.get(i);
			String orderingNb = item.getOrderingNb();
			if (orderingNb == null) {
				orderingNb = "";
			} else if (orderingNb.endsWith("+")) {
				orderingNb = "*";
			}
    		tableModel.setValueAt(item.getStatus(), i, 0);
    		tableModel.setValueAt("(" + orderingNb + ")", i, 1);
    		tableModel.setValueAt(item.getName(), i, 2);
    		tableModel.setValueAt(item.getActors(), i, 3);
//    		tableModel.setValueAt(item.getPriority(), i, 4);
    	}
    	itemTable.setModel(tableModel);
        itemTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6L;
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = new JLabel();
                if (value != null) {
                	label.setHorizontalAlignment(JLabel.CENTER);
                	switch ((Status) value) {
                		case Completed: label.setIcon(ICONS[0]); break;
                		case Aborted: label.setIcon(ICONS[2]); break;
                		case InProgress: label.setIcon(ICONS[1]); break;
                		case Created: label.setIcon(ICONS[1]); break;
                		case Reserved: label.setIcon(ICONS[1]); break;
                		case Pending: break;
                		default: break;                			
                	}
                }
                return label;
            }
        });
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(230);
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(120);
    }
    
    private void create() {
    	long processInstanceId = checklistManager.createContext("org.jbpm.examples.checklist.sample1");
    	String contextName = processInstanceId + "";
    	contexts.addItem(contextName);
    	contexts.setSelectedItem(contextName);
    	refresh();
    }
    
    private Long getSelectedProcessInstance() {
    	Object selected = contexts.getSelectedItem();
    	if (selected != null) {
    		try {
    			return new Long((String) selected);
    		} catch (NumberFormatException e) {
    			// Do nothing
    		}
    	}
    	return null;
    }
    
    private void createNewItem() {
    	if (getSelectedProcessInstance() != null) {
	    	CreateItemDialog dialog = new CreateItemDialog(this, getActorId());
	    	dialog.setVisible(true);
	    	String name = dialog.getItemName();
	    	if (name != null) {
	    		ChecklistItem item = getSelectedItem();
	    		String orderingNb = null;
	    		if (item != null) {
	    			orderingNb = item.getOrderingNb() + "+";
	    		} else {
	    			orderingNb = items.get(items.size() - 1).getOrderingNb() + "+";
	    		}
	    		String[] actors = null;
	    		String actorIds = dialog.getActors();
	    		if (actorIds.trim().length() == 0) {
	    			actors = new String[0];
	    		} else {
	    			actors = actorIds.split(",");
	    		}
	    		String[] groups = null;
	    		String groupIds = dialog.getGroups();
	    		if (groupIds.trim().length() == 0) {
	    			groups = new String[0];
	    		} else {
	    			groups = groupIds.split(",");
	    		}
	    		checklistManager.addTask(
    				dialog.getActors(),
    				actors,
    				groups,
    				name,
    				orderingNb,
    				getSelectedProcessInstance());
	    		refresh();
	    	}
    	}
    }
    
    private ChecklistItem getSelectedItem() {
    	int index = itemTable.getSelectedRow();
    	if (index >= 0) {
    		return items.get(index);
    	}
    	return null;
    }
    
    public static void main(String[] args) {
		new ChecklistUI().setVisible(true);
	}
    
}
