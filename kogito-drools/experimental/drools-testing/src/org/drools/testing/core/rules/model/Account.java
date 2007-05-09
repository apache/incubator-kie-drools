package org.drools.testing.core.rules.model;


public class Account {
	
	private Integer balance = new Integer(0);
	private String status = "active";
	
	public Account () {}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
