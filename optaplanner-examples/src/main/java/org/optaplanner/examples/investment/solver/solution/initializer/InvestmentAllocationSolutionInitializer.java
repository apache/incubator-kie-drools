/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.investment.solver.solution.initializer;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvestmentAllocationSolutionInitializer implements CustomPhaseCommand<InvestmentSolution> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void changeWorkingSolution(ScoreDirector<InvestmentSolution> scoreDirector) {
        InvestmentSolution solution = scoreDirector.getWorkingSolution();
        distributeQuantityEvenly(scoreDirector, solution);
    }

    private void distributeQuantityEvenly(ScoreDirector<InvestmentSolution> scoreDirector, InvestmentSolution solution) {
        long budget = InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS;
        int size = solution.getAssetClassAllocationList().size();
        long budgetPerAllocation = budget / size;
        long remainder = budget % size;
        for (AssetClassAllocation allocation : solution.getAssetClassAllocationList()) {
            long quantityMillis = budgetPerAllocation;
            if (remainder > 0L) {
                remainder--;
                quantityMillis++;
            }
            scoreDirector.beforeVariableChanged(allocation, "quantityMillis");
            allocation.setQuantityMillis(quantityMillis);
            scoreDirector.afterVariableChanged(allocation, "quantityMillis");
            scoreDirector.triggerVariableListeners();
        }
    }

}
