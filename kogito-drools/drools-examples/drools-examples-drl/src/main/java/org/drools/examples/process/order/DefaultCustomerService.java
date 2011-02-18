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
