package org.drools.ruleflow.core.impl;

import java.io.Serializable;

public class DroolsConsequenceAction implements Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = 400L;
    private String consequence;
	
	public DroolsConsequenceAction(String consequence) {
		this.consequence = consequence;
	}
	
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	
	public String getConsequence() {
		return consequence;
	}

	public String toString() {
		return consequence;
	}
}
