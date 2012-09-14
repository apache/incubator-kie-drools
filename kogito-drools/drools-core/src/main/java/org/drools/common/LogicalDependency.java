package org.drools.common;

import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.drools.spi.Activation;

public interface LogicalDependency extends LinkedListNode<LogicalDependency> {

    public LinkedListEntry<LogicalDependency> getJustifierEntry();

    public Object getJustified();

    public Activation getJustifier();

    public Object getValue();

}