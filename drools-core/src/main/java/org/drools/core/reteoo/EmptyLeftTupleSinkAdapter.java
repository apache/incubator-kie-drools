package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.base.common.RuleBasePartitionId;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EmptyLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {

    private static final EmptyLeftTupleSinkAdapter instance = new EmptyLeftTupleSinkAdapter();

    private static final LeftTupleSink[] sinks = new LeftTupleSink[]{};

    public static final EmptyLeftTupleSinkAdapter getInstance() {
        return instance;
    }

    public EmptyLeftTupleSinkAdapter() {
        super( RuleBasePartitionId.MAIN_PARTITION );
        // constructor needed for serialisation
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        return null;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public LeftTupleSink[] getSinks() {
        return sinks;
    }
    
    public LeftTupleSinkNode getFirstLeftTupleSink() {
        return null;
    }

    public LeftTupleSinkNode getLastLeftTupleSink() {
        return null;
    }

    public int size() {
        return 0;
    }
}
