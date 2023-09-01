package org.drools.tms;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.tms.beliefsystem.BeliefSystem;
import org.drools.base.beliefsystem.Mode;
import org.drools.core.util.LinkedListEntry;
import org.drools.tms.beliefsystem.ModedAssertion;

public class SimpleMode extends LinkedListEntry<SimpleMode, LogicalDependency<SimpleMode>>
       implements ModedAssertion<SimpleMode> {

    public SimpleMode() {
    }

    public SimpleMode(LogicalDependency<SimpleMode> object) {
        super(object);
    }

    @Override
    public BeliefSystem getBeliefSystem() {
        throw new UnsupportedOperationException("SimpleMode does support BeliefSystems");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // do not super() as it will be manually added into a List
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public Mode getNextMode() {
        return null;
    }
}
