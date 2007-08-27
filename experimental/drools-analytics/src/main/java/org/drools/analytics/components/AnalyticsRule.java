package org.drools.analytics.components;

import org.drools.analytics.result.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsRule extends AnalyticsComponent implements Cloneable,
		Cause {

	private static int index = 0;

	private String ruleSalience;
	private String ruleAgendaGroup;
	private String consequence;
	private int lineNumber;

	public AnalyticsRule() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.RULE;
	}

	public String getRuleAgendaGroup() {
		return ruleAgendaGroup;
	}

	public void setRuleAgendaGroup(String agendaGroup) {
		this.ruleAgendaGroup = agendaGroup;
	}

	public String getRuleSalience() {
		return ruleSalience;
	}

	public void setRuleSalience(String salience) {
		this.ruleSalience = salience;
	}

	public String getConsequence() {
		return consequence;
	}

	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public Object clone() {
		AnalyticsRule clone = new AnalyticsRule();

		clone.setRuleName(ruleName);
		clone.setRuleSalience(ruleSalience);
		clone.setRuleAgendaGroup(ruleAgendaGroup);
		clone.setConsequence(consequence);
		clone.setLineNumber(lineNumber);

		return clone;
	}

	@Override
	public String toString() {
		return "Rule '" + ruleName + "'";
	}
}
