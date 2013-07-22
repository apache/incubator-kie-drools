package org.drools.core.common;

import org.drools.core.BeliefSystemType;
import org.drools.core.beliefsystem.BeliefSystem;

public interface BeliefSystemFactory {
    BeliefSystem createBeliefSystem(BeliefSystemType type, NamedEntryPoint ep, TruthMaintenanceSystem tms);
}
