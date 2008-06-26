package org.drools.examples.process.order;

public interface CustomerService {
	
	Customer getCustomer(String customerId);
	
	void addCustomer(Customer customer);

}
