package org.optaplanner.core.impl.constructionheuristic.placer;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;

public interface EntityPlacer<Solution_> extends Iterable<Placement<Solution_>>, PhaseLifecycleListener<Solution_> {

}
