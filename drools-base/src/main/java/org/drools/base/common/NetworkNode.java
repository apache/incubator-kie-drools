package org.drools.base.common;

import java.io.Serializable;

import org.drools.base.reteoo.BaseTerminalNode;
import org.kie.api.definition.rule.Rule;

/**
 * Interface used to expose generic information on Rete nodes outside of he package. It is used
 * for exposing information events.
 */
public interface NetworkNode extends Serializable {

    /**
     * Returns the unique id that represents the node in the Rete network
     */
    int getId();

    /**
     * Returns the partition ID to which this node belongs to
     */
    RuleBasePartitionId getPartitionId();
    
    short getType();

    Rule[] getAssociatedRules();

    boolean isAssociatedWith( Rule rule );

    void addAssociatedTerminal(BaseTerminalNode terminalNode);
    void removeAssociatedTerminal(BaseTerminalNode terminalNode);

    int getAssociatedTerminalsSize();

    boolean hasAssociatedTerminal(BaseTerminalNode terminalNode);

    NetworkNode[] getSinks();

    default boolean isRightInputIsRiaNode() {
        // not ideal, but this was here to allow NetworkNode to be in drools-base
        return false;
    }
}
