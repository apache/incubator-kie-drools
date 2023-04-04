package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.AbstractNearbySelector;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;

abstract class AbstractNearbyValueSelector<Solution_, ChildSelector_ extends PhaseLifecycleListener<Solution_>, ReplayingSelector_ extends PhaseLifecycleListener<Solution_>>
        extends AbstractNearbySelector<Solution_, ChildSelector_, ReplayingSelector_>
        implements ValueSelector<Solution_> {

    protected AbstractNearbyValueSelector(ChildSelector_ childSelector, Object replayingSelector,
            NearbyDistanceMeter<?, ?> nearbyDistanceMeter, NearbyRandom nearbyRandom, boolean randomSelection) {
        super(childSelector, replayingSelector, nearbyDistanceMeter, nearbyRandom, randomSelection);
    }

}
