package org.drools.util.debug;

import java.util.Collection;
import java.util.Set;

import org.drools.common.NetworkNode;
import org.drools.common.RuleBasePartitionId;
import org.drools.rule.Rule;

public interface NodeInfo {

    /**
     * Returns this node ID
     * @return
     */
    public int getId();

    /**
     * Returns the partition ID this node belongs to
     * @return
     */
    public RuleBasePartitionId getPartitionId();

    /**
     * Returns the set of rules this node belongs to
     * @return
     */
    public Set<Rule> getRules();

    /**
     * Returns the actual node
     * @return
     */
    public NetworkNode getNode();
    
    /**
     * Returns true if memory is enabled for this node
     * @return
     */
    public boolean isMemoryEnabled();

    /**
     * Returns the number of tuples in the tuple memory 
     * @return
     */
    public long getTupleMemorySize();

    /**
     * Returns the number of facts in the fact memory
     * @return
     */
    public long getFactMemorySize();

    /**
     * Returns the number of infered fact handles created in this node
     * @return
     */
    public long getCreatedFactHandles();
    
    public Collection<? extends NetworkNode> getSinkList();

}
