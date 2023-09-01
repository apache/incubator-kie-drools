package org.drools.tms.beliefsystem.jtms;

import org.drools.tms.beliefsystem.BeliefSystem;
import org.drools.tms.beliefsystem.ModedAssertion;
import org.drools.tms.LogicalDependency;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.base.beliefsystem.Mode;

public class JTMSMode<M extends JTMSMode<M>> extends AbstractBaseLinkedListNode<M> implements ModedAssertion<M> {
    private BeliefSystem<M> bs;
    private String value;
    private LogicalDependency<M> dep;
    private Mode nextMode;

    public JTMSMode(String value, BeliefSystem bs) {
        this.value = value;
        this.bs = bs;
    }

    public JTMSMode(String value, BeliefSystem bs,  Mode nextMode) {
        this.value = value;
        this.bs = bs;
        this.nextMode = nextMode;
    }

    @Override
    public BeliefSystem getBeliefSystem() {
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

    public Mode getNextMode() {
        return nextMode;
    }
}
