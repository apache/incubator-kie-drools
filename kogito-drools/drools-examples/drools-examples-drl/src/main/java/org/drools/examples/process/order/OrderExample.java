/*
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

package org.drools.examples.process.order;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.process.instance.impl.demo.UIWorkItemHandler;

public class OrderExample extends JFrame {

    private static final long serialVersionUID = 510l;

    private KnowledgeBase kbase;
    private StatefulKnowledgeSession ksession;
    private KnowledgeRuntimeLogger logger;
    private int orderCounter;

    private JComboBox itemComboBox;
    private JTextField amountTextField;
    private JTextField customerIdTextField;
    private JTextField emailTextField;

    public static void main(String[] args) {
        new OrderExample().setVisible(true);
    }

    public OrderExample() {
        setSize(new Dimension(400, 220));
        setTitle("Order Example");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        createWorkingMemory();
        initializeComponent();
    }

    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        JLabel label = new JLabel("CustomerId");
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(label, c);
        
        customerIdTextField = new JTextField("A-12345");
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(customerIdTextField, c);
        
        label = new JLabel("Item");
        c = new GridBagConstraints();
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(label, c);
        
        itemComboBox = new JComboBox(new Object[] {
        	new ItemInfo("PC game", "I-9876", 50.0D),
        	new ItemInfo("Laptop", "I-5432", 500.0D),
        	new ItemInfo("Book", "Unknown", 5.0D),
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(itemComboBox, c);
        
        label = new JLabel("Amount");
        c = new GridBagConstraints();
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(label, c);
        
        amountTextField = new JTextField("1");
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(amountTextField, c);
        
        label = new JLabel("Email");
        c = new GridBagConstraints();
        c.gridy = 3;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(label, c);
        
        emailTextField = new JTextField();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(emailTextField, c);
        
        JButton createOrderButton = new JButton("Create");
        createOrderButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		createOrder();
        	}
        });
        c = new GridBagConstraints();
        c.gridy = 4;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(createOrderButton, c);
        
        final JCheckBox checkBox = new JCheckBox("Debugging output");
        checkBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (checkBox.isSelected()) {
        			addDebugRules();
        		} else {
        			removeDebugRules();
        		}
        	}
        });
        c = new GridBagConstraints();
        c.gridwidth = 2;
        c.gridy = 5;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(checkBox, c);
    }

    private void createWorkingMemory() {
        try {
        	kbase = createKnowledgeBase();
        	ksession = kbase.newStatefulKnowledgeSession();
        	logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/order");

        	CustomerService customerService = new DefaultCustomerService();
        	Customer c = new Customer("A-12345");
        	Calendar birthday = Calendar.getInstance();
        	birthday.set(1982, 1, 1);
        	c.setBirthday(birthday.getTime());
        	c.setFirstName("John");
        	c.setLastName("Doe");
        	customerService.addCustomer(c);
        	ksession.setGlobal("customerService", customerService);

        	ItemCatalog itemCatalog = new DefaultItemCatalog();
        	Item i = new Item("I-9876");
        	i.setName("PC game");
        	i.setMinimalAge(18);
        	itemCatalog.addItem(i);
        	i = new Item("I-5432");
        	i.setName("Laptop");
        	itemCatalog.addItem(i);
        	ksession.setGlobal("itemCatalog", itemCatalog);

        	ksession.getWorkItemManager().registerWorkItemHandler("Shipping", new ShippingWorkItemHandler(ksession));

        	ksession.getWorkItemManager().registerWorkItemHandler("Email", new WorkItemHandler() {
        		public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        			System.out.println("***********************************************************");
        			System.out.println("Sending email:");
        			System.out.println("From: " + workItem.getParameter("From"));
        			System.out.println("To: " + workItem.getParameter("To"));
        			System.out.println("Subject: " + workItem.getParameter("Subject"));
        			System.out.println("Text: ");
        			System.out.println(workItem.getParameter("Text"));
        			System.out.println("***********************************************************");
        	        manager.completeWorkItem(workItem.getId(), null);
        	    }
        	    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        	    }
        	});

        	UIWorkItemHandler handler = new UIWorkItemHandler();
        	ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        	handler.setVisible(true);

        } catch (Throwable t) {
        	t.printStackTrace();
        }
    }

    private void addDebugRules() {
        try {
        	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource(
            	"logging.drl", OrderExample.class), ResourceType.DRL );
        	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        } catch (Throwable t) {
        	t.printStackTrace();
        }
    }

    private void removeDebugRules() {
        kbase.removeKnowledgePackage("org.drools.examples.process.order.logging");
    }

    public void dispose() {
        super.dispose();
        logger.close();
    }

    private void createOrder() {
        Order order = new Order();
        order.setOrderId("Order-" + ++orderCounter);
        order.setCustomerId(customerIdTextField.getText());
        ItemInfo itemInfo = (ItemInfo) itemComboBox.getSelectedItem();
        order.addOrderItem(itemInfo.getItemId(), new Integer(amountTextField.getText()), itemInfo.getPrice());
        ksession.insert(order);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("order", order);
        parameters.put("email", emailTextField.getText());
        ksession.startProcess("org.drools.examples.process.ruleset.RuleSetExample", parameters);
        ksession.fireAllRules();
    }

    private static KnowledgeBase createKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(
        	"RuleSetExample.rf", OrderExample.class), ResourceType.DRF );
        kbuilder.add(ResourceFactory.newClassPathResource(
        	"workflow_rules.drl", OrderExample.class), ResourceType.DRL );
        kbuilder.add(ResourceFactory.newClassPathResource(
        	"validation.drl", OrderExample.class), ResourceType.DRL );
        kbuilder.add(ResourceFactory.newClassPathResource(
        	"assignment.dsl", OrderExample.class), ResourceType.DSL );
        kbuilder.add(ResourceFactory.newClassPathResource(
        	"assignment.dslr", OrderExample.class), ResourceType.DSLR );
        kbuilder.add(ResourceFactory.newClassPathResource(
        	"discount.drl", OrderExample.class), ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    private class ItemInfo {
        private String name;
        private String itemId;
        private double price;
        public ItemInfo(String name, String itemId, double price) {
        	this.name = name;
        	this.itemId = itemId;
        	this.price = price;
        }
        public String getName() {
        	return name;
        }
        public String getItemId() {
        	return itemId;
        }
        public double getPrice() {
        	return price;
        }
        public String toString() {
        	return name;
        }
    }
}
