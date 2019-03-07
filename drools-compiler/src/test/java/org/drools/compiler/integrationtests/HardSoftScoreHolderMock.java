package org.drools.compiler.integrationtests;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.runtime.rule.RuleContext;

public class HardSoftScoreHolderMock {

    private final Set<ConstraintMatchMock> constraintMatchSet = new LinkedHashSet<>();
    private int softScore = 0;

    public void addSoftConstraintMatch(RuleContext ruleContext, int softWeight) {
        softScore += softWeight;
        registerConstraintMatch(ruleContext);
    }

    protected ConstraintMatchMock registerConstraintMatch(RuleContext kcontext) {
        List<Object> justificationList = extractJustificationList(kcontext);

        ConstraintMatchMock constraintMatch = new ConstraintMatchMock(kcontext, justificationList, softScore);
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The constraintMatchTotal (" + this
                                                    + ") could not add constraintMatch (" + constraintMatch
                                                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
        return constraintMatch;
    }

    protected List<Object> extractJustificationList(RuleContext kcontext) {
        return ((org.drools.core.spi.Activation) kcontext.getMatch()).getObjectsDeep();
    }

    class ConstraintMatchMock {

        private String constraintPackage;
        private String constraintName;
        private List<Object> justificationList;
        private int score;

        public ConstraintMatchMock(RuleContext ruleContext, List<Object> justificationList, int score) {
            this.constraintName = ruleContext.getMatch().getRule().getName();
            this.constraintPackage = ruleContext.getMatch().getRule().getPackageName();
            this.justificationList = justificationList;
            this.score = score;
        }

        @Override
        public int hashCode() {
            return (((17 * 37)
                    + constraintPackage.hashCode()) * 37
                    + constraintName.hashCode()) * 37
                    + justificationList.hashCode();
        }

        public String getConstraintId() {
            return constraintPackage + "/" + constraintName;
        }

        public String getIdentificationString() {
            return getConstraintId() + "/" + justificationList;
        }

        @Override
        public String toString() {
            return getIdentificationString() + "=" + score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof ConstraintMatchMock) {
                ConstraintMatchMock other = (ConstraintMatchMock) o;
                return constraintPackage.equals(other.constraintPackage)
                        && constraintName.equals(other.constraintName)
                        && justificationList.equals(other.justificationList);
            } else {
                return false;
            }
        }
    }
}

