package org.drools.planner.core.constructionheuristic.placer.entity;

import org.drools.planner.core.constructionheuristic.placer.Placer;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicStepScope;

public interface EntityPlacer extends Placer {

    /**
     * @param stepScope never null
     * @return null if no more move can be nominated
     */
    ConstructionHeuristicMoveScope nominateMove(ConstructionHeuristicStepScope stepScope);

}
