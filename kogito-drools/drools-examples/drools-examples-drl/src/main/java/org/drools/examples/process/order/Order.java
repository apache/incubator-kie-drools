package org.drools.examples.process.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
	
	private String orderId;
	private String customerId;
	private int discountPercentage;
	private Date date;
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();
	private List<String> errorList;
	private String trackingId;
	
	public Order() {
		date = new Date();
	}
	
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
	
	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	public Date getDate() {
		return date;
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
	
	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String toString() {
		return "Order " + orderId;
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
