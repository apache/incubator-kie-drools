package org.drools.verifier.components;

import java.util.HashSet;
import java.util.Set;

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
    private static final long     serialVersionUID = 8871361928380977116L;

    private Pattern               pattern;

    private Set<PatternComponent> items            = new HashSet<PatternComponent>();

    public String getSourceGuid() {
        return pattern.getSourceGuid();
    }

    public VerifierComponentType getSourceType() {
        return pattern.getSourceType();
    }

    public String getName() {
        return pattern.getName();
    }

    public String getObjectTypeGuid() {
        return pattern.getObjectTypeGuid();
    }

    public boolean isPatternNot() {
        return pattern.isPatternNot();
    }

    public boolean isPatternExists() {
        return pattern.isPatternExists();
    }

    public boolean isPatternForall() {
        return pattern.isPatternForall();
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Set<PatternComponent> getItems() {
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
        return VerifierComponentType.SUB_PATTERN;
    }

}