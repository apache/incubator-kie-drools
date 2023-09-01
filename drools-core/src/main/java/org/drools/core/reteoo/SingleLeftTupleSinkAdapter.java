package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.BaseNode;
import org.drools.base.common.RuleBasePartitionId;

public class SingleLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {
    protected LeftTupleSink sink;
    
    private LeftTupleSink[] sinkArray;

    public SingleLeftTupleSinkAdapter() {
        this( RuleBasePartitionId.MAIN_PARTITION,
              null );
    }

    public SingleLeftTupleSinkAdapter(final RuleBasePartitionId partitionId,
                                      final LeftTupleSink sink) {
        super( partitionId );
        this.sink = sink;
        this.sinkArray = new LeftTupleSink[]{this.sink};
    }
    
    public BaseNode getMatchingNode(BaseNode candidate) {
        if ( sink.equals( candidate ) ) {
            return (BaseNode) sink;
        }
        return null;
    }

    public LeftTupleSink[] getSinks() {
        return sinkArray;
    }

    // See LeftTuple.getTupleSink() or https://issues.redhat.com/browse/DROOLS-7521
    public LeftTupleSinkNode getFirstLeftTupleSink() {
        if (sink instanceof AccumulateNode) {
            return (AccumulateNode) sink;
        } else if (sink instanceof RuleTerminalNode) {
            return (RuleTerminalNode) sink;
        } else if (sink instanceof RightInputAdapterNode) {
            return (RightInputAdapterNode) sink;
        } else if (sink instanceof ExistsNode) {
            return (ExistsNode) sink;
        }
        return (LeftTupleSinkNode) sink;
    }

    public LeftTupleSinkNode getLastLeftTupleSink() {
        return ( LeftTupleSinkNode ) sink;
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.sink = (LeftTupleSink) in.readObject();
        this.sinkArray = new LeftTupleSink[]{this.sink};
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.sink );
    }
}
