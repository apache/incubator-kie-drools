/*
 * Created on 16/09/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.drools.testing.core.filters;

import java.util.Collection;

import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;

/**
 * @author mshaw
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultipleRuleAgendaFilter implements AgendaFilter {

	private Collection rules;
	private final boolean accept = false;
	
	public MultipleRuleAgendaFilter (Collection rules) {
		this.rules = rules;
	}
	
	public boolean accept(Activation activation) {
		
		if (rules.contains(activation.getRule().getName())) 
			return true;
		else 
			return false;
	}
	
}
