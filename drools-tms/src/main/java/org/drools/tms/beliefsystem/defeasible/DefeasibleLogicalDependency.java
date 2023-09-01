package org.drools.tms.beliefsystem.defeasible;

import org.drools.tms.agenda.TruthMaintenanceSystemInternalMatch;
import org.drools.tms.beliefsystem.simple.SimpleLogicalDependency;

public class DefeasibleLogicalDependency<M extends DefeasibleMode<M>> extends SimpleLogicalDependency<M> {

    public DefeasibleLogicalDependency(TruthMaintenanceSystemInternalMatch<M> justifier, Object justified, Object object, M mode) {
        super(justifier, justified, object, mode);
    }

}
