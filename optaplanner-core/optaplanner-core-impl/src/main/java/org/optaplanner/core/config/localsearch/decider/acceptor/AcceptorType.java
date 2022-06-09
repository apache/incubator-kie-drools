package org.optaplanner.core.config.localsearch.decider.acceptor;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum AcceptorType {
    HILL_CLIMBING,
    ENTITY_TABU,
    VALUE_TABU,
    MOVE_TABU,
    UNDO_MOVE_TABU,
    SIMULATED_ANNEALING,
    LATE_ACCEPTANCE,
    GREAT_DELUGE,
    STEP_COUNTING_HILL_CLIMBING
}
