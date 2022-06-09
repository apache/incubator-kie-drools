package org.optaplanner.core.impl.solver;

import java.util.List;
import java.util.concurrent.CompletableFuture;

final class BestSolutionContainingProblemChanges<Solution_> {
    private final Solution_ bestSolution;
    private final List<CompletableFuture<Void>> containedProblemChanges;

    public BestSolutionContainingProblemChanges(Solution_ bestSolution, List<CompletableFuture<Void>> containedProblemChanges) {
        this.bestSolution = bestSolution;
        this.containedProblemChanges = containedProblemChanges;
    }

    public Solution_ getBestSolution() {
        return bestSolution;
    }

    public void completeProblemChanges() {
        containedProblemChanges.forEach(futureProblemChange -> futureProblemChange.complete(null));
    }

    public void completeProblemChangesExceptionally(Throwable exception) {
        containedProblemChanges.forEach(futureProblemChange -> futureProblemChange.completeExceptionally(exception));
    }
}
