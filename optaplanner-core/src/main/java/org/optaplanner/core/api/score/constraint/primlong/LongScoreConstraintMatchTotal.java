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

package org.optaplanner.core.api.score.constraint.primlong;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.constraint.ScoreConstraintMatchTotal;

public class LongScoreConstraintMatchTotal extends ScoreConstraintMatchTotal {

    protected final Set<LongScoreConstraintMatch> constraintMatchSet;
    protected long weightTotal;

    public LongScoreConstraintMatchTotal(String constraintPackage, String constraintName, int scoreLevel) {
        super(constraintPackage, constraintName, scoreLevel);
        constraintMatchSet = new HashSet<LongScoreConstraintMatch>();
        weightTotal = 0;
    }

    @Override
    public Set<LongScoreConstraintMatch> getConstraintMatchSet() {
        return constraintMatchSet;
    }

    public long getWeightTotal() {
        return weightTotal;
    }

    @Override
    public Number getWeightTotalAsNumber() {
        return weightTotal;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public LongScoreConstraintMatch addConstraintMatch(RuleContext kcontext, long weight) {
        weightTotal += weight;
        List<Object> justificationList = extractJustificationList(kcontext);
        LongScoreConstraintMatch constraintMatch = new LongScoreConstraintMatch(this, justificationList, weight);
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The scoreConstraintMatchTotal (" + this
                    + ") could not add constraintMatch (" + constraintMatch
                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
        return constraintMatch;
    }

    public void removeConstraintMatch(LongScoreConstraintMatch constraintMatch) {
        weightTotal -= constraintMatch.getWeight();
        boolean removed = constraintMatchSet.remove(constraintMatch);
        if (!removed) {
            throw new IllegalStateException("The scoreConstraintMatchTotal (" + this
                    + ") could not remove constraintMatch (" + constraintMatch
                    + ") from its constraintMatchSet (" + constraintMatchSet + ").");
        }
    }

}
