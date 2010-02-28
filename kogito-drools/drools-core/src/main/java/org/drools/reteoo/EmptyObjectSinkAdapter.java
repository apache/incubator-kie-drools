package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class EmptyObjectSinkAdapter extends AbstractObjectSinkAdapter {

    private static final long                   serialVersionUID = -631743913176779720L;

    private static final EmptyObjectSinkAdapter INSTANCE = new EmptyObjectSinkAdapter();

    private static final ObjectSink[]           SINK_LIST        = new ObjectSink[0];

    public static EmptyObjectSinkAdapter getInstance() {
        return INSTANCE;
    }

    public EmptyObjectSinkAdapter() {
        super( RuleBasePartitionId.MAIN_PARTITION );
    }

    public EmptyObjectSinkAdapter( RuleBasePartitionId partitionId ) {
        super( partitionId );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
    }

    public RuleBasePartitionId getPartitionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void propagateAssertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {

    }

    public void propagateRetractObject(final InternalFactHandle handle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory,
                                       final boolean useHash) {
    }
    
    public void propagateModifyObject(InternalFactHandle factHandle,
                                      ModifyPreviousTuples modifyPreviousTuples,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {

    }    

    public BaseNode getMatchingNode(BaseNode candidate) {
        return null;
    }

    public ObjectSink[] getSinks() {
        return SINK_LIST;
    }

    public int size() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof EmptyObjectSinkAdapter;
    }



}
