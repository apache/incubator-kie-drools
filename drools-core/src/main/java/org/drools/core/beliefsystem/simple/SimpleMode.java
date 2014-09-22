package org.drools.core.beliefsystem.simple;

import org.drools.core.common.LogicalDependency;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SimpleMode extends LinkedListEntry<SimpleMode, LogicalDependency<SimpleMode>> implements Mode {

    public SimpleMode() {
    }

    public SimpleMode(LogicalDependency<SimpleMode> object) {
        super(object);
    }

    @Override
    public Object getBeliefSystem() {
        throw new UnsupportedOperationException("SimpleMode does support BeliefSystems");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // do not super() as it will be manually added into a List
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }
}
