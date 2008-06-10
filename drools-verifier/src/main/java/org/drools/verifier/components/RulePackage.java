package org.drools.verifier.components;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Toni Rikkola
 */
public class RulePackage extends VerifierComponent {

	private static int index = 0;

	private int offset = 0;
	private String name;
	private Set<VerifierRule> rules = new HashSet<VerifierRule>();

	public RulePackage() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.RULE_PACKAGE;
	}
	
	public int getOffset(){ 
	    offset++;
        return offset % 2;
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<VerifierRule> getRules() {
		return rules;
	}

	public void setRules(Set<VerifierRule> rules) {
		this.rules = rules;
	}
}
