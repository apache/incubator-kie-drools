package org.jbpm.runtime.manager.rule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrderEligibilityCheck {

    private static final Logger logger = LoggerFactory.getLogger(OrderEligibilityCheck.class);    
    
	public static Boolean dateDifference(Date dt1, Date dt2) {
		long diff = dt1.getTime() - dt2.getTime();
		
		logger.debug("Start Date: " + dt2);
		logger.debug("End Date: " + dt1);
		long result = diff/(24*60*60*1000);
		
		if (result > 30) {
			logger.debug("Date difference is more than 30");
			return true;
		}
		logger.debug("Date difference is less than 30");
		return false;
	}
}
