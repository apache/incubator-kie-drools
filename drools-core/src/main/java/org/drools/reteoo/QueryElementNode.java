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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.drools.base.DroolsQuery;
import org.drools.base.InternalViewChangedEventListener;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.QueryElementFactHandle;
import org.drools.core.util.RightTupleList;
import org.drools.reteoo.ReteooWorkingMemory.QueryInsertModifyAction;
import org.drools.reteoo.ReteooWorkingMemory.QueryEvaluationAction;
import org.drools.reteoo.ReteooWorkingMemory.QueryResultRetractAction;
import org.drools.reteoo.ReteooWorkingMemory.QueryResultUpdateAction;
import org.drools.reteoo.ReteooWorkingMemory.QueryRetractAction;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.EvalCondition;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.runtime.rule.Variable;
import org.drools.spi.PropagationContext;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class QueryElementNode extends LeftTupleSource
    implements
    LeftTupleSinkNode {

    private LeftTupleSource   tupleSource;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private QueryElement      queryElement;

    private boolean           tupleMemoryEnabled;
    
    private boolean           openQuery;

    public QueryElementNode() {
        // for serialization
    }
    
    public QueryElementNode(final int id,
                            final LeftTupleSource tupleSource,
                            final QueryElement queryElement,
                            final boolean tupleMemoryEnabled,
                            final boolean openQuery,
                            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.tupleSource = tupleSource;
        this.queryElement = queryElement;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
        this.openQuery = openQuery;
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        queryElement = (QueryElement) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        openQuery = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( queryElement );
        out.writeObject( tupleSource );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeBoolean( openQuery );
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
    
    public short getType() {
        return NodeTypeEnums.UnificationNode;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }
    
    
    public QueryElement getQueryElement() {
        return queryElement;
    }

    public boolean isOpenQuery() {
        return openQuery;
    }

    public void assertLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        Object[] argTemplate = this.queryElement.getArgTemplate(); // an array of declr, variable and literals
        Object[] args = new Object[argTemplate.length]; // the actual args, to be created from the  template

        // first copy everything, so that we get the literals. We will rewrite the declarations and variables next
        System.arraycopy( argTemplate,
                          0,
                          args,
                          0,
                          args.length );

        int[] declIndexes = this.queryElement.getDeclIndexes();

        List<Integer> srcVarIndexes = null;

        for ( int i = 0, length = declIndexes.length; i < length; i++ ) {
            Declaration declr = (Declaration) argTemplate[declIndexes[i]];

            Object tupleObject = leftTuple.get( declr ).getObject();

            Object o = declr.getValue( workingMemory,
                                       tupleObject );

            args[declIndexes[i]] = o;
        }

        int[] varIndexes = this.queryElement.getVariableIndexes();


        UnificationNodeViewChangedEventListener collector = new UnificationNodeViewChangedEventListener( leftTuple,
                                                                                                         varIndexes,
                                                                                                         this,
                                                                                                         this.tupleMemoryEnabled );

        DroolsQuery queryObject = new DroolsQuery( this.queryElement.getQueryName(),
                                                   args,
                                                   collector,
                                                   openQuery );       

        InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( queryObject,
                                                                                        null,
                                                                                        workingMemory,
                                                                                        null );
        
        collector.setFactHandle( handle );
        
        QueryInsertModifyAction action = new QueryInsertModifyAction(context, handle, leftTuple, varIndexes, srcVarIndexes, this);
        context.getQueue1().addFirst( action );                   
        
        leftTuple.setObject( handle ); // so it can be retracted later and destroyed
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        QueryRetractAction action = new QueryRetractAction(context, leftTuple, this);
        context.getQueue1().addFirst( action );                 
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        if ( !openQuery ) {
            // Was never open so execute as a retract + assert
            if ( leftTuple.getFirstChild() != null ) {
                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory );
            }
            assertLeftTuple( leftTuple, context, workingMemory );
            return;
        }
        Object[] argTemplate = this.queryElement.getArgTemplate(); // an array of declr, variable and literals
        Object[] args = new Object[argTemplate.length]; // the actual args, to be created from the  template

        // first copy everything, so that we get the literals. We will rewrite the declarations and variables next
        System.arraycopy( argTemplate,
                          0,
                          args,
                          0,
                          args.length );

        int[] declIndexes = this.queryElement.getDeclIndexes();

        List<Integer> srcVarIndexes = null;

        for ( int i = 0, length = declIndexes.length; i < length; i++ ) {
            Declaration declr = (Declaration) argTemplate[declIndexes[i]];

            Object tupleObject = leftTuple.get( declr ).getObject();

            Object o = declr.getValue( workingMemory,
                                       tupleObject );

            args[declIndexes[i]] = o;
        }

        int[] varIndexes = this.queryElement.getVariableIndexes();

        InternalFactHandle handle = ( InternalFactHandle ) leftTuple.getObject();
        DroolsQuery queryObject = (DroolsQuery) handle.getObject();
        queryObject.setParameters( args );
        ((UnificationNodeViewChangedEventListener)queryObject.getQueryResultCollector()).setVariables( varIndexes );
       
        QueryInsertModifyAction action = new QueryInsertModifyAction(context, handle, leftTuple, varIndexes, srcVarIndexes, this);
        context.getQueue1().addFirst( action );
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
        if ( leftTuple != null ) {
            leftTuple.reAdd(); //
            // LeftTuple previously existed, so continue as modify
            modifyLeftTuple( leftTuple,
                             context,
                             workingMemory );
        } else {
            // LeftTuple does not exist, so create and continue as assert
            assertLeftTuple( new LeftTupleImpl( factHandle,
                                                this,
                                                true ),
                             context,
                             workingMemory );
        }
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

    public static class UnificationNodeViewChangedEventListener
        implements
        InternalViewChangedEventListener {

        private LeftTuple          leftTuple;
        
        private QueryElementNode   node;
        
        private InternalFactHandle factHandle;

        private int[]              variables;

        private boolean            tupleMemoryEnabled;

        public UnificationNodeViewChangedEventListener(LeftTuple leftTuple,
                                                       int[] variables,
                                                       QueryElementNode node,
                                                       boolean tupleMemoryEnabled) {
            this.leftTuple = leftTuple;
            this.variables = variables;
            this.node = node;
            this.tupleMemoryEnabled = tupleMemoryEnabled;
        }

        public InternalFactHandle getFactHandle() {
            return factHandle;
        }

        public void setFactHandle(InternalFactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public void setVariables(int[] variables) {
            this.variables = variables;
        }

        public void rowAdded(final Rule rule,
                             LeftTuple resultLeftTuple,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {

            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getLeftTupleSink();
            Declaration[] decls = node.getDeclarations();           
            DroolsQuery query = (DroolsQuery) this.factHandle.getObject();
            Object[] objects = new Object[query.getElements().length];

            Declaration decl;
            for ( int i = 0, length = this.variables.length; i < length; i++ ) {
                decl = decls[this.variables[i]];
                objects[this.variables[i]] = decl.getValue( workingMemory,
                                                            resultLeftTuple.get( decl ).getObject() );
            }

            QueryElementFactHandle resultHandle = new QueryElementFactHandle( objects,
                                                                              workingMemory.getFactHandleFactory().getAtomicId().incrementAndGet(),
                                                                              workingMemory.getFactHandleFactory().getAtomicRecency().incrementAndGet() );
            RightTuple rightTuple = new RightTuple( resultHandle );
            if ( query.isOpen() ) {
                rightTuple.setLeftTuple( resultLeftTuple );
                resultLeftTuple.setBlocker( rightTuple );

            }                        

            this.node.getSinkPropagator().createChildLeftTuplesforQuery( this.leftTuple,
                                                                         rightTuple,
                                                                         true, // this must always be true, otherwise we can't 
                                                                               // find the child tuples to iterate for evaluating the query results
                                                                         query.isOpen() );
            
            RightTupleList rightTuples = query.getRightTupleList();
            if ( rightTuples == null ) {
                rightTuples = new RightTupleList();
                query.setRightTupleList( rightTuples );                
                QueryEvaluationAction evalAction = new QueryEvaluationAction( context,
                                                                              this.factHandle,
                                                                              leftTuple,
                                                                              this.node );
                context.getQueue2().addFirst( evalAction );
            }
            
            rightTuples.add(  rightTuple );
        }

        public void rowRemoved(final Rule rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = resultLeftTuple.getBlocker();
            rightTuple.setLeftTuple( null );
            resultLeftTuple.setBlocker( null );    
            
            DroolsQuery query = (DroolsQuery) this.factHandle.getObject();
            
            RightTupleList rightTuples = query.getRightTupleList();
            if ( rightTuples == null ) {
                rightTuples = new RightTupleList();
                query.setRightTupleList( rightTuples );                
                QueryResultRetractAction retractAction = new QueryResultRetractAction( context,
                                                                                       this.factHandle,
                                                                                       leftTuple,
                                                                                       this.node );
                context.getQueue2().addFirst( retractAction );
            }
            
            rightTuples.add(  rightTuple );            
        }

        public void rowUpdated(final Rule rule,
                               final LeftTuple resultLeftTuple,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            RightTuple rightTuple = resultLeftTuple.getBlocker();
            rightTuple.setLeftTuple( null );
            resultLeftTuple.setBlocker( null );            
       
            // We need to recopy everything back again, as we don't know what has or hasn't changed
            QueryTerminalNode node = (QueryTerminalNode) resultLeftTuple.getLeftTupleSink();
            Declaration[] decls = node.getDeclarations();
            InternalFactHandle rootHandle = resultLeftTuple.get( 0 );
            DroolsQuery query = (DroolsQuery) rootHandle.getObject();

            Object[] objects = new Object[query.getElements().length];

            Declaration decl;
            for ( int i = 0, length = this.variables.length; i < length; i++ ) {
                decl = decls[this.variables[i]];
                objects[this.variables[i]] = decl.getValue( workingMemory,
                                                            resultLeftTuple.get( decl ).getObject() );
            }

            QueryElementFactHandle handle = (QueryElementFactHandle)rightTuple.getFactHandle();
            
            handle.setRecency( workingMemory.getFactHandleFactory().getAtomicRecency().incrementAndGet() );
            handle.setObject( objects );
            
            if ( query.isOpen() ) {
                rightTuple.setLeftTuple( resultLeftTuple );
                resultLeftTuple.setBlocker( rightTuple );
            }

            this.node.getSinkPropagator().createChildLeftTuplesforQuery( this.leftTuple,
                                                                         rightTuple,
                                                                         true, // this must always be true, otherwise we can't 
                                                                               // find the child tuples to iterate for evaluating the query results
                                                                         query.isOpen() );
            
            RightTupleList rightTuples = query.getRightTupleList();
            if ( rightTuples == null ) {
                rightTuples = new RightTupleList();
                query.setRightTupleList( rightTuples );                
                QueryResultUpdateAction updateAction = new QueryResultUpdateAction( context,
                                                                                    this.factHandle,
                                                                                    leftTuple,
                                                                                    this.node );
                context.getQueue2().addFirst( updateAction );
            }
            
            rightTuples.add(  rightTuple );
        }

        public List< ? extends Object> getResults() {
            throw new UnsupportedOperationException( getClass().getCanonicalName() + " does not support the getResults() method." );
        }
    }
    
}
