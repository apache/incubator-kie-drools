package org.optaplanner.core.impl.exhaustivesearch.scope;

import java.util.List;
import java.util.SortedSet;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ExhaustiveSearchPhaseScope<Solution_> extends AbstractPhaseScope<Solution_> {

    private List<ExhaustiveSearchLayer> layerList;
    private SortedSet<ExhaustiveSearchNode> expandableNodeQueue;
    private Score bestPessimisticBound;

    private ExhaustiveSearchStepScope<Solution_> lastCompletedStepScope;

    public ExhaustiveSearchPhaseScope(SolverScope<Solution_> solverScope) {
        super(solverScope);
        lastCompletedStepScope = new ExhaustiveSearchStepScope<>(this, -1);
    }

    public List<ExhaustiveSearchLayer> getLayerList() {
        return layerList;
    }

    public void setLayerList(List<ExhaustiveSearchLayer> layerList) {
        this.layerList = layerList;
    }

    public SortedSet<ExhaustiveSearchNode> getExpandableNodeQueue() {
        return expandableNodeQueue;
    }

    public void setExpandableNodeQueue(SortedSet<ExhaustiveSearchNode> expandableNodeQueue) {
        this.expandableNodeQueue = expandableNodeQueue;
    }

    public Score getBestPessimisticBound() {
        return bestPessimisticBound;
    }

    public void setBestPessimisticBound(Score bestPessimisticBound) {
        this.bestPessimisticBound = bestPessimisticBound;
    }

    @Override
    public ExhaustiveSearchStepScope<Solution_> getLastCompletedStepScope() {
        return lastCompletedStepScope;
    }

    public void setLastCompletedStepScope(ExhaustiveSearchStepScope<Solution_> lastCompletedStepScope) {
        this.lastCompletedStepScope = lastCompletedStepScope;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public int getDepthSize() {
        return layerList.size();
    }

    public void registerPessimisticBound(Score pessimisticBound) {
        if (pessimisticBound.compareTo(bestPessimisticBound) > 0) {
            bestPessimisticBound = pessimisticBound;
            // Prune the queue
            // TODO optimize this because expandableNodeQueue is too long to iterate
            expandableNodeQueue.removeIf(node -> node.getOptimisticBound().compareTo(bestPessimisticBound) <= 0);
        }
    }

    public void addExpandableNode(ExhaustiveSearchNode moveNode) {
        expandableNodeQueue.add(moveNode);
        moveNode.setExpandable(true);
    }

}
