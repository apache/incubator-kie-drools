package org.drools.core.common;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.drools.core.spi.Activation;
import org.kie.internal.runtime.beliefs.Mode;

public interface LogicalDependency<M extends ModedAssertion<M>> extends LinkedListNode<LogicalDependency<M>> {

    public Object getJustified();

    public Activation<M> getJustifier();

    public Object getObject();

    public M getMode();

}
