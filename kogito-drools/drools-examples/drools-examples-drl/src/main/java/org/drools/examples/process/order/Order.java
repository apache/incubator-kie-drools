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
