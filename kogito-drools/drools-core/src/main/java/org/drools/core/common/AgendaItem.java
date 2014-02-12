package org.drools.core.common;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.event.rule.ActivationUnMatchListener;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public interface AgendaItem extends Activation {
    PropagationContext getPropagationContext();

    void setPropagationContext(PropagationContext context);

    RuleImpl getRule();

    Consequence getConsequence();

    LeftTuple getTuple();

    int getSalience();

    void setSalience(int salience);

    InternalFactHandle getFactHandle();

    void setFactHandle(InternalFactHandle factHandle);

    RuleAgendaItem getRuleAgendaItem();

    long getActivationNumber();

    void addBlocked(LogicalDependency dep);

    void removeAllBlockersAndBlocked(InternalAgenda agenda);

    void removeBlocked(LogicalDependency dep);

    LinkedList<LogicalDependency> getBlocked();

    void setBlocked(LinkedList<LogicalDependency> justified);

    LinkedList<LinkedListEntry<LogicalDependency>> getBlockers();

    void addLogicalDependency(LogicalDependency node);

    LinkedList<LogicalDependency> getLogicalDependencies();

    void setLogicalDependencies(LinkedList<LogicalDependency> justified);

    boolean isQueued();

    void setQueued(boolean queued);

    String toString();

    /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
    boolean equals(Object object);

    int hashCode();

    void setQueueIndex(int index);

    void dequeue();

    int getQueueIndex();

    void remove();

    ActivationGroupNode getActivationGroupNode();

    void setActivationGroupNode(ActivationGroupNode activationNode);

    InternalAgendaGroup getAgendaGroup();

    ActivationNode getActivationNode();

    void setActivationNode(ActivationNode activationNode);

    GroupElement getSubRule();

    TerminalNode getTerminalNode();

    ActivationUnMatchListener getActivationUnMatchListener();

    void setActivationUnMatchListener(ActivationUnMatchListener activationUnMatchListener);

    List<FactHandle> getFactHandles();

    String toExternalForm();

    List<Object> getObjects();

    Object getDeclarationValue(String variableName);

    List<String> getDeclarationIds();

    boolean isCanceled();

    void cancel();

    boolean isMatched();

    void setMatched(boolean matched);

    boolean isRuleAgendaItem();
}
