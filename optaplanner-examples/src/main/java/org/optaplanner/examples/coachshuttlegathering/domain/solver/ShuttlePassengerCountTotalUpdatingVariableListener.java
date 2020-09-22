package org.optaplanner.examples.coachshuttlegathering.domain.solver;

import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;

public class ShuttlePassengerCountTotalUpdatingVariableListener extends BusPassengerCountTotalUpdatingVariableListener {

    @Override
    protected boolean isCorrectBusInstance(Bus bus) {
        return bus instanceof Shuttle;
    }
}
