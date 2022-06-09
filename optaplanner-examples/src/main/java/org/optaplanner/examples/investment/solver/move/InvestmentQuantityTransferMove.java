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
