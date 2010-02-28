package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class SingleObjectSinkAdapter extends AbstractObjectSinkAdapter {

    private static final long serialVersionUID = 873985743021L;

    protected ObjectSink      sink;

    public SingleObjectSinkAdapter() {
        super( null );
    }

    public SingleObjectSinkAdapter(final RuleBasePartitionId partitionId,
                                   final ObjectSink sink) {
        super( partitionId );
        this.sink = sink;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (ObjectSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
    }

    public void propagateAssertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        this.sink.assertObject( factHandle,
                                context,
                                workingMemory );
    }

    public void propagateModifyObject(InternalFactHandle factHandle,
                                            ModifyPreviousTuples modifyPreviousTuples,
                                            PropagationContext context,
                                            InternalWorkingMemory workingMemory) {
        this.sink.modifyObject( factHandle,
                                       modifyPreviousTuples,
                                       context,
                                       workingMemory );
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        if ( candidate.equals( sink ) ) {
            return (BaseNode) sink;
        }
        return null;
    }

    public ObjectSink[] getSinks() {
        return new ObjectSink[]{this.sink};
    }

    public int size() {
        return 1;
    }

}
