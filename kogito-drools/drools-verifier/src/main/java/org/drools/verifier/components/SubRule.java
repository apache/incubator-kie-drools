package org.drools.verifier.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.verifier.components.Consequence.ConsequenceType;
import org.drools.verifier.report.components.CauseType;

/**
 * Instance of this class represents a possible combination of
 * PatternPosibilities under one Rule. Each possibility returns true if all the
 * PatternPosibilities in the combination are true.
 *
 * @author Toni Rikkola
 */
public class SubRule extends RuleComponent
    implements
    Serializable,
    Possibility {
    private static final long  serialVersionUID = 8871361928380977116L;

    private Set<RuleComponent> items            = new HashSet<RuleComponent>();

    private VerifierRule       rule;

    public CauseType getCauseType() {
        return CauseType.SUB_RULE;
    }

    public Set<RuleComponent> getItems() {
        return items;
    }

    public int getAmountOfItems() {
        return items.size();
    }

    public void add(SubPattern patternPossibility) {
        items.add( patternPossibility );
    }

    public void setRule(VerifierRule rule) {
        this.rule = rule;
    }

    public VerifierRule getRule() {
        return rule;
    }

    public String getConsequenceGuid() {
        return rule.getConsequenceGuid();
    }

    public ConsequenceType getConsequenceType() {
        return rule.getConsequenceType();
    }

    public Map<String, String> getAttributes() {
        return rule.getAttributes();
    }

    @Override
    public String toString() {
        return "RulePossibility from rule: " + getRuleName() + ", amount of items:" + items.size();
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.SUB_RULE;
    }
}
