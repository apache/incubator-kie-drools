package org.drools.examples.process.order;

import java.util.ArrayList;
import java.util.List;

public class Order {
	
	private String orderId;
	private String customerId;
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();
	private List<String> errorList;
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	
	public void addOrderItem(String itemId, int amount, double price) {
		orderItems.add(new OrderItem(itemId, amount, price));
	}
	
	public double getPrice() {
		double price = 0;
		for (OrderItem item: orderItems) {
			price += item.getAmount() * item.getPrice();
		}
		return price;
	}
	
	public List<String> getErrorList() {
		return errorList;
	}
	
	public void addError(String error) {
		if (errorList == null) {
			errorList = new ArrayList<String>();
		}
		errorList.add(error);
	}
	
	public class OrderItem {
		
		private String itemId;
		private int amount;
		private double price;
		
		public OrderItem(String itemId, int amount, double price) {
			this.itemId = itemId;
			this.amount = amount;
			this.price = price;
		}
		
		public String getItemId() {
			return itemId;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public double getPrice() {
			return price;
		}
		
	}
	
}
