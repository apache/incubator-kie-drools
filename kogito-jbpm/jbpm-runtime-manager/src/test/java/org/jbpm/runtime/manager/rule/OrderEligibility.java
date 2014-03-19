package org.jbpm.runtime.manager.rule;

import java.io.Serializable;

public class OrderEligibility implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private OrderDetails orderDetails = null;
	
	private Boolean orderEligibile = false;
	
	public OrderEligibility(OrderDetails orderDetails) {
		this.orderDetails = orderDetails;
	}

	public OrderDetails getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(OrderDetails orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Boolean getOrderEligibile() {
		return orderEligibile;
	}

	public void setOrderEligibile(Boolean orderEligibile) {
		this.orderEligibile = orderEligibile;
	}
}
