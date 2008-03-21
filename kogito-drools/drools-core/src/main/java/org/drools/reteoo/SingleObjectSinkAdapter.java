package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class SingleObjectSinkAdapter
    implements
    ObjectSinkPropagator,
    Externalizable {

    private static final long serialVersionUID = 873985743021L;

    private ObjectSink        sink;

    public SingleObjectSinkAdapter() {

    }

    public SingleObjectSinkAdapter(final ObjectSink sink) {
        this.sink = sink;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        sink = (ObjectSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( sink );
    }

    public void propagateAssertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.assertObject( factHandle,
                                context,
                                workingMemory );

    }

    public ObjectSink[] getSinks() {
        return new ObjectSink[]{this.sink};
    }

    public int size() {
        return 1;
    }

}
