package org.drools.core.common;

import org.drools.core.BeliefSystemType;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.defeasible.DefeasibleBeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleBeliefSystem;

import java.io.Serializable;

public class PhreakBeliefSystemFactory implements BeliefSystemFactory, Serializable {

    public BeliefSystem createBeliefSystem(BeliefSystemType type, NamedEntryPoint ep,
                                           TruthMaintenanceSystem tms) {
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
