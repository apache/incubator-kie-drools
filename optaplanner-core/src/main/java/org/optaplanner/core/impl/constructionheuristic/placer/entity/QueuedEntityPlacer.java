package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.constructionheuristic.placer.AbstractPlacer;
import org.optaplanner.core.impl.constructionheuristic.placer.value.ValuePlacer;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;

public class QueuedEntityPlacer extends AbstractPlacer implements EntityPlacer {

    protected final EntitySelector entitySelector;
    protected final List<ValuePlacer> valuePlacerList;

    protected Iterator<Object> entityIterator = null;

    protected Object nominatedEntity;
    protected Iterator<ValuePlacer> valuePlacerIterator;

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
        nominatedEntity = null;
        valuePlacerIterator = null;
    }

    public ConstructionHeuristicMoveScope nominateMove(ConstructionHeuristicStepScope stepScope) {
        ConstructionHeuristicMoveScope nominatedMoveScope = null;
        while (nominatedMoveScope == null) {
            if (valuePlacerIterator == null || !valuePlacerIterator.hasNext()) {
                if (!entityIterator.hasNext()) {
                    return null;
                }
                nominatedEntity = entityIterator.next();
                valuePlacerIterator = valuePlacerList.iterator();
            }
            stepScope.setEntity(nominatedEntity);
            ValuePlacer valuePlacer = valuePlacerIterator.next();
            nominatedMoveScope = valuePlacer.nominateMove(stepScope);
        }
        return nominatedMoveScope;
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        entityIterator = null;
        nominatedEntity = null;
        valuePlacerIterator = null;
    }

}
