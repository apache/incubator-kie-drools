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
