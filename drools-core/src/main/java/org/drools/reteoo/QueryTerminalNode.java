/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import org.drools.base.DroolsQuery;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.UpdateContext;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.drools.rule.Rule
 */
public final class QueryTerminalNode extends BaseNode
    implements
    LeftTupleSinkNode,
    TerminalNode {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;

    public static final short type             = 8;

    /** The rule to invoke upon match. */
    private Query             query;
    private GroupElement      subrule;
    private int               subruleIndex;    
    private LeftTupleSource   tupleSource;
    private Declaration[]     declarations; 

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;
    
    private long             declaredMask;
    private long             inferredMask;
    private long             negativeMask;
    
    private int              leftInputOtnId;    

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
                             final Rule rule,
                             final GroupElement subrule,
                             final int subruleIndex,                              
                             final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.query = (Query) rule;
        this.subrule = subrule;
        this.tupleSource = source;
        this.subruleIndex = subruleIndex;   
        
        initDeclaredMask(context);        
        initInferredMask();        
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        query = (Query) in.readObject();
        subrule = (GroupElement) in.readObject();
        subruleIndex = in.readInt();        
        tupleSource = (LeftTupleSource) in.readObject();
        declaredMask = in.readLong();
        inferredMask = in.readLong();        
        negativeMask = in.readLong();
        leftInputOtnId = in.readInt();        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( query );
        out.writeObject( subrule );
        out.writeInt(subruleIndex);
        out.writeObject( tupleSource );
        out.writeLong(declaredMask);
        out.writeLong(inferredMask);        
        out.writeLong(negativeMask);
        out.writeLong(leftInputOtnId);        
    }
    
    public void initDeclaredMask(BuildContext context) {  
        RuleTerminalNode.doInitDeclaredMask(this, context);
    }
    
    public void initInferredMask() {
        RuleTerminalNode.doInitInferredMask(this);
    }
    
    public long getDeclaredMask() {
        return declaredMask;
    }

    public long getInferredMask() {
        return inferredMask;
    }
    

    public Rule getRule() {
        return this.query;
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }      
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Assert a new <code>Tuple</code>.
     *
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        LeftTuple entry = leftTuple;

        // find the DroolsQuery object
        while ( entry.getParent() != null ) {
            entry = entry.getParent();
        }
        
        
        DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
        query.setQuery( this.query );

        // Add results to the adapter
        query.getQueryResultCollector().rowAdded( this.query,
                                                  leftTuple,
                                                  context,
                                                  workingMemory );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        LeftTuple entry = leftTuple;

        // find the DroolsQuery object
        while ( entry.getParent() != null ) {
            entry = entry.getParent();
        }
        DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
        query.setQuery( this.query );

        // Add results to the adapter
        query.getQueryResultCollector().rowRemoved( this.query,
                                                    leftTuple,
                                                    context,
                                                    workingMemory );
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSource.doMdifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory, 
                                         (LeftTupleSink) this, getLeftInputOtnId(), inferredMask );        
//        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
//        if ( leftTuple != null ) {
//            leftTuple.reAdd(); //
//            // LeftTuple previously existed, so continue as modify
//            modifyLeftTuple( leftTuple,
//                             context,
//                             workingMemory );
//        } else {
//            // LeftTuple does not exist, so create and continue as assert
//            assertLeftTuple( createLeftTuple( factHandle,
//                                              this,
//                                              true ),
//                             context,
//                             workingMemory );
//        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTuple entry = leftTuple;

        // find the DroolsQuery object
        while ( entry.getParent() != null ) {
            entry = entry.getParent();
        }
        DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
        query.setQuery( this.query );

        // Add results to the adapter
        query.getQueryResultCollector().rowUpdated( this.query,
                                                    leftTuple,
                                                    context,
                                                    workingMemory );
    }

    public String toString() {
        return "[QueryTerminalNode(" + this.getId() + "): query=" + this.query.getName() + "]";
    }

    public void ruleAttached() {

    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.tupleSource.networkUpdated(updateContext);
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
    }

    public boolean isInUse() {
        return false;
    }

    public void updateNewNode(final InternalWorkingMemory workingMemory,
                              final PropagationContext context) {
        // There are no child nodes to update, do nothing.
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        // do nothing, this can only ever be false
    }

    /**
     * @return the subrule
     */
    public GroupElement getSubrule() {
        return this.subrule;
    }
    
    public Declaration[] getDeclarations() {     
        if ( declarations == null ) {
            declarations = new Declaration[ query.getParameters().length ];
            Map<String, Declaration> declMap = subrule.getOuterDeclarations();
            int i = 0;
            for ( Declaration declr : query.getParameters() ) {
                declarations[i++] =  declMap.get( declr.getIdentifier() );
            }
        }
        return declarations;
    }
    
    public int getSubruleIndex() {
        return this.subruleIndex;
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

    public short getType() {
        return NodeTypeEnums.QueryTerminalNode;
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(leftTuple,sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }    
    
    
    public int getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(int leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }

    public LeftTupleSource unwrapTupleSource() {
        return tupleSource instanceof FromNode ? ((FromNode)tupleSource).getLeftTupleSource() : tupleSource;
    }
    
    public void setDeclaredMask(long mask) {
        declaredMask = mask;
    }

    public void setInferredMask(long mask) {
        inferredMask = mask;
    }      

    public long getNegativeMask() {
        return negativeMask;
    }
    
    public void setNegativeMask(long mask) {
        negativeMask = mask;
    }           

}
