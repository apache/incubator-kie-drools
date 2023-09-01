package org.drools.tms;

import org.drools.core.BeliefSystemType;
import org.drools.tms.beliefsystem.BeliefSystem;
import org.drools.tms.beliefsystem.defeasible.DefeasibleBeliefSystem;
import org.drools.tms.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.tms.beliefsystem.simple.SimpleBeliefSystem;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;

public class BeliefSystemFactory {

    public static BeliefSystem createBeliefSystem(BeliefSystemType type, InternalWorkingMemoryEntryPoint ep, TruthMaintenanceSystem tms) {
        switch(type) {
            case SIMPLE:
                return new SimpleBeliefSystem(ep, tms);
            case JTMS:
                return new JTMSBeliefSystem( ep, tms );
            case DEFEASIBLE:
                return new DefeasibleBeliefSystem( ep, tms );
        }
        throw new UnsupportedOperationException();
    }
}
