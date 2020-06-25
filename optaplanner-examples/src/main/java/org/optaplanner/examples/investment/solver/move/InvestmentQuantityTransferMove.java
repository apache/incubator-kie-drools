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

package org.optaplanner.examples.investment.solver.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;

public class InvestmentQuantityTransferMove extends AbstractMove<InvestmentSolution> {

    private final AssetClassAllocation fromAssetClassAllocation;
    private final AssetClassAllocation toAssetClassAllocation;
    private final long transferMillis;

    public InvestmentQuantityTransferMove(AssetClassAllocation fromAssetClassAllocation,
            AssetClassAllocation toAssetClassAllocation, long transferMillis) {
        this.fromAssetClassAllocation = fromAssetClassAllocation;
        this.toAssetClassAllocation = toAssetClassAllocation;
        this.transferMillis = transferMillis;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<InvestmentSolution> scoreDirector) {
        return true;
    }

    @Override
    public InvestmentQuantityTransferMove createUndoMove(ScoreDirector<InvestmentSolution> scoreDirector) {
        return new InvestmentQuantityTransferMove(toAssetClassAllocation, fromAssetClassAllocation, transferMillis);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<InvestmentSolution> scoreDirector) {
        scoreDirector.beforeVariableChanged(fromAssetClassAllocation, "quantityMillis");
        fromAssetClassAllocation.setQuantityMillis(fromAssetClassAllocation.getQuantityMillis() - transferMillis);
        scoreDirector.afterVariableChanged(fromAssetClassAllocation, "quantityMillis");
        scoreDirector.beforeVariableChanged(toAssetClassAllocation, "quantityMillis");
        toAssetClassAllocation.setQuantityMillis(toAssetClassAllocation.getQuantityMillis() + transferMillis);
        scoreDirector.afterVariableChanged(toAssetClassAllocation, "quantityMillis");
    }

    @Override
    public InvestmentQuantityTransferMove rebase(ScoreDirector<InvestmentSolution> destinationScoreDirector) {
        return new InvestmentQuantityTransferMove(
                destinationScoreDirector.lookUpWorkingObject(fromAssetClassAllocation),
                destinationScoreDirector.lookUpWorkingObject(toAssetClassAllocation),
                transferMillis);
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(fromAssetClassAllocation, toAssetClassAllocation);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        long fromQuantity = fromAssetClassAllocation.getQuantityMillis();
        long toQuantity = toAssetClassAllocation.getQuantityMillis();
        return "[" + fromAssetClassAllocation + " {" + fromQuantity + "->" + (fromQuantity - transferMillis) + "}, "
                + toAssetClassAllocation + " {" + toQuantity + "->" + (toQuantity + transferMillis) + "}]";
    }

}
