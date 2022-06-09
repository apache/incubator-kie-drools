package org.optaplanner.examples.investment.solver.solution.initializer;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;

public class InvestmentAllocationSolutionInitializer implements CustomPhaseCommand<InvestmentSolution> {

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
