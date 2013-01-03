package org.drools.planner.core.constructionheuristic.placer.entity;

import java.util.Iterator;

import org.drools.planner.core.constructionheuristic.placer.AbstractPlacer;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.drools.planner.core.constructionheuristic.placer.value.ValuePlacer;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.score.director.ScoreDirector;

public class QueuedEntityPlacer extends AbstractPlacer implements EntityPlacer {

    protected final EntitySelector entitySelector;
    protected final ValuePlacer valuePlacer;

    protected Iterator<Object> entityIterator = null;

    public QueuedEntityPlacer(EntitySelector entitySelector, ValuePlacer valuePlacer) {
        this.entitySelector = entitySelector;
        this.valuePlacer = valuePlacer;
        solverPhaseLifecycleSupport.addEventListener(entitySelector);
        solverPhaseLifecycleSupport.addEventListener(valuePlacer);
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
        // TODO isInitialized check must be inside ValuePlacer and variable specific
        while (entitySelector.getEntityDescriptor().isInitialized(entity)) {
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
