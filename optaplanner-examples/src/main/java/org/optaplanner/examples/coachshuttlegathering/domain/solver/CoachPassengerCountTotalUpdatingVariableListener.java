package org.optaplanner.examples.coachshuttlegathering.domain.solver;

import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;

public class CoachPassengerCountTotalUpdatingVariableListener extends BusPassengerCountTotalUpdatingVariableListener {

    @Override
    protected boolean isCorrectBusInstance(Bus bus) {
        return bus instanceof Coach;
    }
}
