package org.drools.analytics.components;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Toni Rikkola
 */
public class RulePackage extends AnalyticsComponent {

	private static int index = 0;

	private String name;
	private Set<AnalyticsRule> rules = new HashSet<AnalyticsRule>();

	public RulePackage() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.RULE_PACKAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<AnalyticsRule> getRules() {
		return rules;
	}

	public void setRules(Set<AnalyticsRule> rules) {
		this.rules = rules;
	}
}
