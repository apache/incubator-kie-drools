package org.drools.examples.process.order;

import java.util.HashMap;
import java.util.Map;

public class DefaultCustomerService implements CustomerService {

	private Map<String, Customer> customers = new HashMap<String, Customer>();
	
	public Customer getCustomer(String customerId) {
		return customers.get(customerId);
	}
	
	public void addCustomer(Customer customer) {
		this.customers.put(customer.getCustomerId(), customer);
	}
	
}
