package org.drools.examples.process.order;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.impl.demo.UIWorkItemHandler;
import org.drools.rule.Package;

public class OrderExample {
	
	public static void main(String[] args) {
		try {
			RuleBase ruleBase = createKnowledgeBase();
			StatefulSession session = ruleBase.newStatefulSession();
			
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
			i.setName("Rampage !!! PC game");
			i.setMinimalAge(18);
			//itemCatalog.addItem(i);
			session.setGlobal("itemCatalog", itemCatalog);
			
			UIWorkItemHandler handler = new UIWorkItemHandler();
			session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
			handler.setVisible(true);
			
			Order order = new Order();
			order.setOrderId("O-ABCDE");
			order.setCustomerId("A-12345");
			order.addOrderItem("I-9876", 3, 50.0D);
			session.insert(order);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("orderId", order.getOrderId());
			session.startProcess("org.drools.examples.process.ruleset.RuleSetExample", parameters);
			session.fireAllRules();
		} catch (Throwable t) {
			t.printStackTrace();
		}
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
}
