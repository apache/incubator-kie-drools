package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class VerifierRule extends RuleComponent
    implements
    Cause {

    private String      ruleSalience;
    private String      ruleAgendaGroup;
    private Consequence consequence;
    private int         lineNumber;

    private int         packageId;

    public CauseType getCauseType() {
        return CauseType.RULE;
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

    public Consequence getConsequence() {
        return consequence;
    }

    public void setConsequence(Consequence consequence) {
        this.consequence = consequence;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "Rule '" + getRuleName() + "'";
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RULE;
    }
}
