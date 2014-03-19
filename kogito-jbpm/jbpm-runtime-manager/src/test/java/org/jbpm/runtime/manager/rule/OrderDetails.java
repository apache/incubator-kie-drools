package org.jbpm.runtime.manager.rule;

import java.io.Serializable;
import java.util.Date;

public class OrderDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Date startDate;
	
	Date endDate;
	
	public OrderDetails(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
