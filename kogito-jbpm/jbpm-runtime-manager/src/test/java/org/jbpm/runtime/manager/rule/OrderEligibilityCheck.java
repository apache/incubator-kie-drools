package org.jbpm.runtime.manager.rule;

import java.util.Date;

public class OrderEligibilityCheck {

	public static Boolean dateDifference(Date dt1, Date dt2) {
		long diff = dt1.getTime() - dt2.getTime();
		
		System.out.println("Start Date: " + dt2);
		System.out.println("End Date: " + dt1);
		long result = diff/(24*60*60*1000);
		
		if (result > 30) {
			System.out.println("Date difference is more than 30");
			return true;
		}
		System.out.println("Date difference is less than 30");
		return false;
	}
}
