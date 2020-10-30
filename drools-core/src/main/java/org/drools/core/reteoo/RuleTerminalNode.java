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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.Map;

import org.drools.core.base.SalienceInteger;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.PhreakRuleTerminalNode;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.kie.api.definition.rule.Rule
 */
public class RuleTerminalNode extends AbstractTerminalNode {
    private static final long             serialVersionUID = 510l;

    /** The rule to invoke upon match. */
    protected RuleImpl                      rule;
    
    /**
     * the subrule reference is needed to resolve declarations
     * because declarations may have different offsets in each subrule
     */
    protected GroupElement                  subrule;
    protected int                           subruleIndex;
    protected Declaration[]                 allDeclarations;
    protected Declaration[]                 requiredDeclarations;

    protected Declaration[]                 salienceDeclarations;
    protected Declaration[]                 enabledDeclarations;

    protected LeftTupleSinkNode             previousTupleSinkNode;
    protected LeftTupleSinkNode             nextTupleSinkNode;

    protected boolean                       fireDirect;

    protected transient ObjectTypeNode.Id   leftInputOtnId;

    protected String                        consequenceName;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public RuleTerminalNode() {

    }

    public RuleTerminalNode(final int id,
                            final LeftTupleSource source,
                            final RuleImpl rule,
                            final GroupElement subrule,
                            final int subruleIndex,
                            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
               source,
               context );

        this.rule = rule;
        this.subrule = subrule;
        this.consequenceName = context.getConsequenceName();
        initDeclarations();

        this.subruleIndex = subruleIndex;

        setFireDirect( rule.getActivationListener().equals( "direct" ) );
        if ( isFireDirect() ) {
            rule.setSalience( new SalienceInteger(Integer.MAX_VALUE) );
        }

        setDeclarations( this.subrule.getOuterDeclarations() );

        initDeclaredMask(context);        
        initInferredMask();

        hashcode = calculateHashCode();
    }
    
    public void setDeclarations(Map<String, Declaration> decls) {
        setEnabledDeclarations( rule.findEnabledDeclarations( decls ) );
        setSalienceDeclarations( rule.findSalienceDeclarations( decls ) );
    }
    
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        rule = (RuleImpl) in.readObject();
        subrule = (GroupElement) in.readObject();
        subruleIndex = in.readInt();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();

        salienceDeclarations = ( Declaration[]) in.readObject();
        enabledDeclarations = ( Declaration[]) in.readObject();
        consequenceName = (String) in.readObject();

        fireDirect = rule.getActivationListener().equals( "direct" );

        initDeclarations();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeInt( subruleIndex );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );

        out.writeObject( salienceDeclarations );
        out.writeObject( enabledDeclarations );
        out.writeObject( consequenceName );
    }

    /**
     * Retrieve the <code>Action</code> associated with this node.
     *
     * @return The <code>Action</code> associated with this node.
     */
    public RuleImpl getRule() {
        return this.rule;
    }

    public GroupElement getSubRule() {
        return this.subrule;
    }


    public static PropagationContext findMostRecentPropagationContext(Tuple leftTuple, PropagationContext context) {
        // Find the most recent PropagationContext, as this caused this rule to elegible for firing
        Tuple lt = leftTuple;
        while ( lt != null ) {
            if ( lt.getPropagationContext() != null && lt.getPropagationContext().getPropagationNumber() > context.getPropagationNumber() ) {
                context = lt.getPropagationContext();
            }
            lt = lt.getParent();
        }
        return context;
    }



    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[RuleTerminalNode(").append(this.getId()).append("): rule=").append(this.rule.getName());
        if (consequenceName != null) {
            sb.append(", consequence=").append(consequenceName);
        }
        sb.append("]");
        return sb.toString();
    }

    public void attach( BuildContext context ) {
        getLeftTupleSource().addTupleSink(this, context);
        addAssociation( context, context.getRule() );
    }

    public Declaration[] getAllDeclarations() {
        return this.allDeclarations;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    private void initDeclarations() {
        Map<String, Declaration> decls = this.subrule.getOuterDeclarations();
        this.allDeclarations = decls.values().toArray( new Declaration[decls.size()] );

        String[] requiredDeclarationNames = rule.getRequiredDeclarationsForConsequence(getConsequenceName());
        this.requiredDeclarations = new Declaration[requiredDeclarationNames.length];
        int i = 0;
        for ( String str : requiredDeclarationNames ) {
            this.requiredDeclarations[i++] = decls.get( str );
        }
    }
    
    public Declaration[] getSalienceDeclarations() {
        return salienceDeclarations;
    }

    public void setSalienceDeclarations(Declaration[] salienceDeclarations) {
        this.salienceDeclarations = salienceDeclarations;
    }

    public Declaration[] getEnabledDeclarations() {
        return enabledDeclarations;
    }

    public void setEnabledDeclarations( Declaration[] enabledDeclarations ) {
        this.enabledDeclarations = enabledDeclarations;
    }

    public String getConsequenceName() {
        return consequenceName == null ? RuleImpl.DEFAULT_CONSEQUENCE_NAME : consequenceName;
    }

    public void cancelMatch(AgendaItem match, InternalWorkingMemoryActions workingMemory) {
        match.cancel();
        if ( match.isQueued() ) {
            Tuple leftTuple = match.getTuple();
            if ( match.getRuleAgendaItem() != null ) {
                // phreak must also remove the LT from the rule network evaluator
                if ( leftTuple.getMemory() != null ) {
                    leftTuple.getMemory().remove( leftTuple );
                }
            }
            RuleExecutor ruleExecutor = ((RuleTerminalNodeLeftTuple)leftTuple).getRuleAgendaItem().getRuleExecutor();
            PhreakRuleTerminalNode.doLeftDelete(ruleExecutor.getPathMemory().getActualAgenda( workingMemory ), ruleExecutor, leftTuple);
        }
    }


    public static class SortDeclarations
            implements
            Comparator<Declaration> {
        public final static SortDeclarations instance = new SortDeclarations();

        public int compare(Declaration d1,
                           Declaration d2) {
            return (d1.getIdentifier().compareTo( d2.getIdentifier() ));
        }
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    private int calculateHashCode() {
        return 31 * this.rule.hashCode() + (consequenceName == null ? 0 : 37 * consequenceName.hashCode());
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if ( object == null || !(object instanceof RuleTerminalNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }
        final RuleTerminalNode other = (RuleTerminalNode) object;
        return rule.equals(other.rule) && (consequenceName == null ? other.consequenceName == null : consequenceName.equals(other.consequenceName));
    }

    public short getType() {
        return NodeTypeEnums.RuleTerminalNode;
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new RuleTerminalNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple( factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple( leftTuple, sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new RuleTerminalNodeLeftTuple( leftTuple, rightTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        

    }      
    
    public ObjectTypeNode.Id getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }

    public boolean isFireDirect() {
        return fireDirect;
    }

    public void setFireDirect(boolean fireDirect) {
        this.fireDirect = fireDirect;
    }
}
