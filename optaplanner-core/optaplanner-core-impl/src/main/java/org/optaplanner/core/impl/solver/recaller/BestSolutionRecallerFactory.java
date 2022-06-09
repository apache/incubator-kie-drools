package org.optaplanner.core.impl.solver.recaller;

import org.optaplanner.core.config.solver.EnvironmentMode;

public class BestSolutionRecallerFactory {

    public static BestSolutionRecallerFactory create() {
        return new BestSolutionRecallerFactory();
    }

    public <Solution_> BestSolutionRecaller<Solution_> buildBestSolutionRecaller(EnvironmentMode environmentMode) {
        BestSolutionRecaller<Solution_> bestSolutionRecaller = new BestSolutionRecaller<>();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            bestSolutionRecaller.setAssertInitialScoreFromScratch(true);
            bestSolutionRecaller.setAssertShadowVariablesAreNotStale(true);
            bestSolutionRecaller.setAssertBestScoreIsUnmodified(true);
        }
        return bestSolutionRecaller;
    }
}
