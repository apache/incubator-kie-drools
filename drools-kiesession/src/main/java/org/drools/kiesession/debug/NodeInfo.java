package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.definitions.rule.impl.RuleImpl;

import java.util.Collection;
import java.util.Set;

public interface NodeInfo {

    /**
     * Returns this node ID
     * @return
     */
    int getId();

    /**
     * Returns the partition ID this node belongs to
     * @return
     */
    RuleBasePartitionId getPartitionId();

    /**
     * Returns the set of rules this node belongs to
     * @return
     */
    Set<RuleImpl> getRules();

    /**
     * Returns the actual node
     * @return
     */
    NetworkNode getNode();
    
    /**
     * Returns the number of tuples in the tuple memory 
     * @return
     */
    long getTupleMemorySize();

    /**
     * Returns the number of facts in the fact memory
     * @return
     */
    long getFactMemorySize();

    /**
     * Returns the number of infered fact handles created in this node
     * @return
     */
    long getCreatedFactHandles();
    
    Collection<? extends NetworkNode> getSinkList();

}
