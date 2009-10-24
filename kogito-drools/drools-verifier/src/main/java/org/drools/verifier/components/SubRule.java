package org.drools.verifier.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.report.components.Cause;
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
    private static final long serialVersionUID = 8871361928380977116L;

    private Set<Cause>        items            = new HashSet<Cause>();

    public CauseType getCauseType() {
        return CauseType.RULE_POSSIBILITY;
    }

    public Set<Cause> getItems() {
        return items;
    }

    public int getAmountOfItems() {
        return items.size();
    }

    public void add(SubPattern patternPossibility) {
        items.add( patternPossibility );
    }

    @Override
    public String toString() {
        return "RulePossibility from rule: " + getRuleName() + ", amount of items:" + items.size();
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RULE_POSSIBILITY;
    }
}
