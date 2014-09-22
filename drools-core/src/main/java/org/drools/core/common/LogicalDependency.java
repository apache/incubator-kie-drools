package org.drools.core.common;

import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.drools.core.spi.Activation;
import org.kie.internal.runtime.beliefs.Mode;

public interface LogicalDependency<T> extends LinkedListNode<LogicalDependency<T>> {

    public Object getJustified();

    public Activation getJustifier();

    public Object getObject();

    public T getMode();

}
