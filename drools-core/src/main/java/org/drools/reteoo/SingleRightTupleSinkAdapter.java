package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class SingleRightTupleSinkAdapter
    implements
    RightTupleSinkPropagator,
    Externalizable {

    private static final long serialVersionUID = 873985743021L;

    private RightTupleSink    sink;

    public SingleRightTupleSinkAdapter() {

    }

    public SingleRightTupleSinkAdapter(final RightTupleSink sink) {
        this.sink = sink;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        sink = (RightTupleSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( sink );
    }

    public void propagateAssertFact(final InternalFactHandle factHandle,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
        this.sink.assertObject( factHandle,
                                    context,
                                    workingMemory );

    }

    public RightTupleSink[] getSinks() {
        return new RightTupleSink[]{this.sink};
    }

    public int size() {
        return 1;
    }

}
