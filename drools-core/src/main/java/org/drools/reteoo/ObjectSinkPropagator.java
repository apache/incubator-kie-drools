package org.drools.reteoo;

import java.io.Externalizable;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public interface ObjectSinkPropagator
    extends
    Externalizable {

    public RuleBasePartitionId getPartitionId();

    public void propagateAssertObject(InternalFactHandle factHandle,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

    public BaseNode getMatchingNode(BaseNode candidate);

    public ObjectSink[] getSinks();

    public int size();

    public void propagateModifyObject(InternalFactHandle factHandle,
                                      ModifyPreviousTuples modifyPreviousTuples,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory);

}
