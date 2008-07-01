package org.drools.examples.process.order;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.io.Reader;
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

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.impl.demo.UIWorkItemHandler;
import org.drools.rule.Package;

public class OrderExample extends JFrame {
	
	private static final long serialVersionUID = 4L;
	
	private RuleBase ruleBase;
	private StatefulSession session;
	private WorkingMemoryFileLogger logger;
	private int orderCounter;
	
	private JComboBox itemComboBox;
	private JTextField amountTextField;
	private JTextField customerIdTextField;
	
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
        
        JButton createOrderButton = new JButton("Create");
        createOrderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createOrder();
			}
        });
        c = new GridBagConstraints();
        c.gridy = 3;
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
        c.gridy = 4;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(checkBox, c);
	}
	
	private void createWorkingMemory() {
		try {
			ruleBase = createKnowledgeBase();
			session = ruleBase.newStatefulSession();
			logger = new WorkingMemoryFileLogger(session); 
			
			CustomerService customerService = new DefaultCustomerService();
			Customer c = new Customer("A-12345");
			Calendar birthday = Calendar.getInstance();
			birthday.set(1982, 1, 1);
			c.setBirthday(birthday.getTime());
			c.setFirstName("John");
			c.setLastName("Doe");
			customerService.addCustomer(c);
			session.setGlobal("customerService", customerService);
			
			ItemCatalog itemCatalog = new DefaultItemCatalog();
			Item i = new Item("I-9876");
			i.setName("PC game");
			i.setMinimalAge(18);
			itemCatalog.addItem(i);
			i = new Item("I-5432");
			i.setName("Laptop");
			itemCatalog.addItem(i);
			session.setGlobal("itemCatalog", itemCatalog);
			
			UIWorkItemHandler handler = new UIWorkItemHandler();
			session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
			handler.setVisible(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void addDebugRules() {
		try {
			PackageBuilder builder = new PackageBuilder();
			Reader source = new InputStreamReader(
				OrderExample.class.getResourceAsStream("logging.drl"));
			builder.addPackageFromDrl(source);
			for (Package p: builder.getPackages()) {
				if (!p.isValid()) {
					System.err.println("Invalid package " + p.getName() + ": " + p.getErrorSummary());
				}
				ruleBase.addPackage(p);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void removeDebugRules() {
		ruleBase.removePackage("org.drools.examples.process.order.logging");
	}
	
	public void dispose() {
		super.dispose();
		logger.writeToDisk();
	}
	
	private void createOrder() {
		Order order = new Order();
		order.setOrderId("Order-" + ++orderCounter);
		order.setCustomerId(customerIdTextField.getText());
		ItemInfo itemInfo = (ItemInfo) itemComboBox.getSelectedItem();
		order.addOrderItem(itemInfo.getItemId(), new Integer(amountTextField.getText()), itemInfo.getPrice());
		session.insert(order);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("orderId", order.getOrderId());
		session.startProcess("org.drools.examples.process.ruleset.RuleSetExample", parameters);
		session.fireAllRules();
	}

	private static RuleBase createKnowledgeBase() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new InputStreamReader(
			OrderExample.class.getResourceAsStream("RuleSetExample.rf"));
		builder.addProcessFromXml(source);
		source = new InputStreamReader(
			OrderExample.class.getResourceAsStream("workflow_rules.drl"));
		builder.addPackageFromDrl(source);
		source = new InputStreamReader(
			OrderExample.class.getResourceAsStream("validation.drl"));
		builder.addPackageFromDrl(source);
		source = new InputStreamReader(
			OrderExample.class.getResourceAsStream("assignment.dslr"));
		Reader dsl = new InputStreamReader(
			OrderExample.class.getResourceAsStream("assignment.dsl"));
		builder.addPackageFromDrl(source, dsl);
		RuleBaseConfiguration configuration = new RuleBaseConfiguration();
		configuration.setAdvancedProcessRuleIntegration(true);
		RuleBase ruleBase = RuleBaseFactory.newRuleBase(configuration);
		for (Package p: builder.getPackages()) {
			if (!p.isValid()) {
				System.err.println("Invalid package " + p.getName() + ": " + p.getErrorSummary());
			}
			ruleBase.addPackage(p);
		}
		return ruleBase;
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
