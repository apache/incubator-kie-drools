/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.runtime.rule.RuleContext;

public class IntScoreConstraintMatchTotal extends ScoreConstraintMatchTotal {

    protected final Set<IntScoreConstraintMatch> constraintMatchSet;
    protected int weightTotal;

    public IntScoreConstraintMatchTotal(String constraintPackage, String constraintName, int scoreLevel) {
        super(constraintPackage, constraintName, scoreLevel);
        constraintMatchSet = new HashSet<IntScoreConstraintMatch>();
        weightTotal = 0;
    }

    public int getWeightTotal() {
        return weightTotal;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public IntScoreConstraintMatch addConstraintMatch(RuleContext kcontext, int weight) {
        weightTotal += weight;
        List<Object> ruleMatchObjects = kcontext.getMatch().getObjects();
        List<Object> justificationList = new ArrayList<Object>(ruleMatchObjects); // TODO use functional ReverseList
        // Drools always returns the rule matches in reverse order
        Collections.reverse(justificationList);
        IntScoreConstraintMatch constraintMatch = new IntScoreConstraintMatch(this, justificationList, weight);
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The scoreConstraintMatchTotal (" + this
                    + ") could not add constraintMatch (" + constraintMatch
                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
        return constraintMatch;
    }

    public void removeConstraintMatch(IntScoreConstraintMatch constraintMatch) {
        weightTotal -= constraintMatch.getWeight();
        boolean removed = constraintMatchSet.remove(constraintMatch);
        if (!removed) {
            throw new IllegalStateException("The scoreConstraintMatchTotal (" + this
                    + ") could not remove constraintMatch (" + constraintMatch
                    + ") from its constraintMatchSet (" + constraintMatchSet + ").");
        }
    }

    @Override
    public String toString() {
        return super.toString() + "=" + weightTotal;
    }

}
