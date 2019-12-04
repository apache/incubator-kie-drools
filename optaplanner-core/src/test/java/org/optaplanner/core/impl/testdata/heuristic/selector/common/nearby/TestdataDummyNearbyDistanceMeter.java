package org.optaplanner.core.impl.testdata.heuristic.selector.common.nearby;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

public class TestdataDummyNearbyDistanceMeter implements NearbyDistanceMeter {

    @Override
    public double getNearbyDistance(Object origin, Object destination) {
        return 0;
    }
}
