/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.AlphaTerminalNode;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSinkNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNodeId;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.reteoo.RightTupleSink;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.kie.api.definition.rule.Rule;
import org.drools.base.reteoo.NodeTypeEnums;

/**
 * The base class for all Rete nodes.
 */
public abstract class BaseNode
    implements
        NetworkNode {

    protected int                        id;

    protected int                        memoryId = -1;

    protected RuleBasePartitionId partitionId;
    protected Set<Rule>                  associations;

    private Map<Integer, TerminalNode> associatedTerminals;

    private   boolean                    streamMode;

    protected int                        hashcode;

    public BaseNode() {

    }

    /**
     * All nodes have a unique id, set in the constructor.
     *
     * @param id
     *      The unique id
     */
    public BaseNode(final int id,
                    final RuleBasePartitionId partitionId) {
        super();
        this.id = id;
        this.partitionId = partitionId;
        this.associations = new HashSet<>();
        this.associatedTerminals = new HashMap<>();
    }

    /* (non-Javadoc)
     * @see org.kie.spi.ReteooNode#getId()
     */
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemoryId() {
        if (memoryId < 0) {
            throw new UnsupportedOperationException();
        }
        return memoryId;
    }

    protected void initMemoryId( BuildContext context ) {
        if (context != null && this instanceof MemoryFactory) {
            memoryId = context.getNextMemoryId();
        }
    }

    public boolean isStreamMode() {
        return this.streamMode;
    }

    protected void setStreamMode(boolean streamMode) {
        this.streamMode = streamMode;
    }

    /**
     * Attaches the node into the network. Usually to the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    public void attach(BuildContext context) {
        // do common shared code here, so it executes for all nodes
        doAttach(context);
        // do common shared code here, so it executes for all nodes
    }

    public void doAttach(BuildContext context) {

    }


    /**
     * A method that is called for all nodes whose network below them
     * changed, after the change is complete, providing them with an opportunity
     * for state update
     */
    public abstract void networkUpdated(UpdateContext updateContext);

    public boolean remove(RuleRemovalContext context,
                          ReteooBuilder builder) {
        boolean removed = doRemove( context, builder );
        if ( !this.isInUse() && !(NodeTypeEnums.EntryPointNode == getType()) ) {
            builder.releaseId(this);
        }
        return removed;
    }

    /**
     * Removes the node from the network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    protected abstract boolean doRemove(RuleRemovalContext context,
                                        ReteooBuilder builder);

    /**
     * Returns true in case the current node is in use (is referenced by any other node)
     */
    public abstract boolean isInUse();

    public abstract ObjectTypeNode getObjectTypeNode();

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "(" + this.id + ")]";
    }

    /**
     * Returns the partition ID for which this node belongs to
     */
    public RuleBasePartitionId getPartitionId() {
        return this.partitionId;
    }

    /**
     * Sets the partition this node belongs to
     */
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        this.partitionId = partitionId;
    }

    /**
     * Associates this node with the give rule
     */
    public void addAssociation( Rule rule ) {
        this.associations.add( rule );
    }

    public void addAssociation( BuildContext context, Rule rule ) {
        addAssociation( rule );
    }

    /**
     * Removes the association to the given rule from the
     * associations map.
     */
    public boolean removeAssociation( Rule rule, RuleRemovalContext context) {
        return this.associations.remove(rule);
    }

    public int getAssociationsSize() {
        return this.associations.size();
    }

    public Rule[] getAssociatedRules() {
        return this.associations.toArray( new Rule[this.associations.size()] );
    }

    public boolean isAssociatedWith( Rule rule ) {
        return this.associations.contains( rule );
    }

    @Override
    public void addAssociatedTerminal(BaseTerminalNode terminalNode) {
        associatedTerminals.put(terminalNode.getId(),(TerminalNode) terminalNode);
    }

    @Override
    public void removeAssociatedTerminal(BaseTerminalNode terminalNode) {
        associatedTerminals.remove(terminalNode.getId());
    }

    public int getAssociatedTerminalsSize() {
        return associatedTerminals.size();
    }

    public boolean hasAssociatedTerminal(BaseTerminalNode terminalNode) {
        return associatedTerminals.containsKey(terminalNode.getId());
    }

    @Override
    public final int hashCode() {
        return hashcode;
    }

    public NetworkNode[] getSinks() {
        Sink[] sinks = null;
        if (NodeTypeEnums.EntryPointNode == getType() ) {
            EntryPointNode source = (EntryPointNode) this;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (NodeTypeEnums.isObjectSource(this)) {
            ObjectSource source = (ObjectSource) this;
            sinks = source.getObjectSinkPropagator().getSinks();
        } else if (NodeTypeEnums.isLeftTupleSource(this)) {
            LeftTupleSource source = (LeftTupleSource) this;
            sinks = source.getSinkPropagator().getSinks();
        }
        return sinks;
    }


    public static LeftTupleNode getLeftTupleNode(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) s;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) s;
            case NodeTypeEnums.RightInputAdapterNode: return (RightInputAdapterNode) s;
            case NodeTypeEnums.LeftInputAdapterNode: return (LeftInputAdapterNode) s;
            case NodeTypeEnums.AlphaTerminalNode: return (AlphaTerminalNode) s;
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) s;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) s;
            case NodeTypeEnums.NotNode: return (NotNode) s;
            case NodeTypeEnums.FromNode: return (FromNode) s;
            case NodeTypeEnums.JoinNode: return (JoinNode) s;
            case NodeTypeEnums.EvalConditionNode: return (EvalConditionNode) s;
            case NodeTypeEnums.AsyncReceiveNode: return (AsyncReceiveNode) s;
            case NodeTypeEnums.AsyncSendNode: return (AsyncSendNode) s;
            case NodeTypeEnums.ReactiveFromNode: return (ReactiveFromNode) s;
            case NodeTypeEnums.ConditionalBranchNode: return (ConditionalBranchNode) s;
            case NodeTypeEnums.QueryElementNode: return (QueryElementNode) s;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) s;
            case NodeTypeEnums.MockBetaNode: return (LeftTupleNode) s;
            default:
                throw new UnsupportedOperationException("Cannot be cast to LeftTupleNode: " + s);
        }
    }

    public static RightTupleSink getRightTupleSink(RightTupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) s;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) s;
            case NodeTypeEnums.NotNode: return (NotNode) s;
            case NodeTypeEnums.JoinNode: return (JoinNode) s;
            case NodeTypeEnums.WindowNode: return (WindowNode) s;
            case NodeTypeEnums.MockBetaNode: return (RightTupleSink) s;
            case NodeTypeEnums.MockAlphaNode: return (RightTupleSink) s;
            default:
                throw new UnsupportedOperationException("Cannot be cast to RightTupleSink: " + s);
        }
    }

    public static LeftTupleSinkNode asLeftTupleSink(NetworkNode n) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        switch (((BaseNode) n).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) n;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) n;
            case NodeTypeEnums.RightInputAdapterNode: return (RightInputAdapterNode) n;
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) n;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) n;
            case NodeTypeEnums.NotNode: return (NotNode) n;
            case NodeTypeEnums.FromNode: return (FromNode) n;
            case NodeTypeEnums.JoinNode: return (JoinNode) n;
            case NodeTypeEnums.EvalConditionNode: return (EvalConditionNode) n;
            case NodeTypeEnums.AsyncReceiveNode: return (AsyncReceiveNode) n;
            case NodeTypeEnums.AsyncSendNode: return (AsyncSendNode) n;
            case NodeTypeEnums.ReactiveFromNode: return (ReactiveFromNode) n;
            case NodeTypeEnums.ConditionalBranchNode: return (ConditionalBranchNode) n;
            case NodeTypeEnums.QueryElementNode: return (QueryElementNode) n;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) n;
            case NodeTypeEnums.MockBetaNode: return (LeftTupleSinkNode) n;
            default:
                throw new UnsupportedOperationException("Cannot be cast to LeftTupleNode: " + n);
        }
    }

    public static NetworkNode checkcast(NetworkNode n) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        switch (((BaseNode)n).getType()) {
            case NodeTypeEnums.ObjectTypeNode : return (ObjectTypeNode) n;
            case NodeTypeEnums.AlphaNode : return (AlphaNode) n;
            case NodeTypeEnums.LeftInputAdapterNode: return (LeftInputAdapterNode) n;
            case NodeTypeEnums.AlphaTerminalNode: return (AlphaTerminalNode) n;
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) n;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) n;
            case NodeTypeEnums.RightInputAdapterNode: return (RightInputAdapterNode) n;
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) n;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) n;
            case NodeTypeEnums.NotNode: return (NotNode) n;
            case NodeTypeEnums.FromNode: return (FromNode) n;
            case NodeTypeEnums.JoinNode: return (JoinNode) n;
            case NodeTypeEnums.EvalConditionNode: return (EvalConditionNode) n;
            case NodeTypeEnums.AsyncReceiveNode: return (AsyncReceiveNode) n;
            case NodeTypeEnums.AsyncSendNode: return (AsyncSendNode) n;
            case NodeTypeEnums.ReactiveFromNode: return (ReactiveFromNode) n;
            case NodeTypeEnums.ConditionalBranchNode: return (ConditionalBranchNode) n;
            case NodeTypeEnums.QueryElementNode: return (QueryElementNode) n;
            case NodeTypeEnums.WindowNode: return (WindowNode) n;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) n;
            case NodeTypeEnums.MockBetaNode: return (NetworkNode) n;
            case NodeTypeEnums.MockAlphaNode: return (NetworkNode) n;
            default:
                throw new UnsupportedOperationException("Switch needs to include: " + n);
        }
    }

    public static NetworkNode getSink(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        return checkcast(t.getSink());
    }

    public static ObjectTypeNode getObjectTypeNode(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return ((RuleTerminalNode) s).getObjectTypeNode();
            case NodeTypeEnums.QueryTerminalNode: return ((QueryTerminalNode) s).getObjectTypeNode();
            case NodeTypeEnums.AccumulateNode : return ((AccumulateNode) s).getObjectTypeNode();
            case NodeTypeEnums.ExistsNode: return ((ExistsNode) s).getObjectTypeNode();
            case NodeTypeEnums.NotNode: return ((NotNode) s).getObjectTypeNode();
            case NodeTypeEnums.JoinNode: return ((JoinNode) s).getObjectTypeNode();
            default:
                throw new UnsupportedOperationException("Node does not have an ObjectType: " + s);
        }
    }

    public static ObjectTypeNodeId getLeftInputOtnId(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return ((RuleTerminalNode) s).getLeftInputOtnId();
            case NodeTypeEnums.QueryTerminalNode: return ((QueryTerminalNode) s).getLeftInputOtnId();
            case NodeTypeEnums.RightInputAdapterNode: return ((RightInputAdapterNode) s).getLeftInputOtnId();
            case NodeTypeEnums.AccumulateNode : return ((AccumulateNode) s).getLeftInputOtnId();
            case NodeTypeEnums.ExistsNode: return ((ExistsNode) s).getLeftInputOtnId();
            case NodeTypeEnums.NotNode: return ((NotNode) s).getLeftInputOtnId();
            case NodeTypeEnums.JoinNode: return ((JoinNode) s).getLeftInputOtnId();
            case NodeTypeEnums.FromNode: return ((FromNode) s).getLeftInputOtnId();
            case NodeTypeEnums.EvalConditionNode: return ((EvalConditionNode) s).getLeftInputOtnId();
            case NodeTypeEnums.AsyncReceiveNode: return ((AsyncReceiveNode) s).getLeftInputOtnId();
            case NodeTypeEnums.AsyncSendNode: return ((AsyncSendNode) s).getLeftInputOtnId();
            case NodeTypeEnums.ReactiveFromNode: return ((ReactiveFromNode) s).getLeftInputOtnId();
            case NodeTypeEnums.ConditionalBranchNode: return ((ConditionalBranchNode) s).getLeftInputOtnId();
            case NodeTypeEnums.QueryElementNode: return ((QueryElementNode) s).getLeftInputOtnId();
            case NodeTypeEnums.TimerConditionNode: return ((TimerNode) s).getLeftInputOtnId();
            case NodeTypeEnums.MockBetaNode: return ((LeftTupleSource)s).getLeftInputOtnId();
            default:
                throw new UnsupportedOperationException("Node does not have an LeftInputOtnId: " + s);
        }
    }

    public static ObjectTypeNodeId getRightInputOtnId(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.AccumulateNode : return ((AccumulateNode) s).getRightInputOtnId();
            case NodeTypeEnums.ExistsNode: return ((ExistsNode) s).getRightInputOtnId();
            case NodeTypeEnums.NotNode: return ((NotNode) s).getRightInputOtnId();
            case NodeTypeEnums.JoinNode: return ((JoinNode) s).getRightInputOtnId();
            case NodeTypeEnums.WindowNode: return ((WindowNode) s).getRightInputOtnId();
            default:
                throw new UnsupportedOperationException("Node does not have an RightInputOtnId: " + s);
        }
    }

    public static LeftTupleSource getLeftTupleSource(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        LeftTupleSource n;
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.RuleTerminalNode : n = ((RuleTerminalNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.QueryTerminalNode: n =  ((QueryTerminalNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.RightInputAdapterNode: n =  ((RightInputAdapterNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.AccumulateNode : n =  ((AccumulateNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.ExistsNode: n =  ((ExistsNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.NotNode: n =  ((NotNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.JoinNode: n =  ((JoinNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.FromNode: n =  ((FromNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.EvalConditionNode: n =  ((EvalConditionNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.AsyncReceiveNode: n =  ((AsyncReceiveNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.AsyncSendNode: n =  ((AsyncSendNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.ReactiveFromNode: n =  ((ReactiveFromNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.ConditionalBranchNode: n =  ((ConditionalBranchNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.QueryElementNode: n =  ((QueryElementNode) s).getLeftTupleSource(); break;
            case NodeTypeEnums.TimerConditionNode: n =  ((TimerNode) s).getLeftTupleSource(); break;
            default:
                throw new UnsupportedOperationException("Does not have a LeftTupleSource: " + s);
        }

        switch (n.getType()) {
            case NodeTypeEnums.LeftInputAdapterNode: return (LeftInputAdapterNode) n;
            case NodeTypeEnums.AlphaTerminalNode: return (AlphaTerminalNode) n;
            case NodeTypeEnums.AccumulateNode : return (AccumulateNode) n;
            case NodeTypeEnums.ExistsNode: return (ExistsNode) n;
            case NodeTypeEnums.NotNode: return (NotNode) n;
            case NodeTypeEnums.FromNode: return (FromNode) n;
            case NodeTypeEnums.JoinNode: return (JoinNode) n;
            case NodeTypeEnums.EvalConditionNode: return (EvalConditionNode) n;
            case NodeTypeEnums.AsyncReceiveNode: return (AsyncReceiveNode) n;
            case NodeTypeEnums.AsyncSendNode: return (AsyncSendNode) n;
            case NodeTypeEnums.ReactiveFromNode: return (ReactiveFromNode) n;
            case NodeTypeEnums.ConditionalBranchNode: return (ConditionalBranchNode) n;
            case NodeTypeEnums.QueryElementNode: return (QueryElementNode) n;
            case NodeTypeEnums.TimerConditionNode: return (TimerNode) n;
            default:
                throw new UnsupportedOperationException("Switch needs to include: " + n);
        }
    }

    public static ObjectSource getObjectSource(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        ObjectSource o;
        switch (((BaseNode) s).getType()) {
            case NodeTypeEnums.AccumulateNode : o = ((AccumulateNode) s).getRightInput(); break;
            case NodeTypeEnums.ExistsNode: o =  ((ExistsNode) s).getRightInput(); break;
            case NodeTypeEnums.NotNode: o =  ((NotNode) s).getRightInput(); break;
            case NodeTypeEnums.JoinNode: o =  ((JoinNode) s).getRightInput(); break;
            default:
                throw new UnsupportedOperationException("Node does not have an ObjectSource: " + s);
        }

        switch (o.getType()) {
            case NodeTypeEnums.AlphaNode : return (AlphaNode) o;
            case NodeTypeEnums.WindowNode: return (WindowNode) o;
            case NodeTypeEnums.ObjectTypeNode : return (ObjectTypeNode) o;
            case NodeTypeEnums.RightInputAdapterNode : return (RightInputAdapterNode) o;
            default:
                throw new UnsupportedOperationException("Node does match an ObjectSource: " + o);
        }
    }

    public static TerminalNode asTerminalNode(TupleImpl t) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        Sink s = t.getSink();
        switch (((BaseNode) t.getSink()).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) s;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) s;
            default: return null;
        }
    }

    public static TerminalNode asTerminalNode(NetworkNode n) {
        // It's better to always cast to a concrete or abstract class to avoid
        // secondary super cache problem. See https://issues.redhat.com/browse/DROOLS-7521
        switch (((BaseNode)n).getType()) {
            case NodeTypeEnums.RuleTerminalNode : return (RuleTerminalNode) n;
            case NodeTypeEnums.QueryTerminalNode: return (QueryTerminalNode) n;
            default: return null;
        }


    }
}
