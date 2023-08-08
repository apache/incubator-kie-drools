/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.builder.BuildContext;

import java.util.Map;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.kie.api.definition.rule.Rule
 */
public class QueryTerminalNode extends AbstractTerminalNode implements LeftTupleSinkNode {



    private static final long serialVersionUID = 510l;

    public static final short type             = 8;
    
    private transient ObjectTypeNode.Id leftInputOtnId;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public QueryTerminalNode() {
    }

    /**
     * Constructor
     *
     * @param id node ID
     * @param source the tuple source for this node
     * @param rule the rule this node belongs to
     * @param subrule the subrule this node belongs to
     * @param context the current build context
     */
    public QueryTerminalNode(final int id,
                             final LeftTupleSource source,
                             final RuleImpl rule,
                             final GroupElement subrule,
                             final int subruleIndex,                              
                             final BuildContext context) {
        super( id,
               context.getPartitionId(),
               source,
               context,
               rule, subrule, subruleIndex);
        this.hashcode = calculateHashCode();
    }

    public QueryImpl getQuery() {
        return (QueryImpl) getRule();
    }

    public String toString() {
        return "[QueryTerminalNode(" + this.getId() + "): query=" + this.getQuery().getName() + "]";
    }

    @Override
    public boolean isFireDirect() {
        return false;
    }

    void initDeclarations(Map<String, Declaration> decls, final BuildContext context) {
        this.requiredDeclarations = new Declaration[ getQuery().getParameters().length ];
        int i = 0;
        for ( Declaration declr : getQuery().getParameters() ) {
            this.requiredDeclarations[i++] =  decls.get( declr.getIdentifier() );
        }
    }

    public short getType() {
        return NodeTypeEnums.QueryTerminalNode;
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return AgendaComponentFactory.get().createTerminalTuple(factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return AgendaComponentFactory.get().createTerminalTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return AgendaComponentFactory.get().createTerminalTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return AgendaComponentFactory.get().createTerminalTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return AgendaComponentFactory.get().createTerminalTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }    

    public ObjectTypeNode.Id getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }

    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }

    @Override
    public Declaration[] getSalienceDeclarations() {
        throw new UnsupportedOperationException();
    }

    public void doAttach( BuildContext context ) {
        super.doAttach(context);
        getLeftTupleSource().addTupleSink( this, context );
        addAssociation( context, context.getRule() );
    }
}
