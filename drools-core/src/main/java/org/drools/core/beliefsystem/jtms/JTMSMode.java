package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.defeasible.DefeasibleLogicalDependency;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.runtime.beliefs.Mode;

public class JTMSMode extends AbstractBaseLinkedListNode<JTMSMode> implements Mode {
    private BeliefSystem bs;
    private String value;
    private LogicalDependency dep;

    public JTMSMode(String value, BeliefSystem bs) {
        this.value = value;
        this.bs = bs;
    }

    @Override
    public Object getBeliefSystem() {
        return bs;
    }

    public String getValue() {
        return value;
    }

    public LogicalDependency getLogicalDependency() {
        return dep;
    }

    public void setLogicalDependency(LogicalDependency dep) {
        this.dep = dep;
    }
}
