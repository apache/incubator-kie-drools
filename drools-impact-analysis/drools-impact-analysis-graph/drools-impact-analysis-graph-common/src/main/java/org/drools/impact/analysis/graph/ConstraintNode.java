package org.drools.impact.analysis.graph;

import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.left.Constraint;
import org.drools.impact.analysis.model.left.Pattern;

public class ConstraintNode extends BaseNode {

    private String id;

    private Pattern pattern; // parent Pattern
    private Constraint constraint;

    public ConstraintNode(Rule rule, Pattern pattern, int patternIndex, Constraint constraint, int constraintIndex) {
        super(rule);
        this.pattern = pattern;
        this.constraint = constraint;
        this.id = getFqdn() + ":P" + patternIndex + ":C" + constraintIndex;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        // maybe original expression
        if (constraint.getProperty() == null) {
            return "[C: " + pattern.getPatternClass().getSimpleName() + "]";
        } else {
            return "[C: " + pattern.getPatternClass().getSimpleName() + ", " + constraint.getProperty() + "]";
        }
    }

    @Override
    public String toString() {
        return "ConstraintNode [id=" + id + ", constraint=" + constraint + "]";
    }

}
