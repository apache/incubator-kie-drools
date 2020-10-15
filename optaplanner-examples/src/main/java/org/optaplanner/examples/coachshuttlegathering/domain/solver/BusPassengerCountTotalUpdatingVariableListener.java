/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.coachshuttlegathering.domain.solver;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;

public abstract class BusPassengerCountTotalUpdatingVariableListener
        implements VariableListener<CoachShuttleGatheringSolution, Object> {

    @Override
    public void beforeEntityAdded(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, Object busStop) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, Object entity) {
        if (entity instanceof BusStop) {
            updateBusPassengerCount(scoreDirector, (BusStop) entity, true);
        }
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, Object entity) {
        if (entity instanceof BusStop) {
            updateBusPassengerCount(scoreDirector, (BusStop) entity, false);
        }
    }

    @Override
    public void afterVariableChanged(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, Object entity) {
        if (entity instanceof BusStop) {
            updateBusPassengerCount(scoreDirector, (BusStop) entity, true);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, Object entity) {
        if (entity instanceof BusStop) {
            updateBusPassengerCount(scoreDirector, (BusStop) entity, false);
        }
    }

    private void updateBusPassengerCount(ScoreDirector<CoachShuttleGatheringSolution> scoreDirector, BusStop busStop,
            boolean increase) {
        Bus bus = busStop.getBus();
        if (!isCorrectBusInstance(bus)) {
            return;
        }
        int difference = increase ? busStop.getPassengerQuantity() : -busStop.getPassengerQuantity();
        scoreDirector.beforeVariableChanged(bus, "passengerQuantityTotal");
        bus.setPassengerQuantityTotal(bus.getPassengerQuantityTotal() + difference);
        scoreDirector.afterVariableChanged(bus, "passengerQuantityTotal");
    }

    protected abstract boolean isCorrectBusInstance(Bus bus);
}
