/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.investmentallocation.solver.score;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.investmentallocation.domain.AssetClassAllocation;
import org.optaplanner.examples.investmentallocation.domain.InvestmentAllocationSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvestmentIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<InvestmentAllocationSolution> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private InvestmentAllocationSolution solution;

    private long standardDeviationSquaredFemtos;
    private long standardDeviationSquaredFemtosMaximum;

    private long hardScore;
    private long softScore;

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void resetWorkingSolution(InvestmentAllocationSolution solution) {
        this.solution = solution;
        standardDeviationSquaredFemtos = 0L;
        long standardDeviationMillisMaximum = solution.getParametrization().getStandardDeviationMillisMaximum();
        standardDeviationSquaredFemtosMaximum = standardDeviationMillisMaximum * standardDeviationMillisMaximum
                * 1000L * 1000L * 1000L;
        hardScore = 0L;
        softScore = 0L;
        for (AssetClassAllocation allocation : solution.getAssetClassAllocationList()) {
            insertQuantityMillis(allocation);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insertQuantityMillis((AssetClassAllocation) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retractQuantityMillis((AssetClassAllocation) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insertQuantityMillis((AssetClassAllocation) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retractQuantityMillis((AssetClassAllocation) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    // ************************************************************************
    // Modify methods
    // ************************************************************************

    private void insertQuantityMillis(AssetClassAllocation allocation) {
        if (standardDeviationSquaredFemtos > standardDeviationSquaredFemtosMaximum) {
            hardScore += standardDeviationSquaredFemtos - standardDeviationSquaredFemtosMaximum;
        }
        standardDeviationSquaredFemtos += calculateStandardDeviationSquaredFemtosDelta(allocation);
        if (standardDeviationSquaredFemtos > standardDeviationSquaredFemtosMaximum) {
            hardScore -= standardDeviationSquaredFemtos - standardDeviationSquaredFemtosMaximum;
        }
        softScore += allocation.getQuantifiedExpectedReturnMicros();
    }

    private void retractQuantityMillis(AssetClassAllocation allocation) {
        if (standardDeviationSquaredFemtos > standardDeviationSquaredFemtosMaximum) {
            hardScore += standardDeviationSquaredFemtos - standardDeviationSquaredFemtosMaximum;
        }
        standardDeviationSquaredFemtos -= calculateStandardDeviationSquaredFemtosDelta(allocation);
        if (standardDeviationSquaredFemtos > standardDeviationSquaredFemtosMaximum) {
            hardScore -= standardDeviationSquaredFemtos - standardDeviationSquaredFemtosMaximum;
        }
        softScore -= allocation.getQuantifiedExpectedReturnMicros();
    }

    private long calculateStandardDeviationSquaredFemtosDelta(AssetClassAllocation allocation) {
        long squaredFemtos = 0L;
        for (AssetClassAllocation other : solution.getAssetClassAllocationList()) {
            if (allocation == other) {
                long micros = allocation.getQuantifiedStandardDeviationRiskMicros();
                squaredFemtos += micros * micros * 1000L;
            } else {
                long picos = allocation.getQuantifiedStandardDeviationRiskMicros() * other.getQuantifiedStandardDeviationRiskMicros();
                squaredFemtos += picos * allocation.getAssetClass().getCorrelationMillisMap().get(other.getAssetClass());
                squaredFemtos += picos * other.getAssetClass().getCorrelationMillisMap().get(allocation.getAssetClass());
            }
        }
        return squaredFemtos;
    }

    @Override
    public Score calculateScore() {
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
