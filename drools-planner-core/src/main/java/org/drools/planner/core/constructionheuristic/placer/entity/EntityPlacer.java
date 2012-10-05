package org.drools.planner.core.constructionheuristic.placer.entity;

import org.drools.planner.core.constructionheuristic.placer.Placer;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicStepScope;

public interface EntityPlacer extends Placer {

    boolean hasPlacement();

    void doPlacement(ConstructionHeuristicStepScope stepScope);

}
