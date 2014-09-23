package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.runtime.beliefs.Mode;

public class DefeasibleLogicalDependency<M extends DefeasibleMode<M>> extends SimpleLogicalDependency<M> {

    public DefeasibleLogicalDependency(Activation<M> justifier, Object justified, Object object, M mode) {
        super(justifier, justified, object, mode);
    }

}
