package org.drools.verifier.components;

import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 * Instance of this class represents a possible combination of Constraints under
 * one Pattern. Each possibility returns true if all the Constraints in the
 * combination are true.
 *
 * @author Toni Rikkola
 */
public class SubPattern extends PatternComponent
    implements
    Possibility {
    private static final long serialVersionUID = 8871361928380977116L;

    private Set<Cause>        items            = new HashSet<Cause>();

    public CauseType getCauseType() {
        return CauseType.PATTERN_POSSIBILITY;
    }

    public Set<Cause> getItems() {
        return items;
    }

    public int getAmountOfItems() {
        return items.size();
    }

    public void add(Restriction restriction) {
        items.add( restriction );
    }

    @Override
    public String toString() {
        return "PatternPossibility from rule: " + getRuleName() + ", amount of items:" + items.size();
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.PATTERN_POSSIBILITY;
    }

}