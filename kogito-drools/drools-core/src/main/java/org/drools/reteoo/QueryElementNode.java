/*
 * Copyright 2010 JBoss Inc
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

import java.util.List;

import org.drools.base.DroolsQuery;
import org.drools.base.InternalViewChangedEventListener;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.QueryElementFactHandle;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.rule.Variable;
import org.drools.spi.PropagationContext;

public class QueryElementNode extends LeftTupleSource
    implements
    LeftTupleSinkNode {

    private LeftTupleSource   tupleSource;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private QueryElement      queryElement;

    private boolean           tupleMemoryEnabled;

    public QueryElementNode(final int id,
                            final LeftTupleSource tupleSource,
                            final QueryElement queryElement,
                            final boolean tupleMemoryEnabled,
                            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.tupleSource = tupleSource;
        this.queryElement = queryElement;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    public void updateSink(LeftTupleSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        // do nothing as we have no left memory
    }

    protected void doRemove(RuleRemovalContext context,
                            ReteooBuilder builder,
                            BaseNode node,
                            InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }

        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(InternalWorkingMemory[] workingMemories) {
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

    public void networkUpdated() {
        this.tupleSource.networkUpdated();
    }

    public void assertLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        Object[] arguments = this.queryElement.getArguments();
        Object[] inputArgs = new Object[arguments.length];

        System.arraycopy( arguments,
                          0,
                          inputArgs,
                          0,
                          inputArgs.length );

        int[] declIndexes = this.queryElement.getDeclIndexes();

        for ( int i = 0, length = declIndexes.length; i < length; i++ ) {
            Declaration declr = (Declaration) arguments[declIndexes[i]];
            inputArgs[declIndexes[i]] = declr.getValue( workingMemory,
                                           leftTuple.get( declr ).getObject() );
        }

        UnificationNodeViewChangedEventListener collector = new UnificationNodeViewChangedEventListener( leftTuple,
                                                                                                   this.queryElement.getVariables(),
                                                                                                   this.sink,
                                                                                                   this.tupleMemoryEnabled );
        
        DroolsQuery queryObject = new DroolsQuery( this.queryElement.getQueryName(),
                                                   inputArgs,
                                                   collector,
                                                   false );
        collector.setDroolsQuery( queryObject );

        InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( queryObject,
                                                                                        workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
                                                                                                                                                              queryObject ),
                                                                                        workingMemory,
                                                                                        null );

        workingMemory.insert( handle,
                              queryObject,
                              null,
                              null,
                              workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( workingMemory.getEntryPoint(),
                                                                                                    queryObject ) );

        workingMemory.getFactHandleFactory().destroyFactHandle( handle );
        
        LeftTuple childLeftTuple = leftTuple.firstChild;
        LeftTuple temp = null;
        while ( childLeftTuple != null ) {
            temp = childLeftTuple;
            this.sink.doPropagateAssertLeftTuple( context, workingMemory, childLeftTuple, childLeftTuple.getLeftTupleSink() );
            childLeftTuple = childLeftTuple.getLeftParentNext();
            temp.setLeftParentNext( null );
        }
        leftTuple.firstChild = null;

    }

    public static class UnificationNodeViewChangedEventListener
        implements
        InternalViewChangedEventListener {

        private LeftTuple                 leftTuple;
        protected LeftTupleSinkPropagator sink;

        private DroolsQuery               query;
        private int[]                     variables;
        
        private boolean                   tupleMemoryEnabled;

        public UnificationNodeViewChangedEventListener(LeftTuple leftTuple,
                                                    int[] variables,
                                                    LeftTupleSinkPropagator sink,
                                                    boolean                   tupleMemoryEnabled) {
            this.leftTuple = leftTuple;
            this.variables = variables;
            this.sink = sink;
            this.tupleMemoryEnabled = tupleMemoryEnabled;
        }

        public void setDroolsQuery(DroolsQuery query) {
            this.query = query;
        }

        public void rowAdded(final Rule rule,
                             LeftTuple resultLeftTuple,
                        PropagationContext context,
                        InternalWorkingMemory workingMemory) {

            Object[] args = query.getElements();
            Object[] objects = new Object[this.variables.length];

            for ( int i = 0, length = this.variables.length; i < length; i++ ) {
                objects[i] = ((Variable) args[ this.variables[i]] ).getValue();
            }

            QueryElementFactHandle handle = new QueryElementFactHandle(objects );
            RightTuple rightTuple = new RightTuple( handle );
            
            this.sink.createChildLeftTuplesforQuery( this.leftTuple, 
                                                     rightTuple, 
                                                     this.tupleMemoryEnabled );
        }
        
        public void rowRemoved(final Rule rule,
                               final LeftTuple tuple,
                final PropagationContext context,
                final InternalWorkingMemory workingMemory) {
            //TODO
        }
        
        public void rowUpdated(final Rule rule,
                               final LeftTuple tuple,
                final PropagationContext context,
                final InternalWorkingMemory workingMemory) {
            //TODO        	
        }

        public List< ? extends Object> getResults() {
            throw new UnsupportedOperationException(getClass().getCanonicalName()+" does not support the getResults() method.");
        }
    }

    public short getType() {
        return NodeTypeEnums.UnificationNode;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
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

}
