package org.drools.planner.core.constructionheuristic.placer.entity;

import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.constructionheuristic.placer.AbstractPlacer;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.drools.planner.core.constructionheuristic.placer.value.ValuePlacer;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.score.director.ScoreDirector;

public class QueuedEntityPlacer extends AbstractPlacer implements EntityPlacer {

    protected final EntitySelector entitySelector;
    protected final List<ValuePlacer> valuePlacerList;

    protected Iterator<Object> entityIterator = null;

    public QueuedEntityPlacer(EntitySelector entitySelector, List<ValuePlacer> valuePlacerList) {
        this.entitySelector = entitySelector;
        this.valuePlacerList = valuePlacerList;
        if (valuePlacerList.isEmpty()) {
            throw new IllegalArgumentException("The placer (" + this
                    + ") with valuePlacerList (" + valuePlacerList + ") must have at least 1 valuePlacer.");
        }
        solverPhaseLifecycleSupport.addEventListener(entitySelector);
        for (ValuePlacer valuePlacer : valuePlacerList) {
            solverPhaseLifecycleSupport.addEventListener(valuePlacer);
        }
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        entityIterator = entitySelector.iterator();
    }

    public boolean hasPlacement() {
        // If a valuePlacer has an empty valueSelector, a move to value null will win
        return entityIterator.hasNext();
    }

    public void doPlacement(ConstructionHeuristicStepScope stepScope) {
        Object entity = entityIterator.next();
        // start HACK
        // TODO isInitialized check must be inside ValuePlacer
        while (valuePlacer.getVariableDescriptor().isInitialized(entity)) {
            if (!entityIterator.hasNext()) {
                return;
            }
            entity = entityIterator.next();
        }
        // end HACK
        stepScope.setEntity(entity);
        valuePlacer.doPlacement(stepScope);
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        entityIterator = null;
    }

}
