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
        stepScope.setEntity(entity);

        // TODO add uninitialized entities immediately and remove logic in SolutionDescriptor.getAllFacts()
        ScoreDirector scoreDirector = stepScope.getScoreDirector();
        // TODO FIXME entity has already been added if another variable is already initialized
        scoreDirector.beforeEntityAdded(entity);
        scoreDirector.afterEntityAdded(entity);

        valuePlacer.doPlacement(stepScope);
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        entityIterator = null;
    }

}
