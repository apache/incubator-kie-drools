package org.drools.testing.core.wrapper;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;

/**
 * 
 * @author Matt
 *
 * A thread safe instance of the rule base which can contain many packages
 * 
 * (c) Matt Shaw
 */
public class RuleBaseWrapper {

	private static RuleBaseWrapper instance = null;
	private RuleBase ruleBase = null;
	
	protected RuleBaseWrapper () {
		setRuleBase(RuleBaseFactory.newRuleBase());
	}
	
	public synchronized static RuleBaseWrapper getInstance () {
		if (instance == null)
			instance = new RuleBaseWrapper();
		return instance;
	}

	public RuleBase getRuleBase() {
		return ruleBase;
	}

	public void setRuleBase(RuleBase ruleBase) {
		this.ruleBase = ruleBase;
	}
}
