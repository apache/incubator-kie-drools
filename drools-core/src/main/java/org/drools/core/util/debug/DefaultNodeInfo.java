package org.drools.core.util.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.common.NetworkNode;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.Rete;
import org.drools.rule.Rule;

public class DefaultNodeInfo
    implements
    NodeInfo {

    private NetworkNode node;
    private Set<Rule>   rules;

    private boolean memoryEnabled = false;
    private long tupleMemorySize = -1;
    private long factMemorySize = -1;
    private long createdFactHandles = -1;
    private long actionQueueSize = -1;

    public DefaultNodeInfo(NetworkNode node) {
        this.node = node;
        this.rules = new HashSet<Rule>();
    }

    public void assign(Rule rule) {
        this.rules.add( rule );
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public int getId() {
        return node.getId();
    }

    public RuleBasePartitionId getPartitionId() {
        return node.getPartitionId();
    }

    public long getTupleMemorySize() {
        return tupleMemorySize;
    }

    public void setTupleMemorySize(long leftMemorySize) {
        this.tupleMemorySize = leftMemorySize;
    }

    public long getFactMemorySize() {
        return factMemorySize;
    }

    public void setFactMemorySize(long rightMemorySize) {
        this.factMemorySize = rightMemorySize;
    }

    public long getCreatedFactHandles() {
        return createdFactHandles;
    }

    public void setCreatedFactHandles(long createdFactHandles) {
        this.createdFactHandles = createdFactHandles;
    }

    public long getActionQueueSize() {
        return this.actionQueueSize;
    }
    
    public void setActionQueueSize(long size) {
        this.actionQueueSize = size;
    }

    public NetworkNode getNode() {
        return node;
    }

    public boolean isMemoryEnabled() {
        return memoryEnabled;
    }

    public void setMemoryEnabled(boolean memoryEnabled) {
        this.memoryEnabled = memoryEnabled;
    }
    
    public Collection<? extends NetworkNode> getSinkList() {
        if ( node instanceof Rete ) {
            Rete rete = (Rete) node;
            return  rete.getEntryPointNodes().values();
        } else if ( node instanceof EntryPointNode ) {
            EntryPointNode epn = (EntryPointNode) node;
            return epn.getObjectTypeNodes().values();
        } else if ( node instanceof ObjectSource ) {
            List<NetworkNode> result = new ArrayList<NetworkNode>();
            for ( ObjectSink sink : ((ObjectSource)node).getSinkPropagator().getSinks() ) {
                result.add( (NetworkNode) sink );
            }
            return result;
        } else if ( node instanceof LeftTupleSource ) {
            List<NetworkNode> result = new ArrayList<NetworkNode>();
            LeftTupleSource source = (LeftTupleSource) node;
            for ( LeftTupleSink sink : source.getSinkPropagator().getSinks() ) {
                result.add( (NetworkNode) sink );
            }
            return result;
        }
        return Collections.emptyList();
    }

}
