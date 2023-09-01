package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.base.common.RuleBasePartitionId;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CompositeLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {
    private LeftTupleSinkNodeList sinks;

    private volatile LeftTupleSink[] sinkArray;

    public CompositeLeftTupleSinkAdapter() {
        super( RuleBasePartitionId.MAIN_PARTITION );
    }

    public CompositeLeftTupleSinkAdapter(final RuleBasePartitionId partitionId) {
        super( partitionId );
        this.sinks = new LeftTupleSinkNodeList();
    }

    public void addTupleSink(final LeftTupleSink sink) {
        this.sinks.add( (LeftTupleSinkNode) sink );
        sinkArray = null;
    }

    public void removeTupleSink(final LeftTupleSink sink) {
        this.sinks.remove( (LeftTupleSinkNode) sink );
        sinkArray = null;
    }
    
    public  LeftTupleSinkNodeList getRawSinks() {
        return sinks;
    }
    
    public BaseNode getMatchingNode(BaseNode candidate) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            if ( candidate.equals( sink ) ) {
                return (BaseNode) sink;
            }
        }
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
    
    public LeftTupleSink[] getSinks() {
        if ( sinkArray != null ) {
            return sinkArray;
        }

        LeftTupleSink[] sinks = new LeftTupleSink[this.sinks.size()];

        int i = 0;
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sinks[i++] = sink;
        }

        this.sinkArray = sinks;
        return sinks;
    }
    
    public LeftTupleSinkNode getFirstLeftTupleSink() {
        return this.sinks.getFirst();
    }

    public LeftTupleSinkNode getLastLeftTupleSink() {
        return this.sinks.getLast();
    }

    public int size() {
        return this.sinks.size();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.sinks = (LeftTupleSinkNodeList) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.sinks );
    }
}
