package org.drools.ruleflow.core.impl;

import java.io.Serializable;

public class DroolsConsequenceAction implements Serializable {
	
    private static final long serialVersionUID = 400L;
    
    private String dialect = "mvel";
    private String consequence;
	
	public DroolsConsequenceAction(String dialect, String consequence) {
	    this.dialect = dialect;
		this.consequence = consequence;
	}
	
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	
	public String getConsequence() {
		return consequence;
	}
	
	public void setDialect(String dialect) {
	    this.dialect = dialect;
	}
	
	public String getDialect() {
	    return dialect;
	}

	public String toString() {
		return consequence;
	}
}
