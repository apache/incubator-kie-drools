package org.jbpm.runtime.manager.rule;

import java.io.Serializable;

public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String accountStatus = null;
	private Boolean accountEligible = null;
	
	public Account(String accountStatus) {
		this.accountStatus = accountStatus;
		this.accountEligible = false;
	}

	public Boolean getAccountEligible() {
		return accountEligible;
	}

	public void setAccountEligible(Boolean accountEligibile) {
		this.accountEligible = accountEligibile;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

}
