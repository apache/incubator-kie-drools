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

package org.optaplanner.core.api.score.constraint.bigdecimal;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public class BigDecimalConstraintMatchTotal extends ConstraintMatchTotal {

    protected final Set<BigDecimalConstraintMatch> constraintMatchSet;
    protected BigDecimal weightTotal;

    public BigDecimalConstraintMatchTotal(String constraintPackage, String constraintName, int scoreLevel) {
        super(constraintPackage, constraintName, scoreLevel);
        constraintMatchSet = new HashSet<BigDecimalConstraintMatch>();
        weightTotal = BigDecimal.ZERO;
    }

    @Override
    public Set<BigDecimalConstraintMatch> getConstraintMatchSet() {
        return constraintMatchSet;
    }

    public BigDecimal getWeightTotal() {
        return weightTotal;
    }

    @Override
    public Number getWeightTotalAsNumber() {
        return weightTotal;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public BigDecimalConstraintMatch addConstraintMatch(RuleContext kcontext, BigDecimal weight) {
        weightTotal = weightTotal.add(weight);
        List<Object> justificationList = extractJustificationList(kcontext);
        BigDecimalConstraintMatch constraintMatch = new BigDecimalConstraintMatch(this, justificationList, weight);
        boolean added = constraintMatchSet.add(constraintMatch);
        if (!added) {
            throw new IllegalStateException("The constraintMatchTotal (" + this
                    + ") could not add constraintMatch (" + constraintMatch
                    + ") to its constraintMatchSet (" + constraintMatchSet + ").");
        }
        return constraintMatch;
    }

    public void removeConstraintMatch(BigDecimalConstraintMatch constraintMatch) {
        weightTotal = weightTotal.subtract(constraintMatch.getWeight());
        boolean removed = constraintMatchSet.remove(constraintMatch);
        if (!removed) {
            throw new IllegalStateException("The constraintMatchTotal (" + this
                    + ") could not remove constraintMatch (" + constraintMatch
                    + ") from its constraintMatchSet (" + constraintMatchSet + ").");
        }
    }

}
