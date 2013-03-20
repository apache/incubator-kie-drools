package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import org.optaplanner.core.impl.constructionheuristic.placer.Placer;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;

public interface EntityPlacer extends Placer {

    /**
     * @param stepScope never null
     * @return null if no more move can be nominated
     */
    ConstructionHeuristicMoveScope nominateMove(ConstructionHeuristicStepScope stepScope);

}
