package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.defeasible.DefeasibleLogicalDependency;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.runtime.beliefs.Mode;

public class JTMSMode<M extends JTMSMode<M>> extends AbstractBaseLinkedListNode<M> implements ModedAssertion<M> {
    private BeliefSystem<M> bs;
    private String value;
    private LogicalDependency<M> dep;

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

    public LogicalDependency<M> getLogicalDependency() {
        return dep;
    }

    public void setLogicalDependency(LogicalDependency<M> dep) {
        this.dep = dep;
    }
}
