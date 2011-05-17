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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.base.DroolsQuery;
import org.drools.base.InternalViewChangedEventListener;
import org.drools.base.extractors.ArrayElementReader;
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

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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
        List<Variable> trgVars = null;

        for ( int i = 0, length = declIndexes.length; i < length; i++ ) {
            Declaration declr = (Declaration) argTemplate[declIndexes[i]];

            Object tupleObject = leftTuple.get( declr ).getObject();

            if ( tupleObject instanceof DroolsQuery ) {
                ArrayElementReader reader = (ArrayElementReader) declr.getExtractor();
                DroolsQuery q = (DroolsQuery) tupleObject;
                Variable v = q.getVariables()[reader.getIndex()];

                // is that parameter an output Variable
                if ( v != null ) {
                    if ( !v.isSet() ) {
                        // it's not set yet, so we need to pass back any unified values

                        // If the declaration resolves to a variable being passed in, we need to add that to the variable indexes, so it's copied
                        if ( srcVarIndexes == null ) {
                            srcVarIndexes = new ArrayList<Integer>();
                            trgVars = new ArrayList<Variable>();
                        }
                        trgVars.add( v ); // this needs to be here, so we can pass the value back
                        srcVarIndexes.add( declIndexes[i] );

                        args[declIndexes[i]] = Variable.variable;
                        continue;
                    }
                }
            }

            Object o = declr.getValue( workingMemory,
                                       tupleObject );

            args[declIndexes[i]] = o;
        }

        int[] varIndexes = this.queryElement.getVariableIndexes();
        if ( srcVarIndexes != null ) {
            // we have Variable inputs to handle            
            // now merge the two, by adding new onto the end of the old
            int length = varIndexes.length;
            varIndexes = new int[varIndexes.length + srcVarIndexes.size()];
            System.arraycopy( this.queryElement.getVariableIndexes(),
                              0,
                              varIndexes,
                              0,
                              length );
            for ( int i = 0; i < srcVarIndexes.size(); i++ ) {
                varIndexes[i + length] = srcVarIndexes.get( i );
            }
        }

        UnificationNodeViewChangedEventListener collector = new UnificationNodeViewChangedEventListener( leftTuple,
                                                                                                         varIndexes,
                                                                                                         this.sink,
                                                                                                         this.tupleMemoryEnabled );

        DroolsQuery queryObject = new DroolsQuery( this.queryElement.getQueryName(),
                                                   args,
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
            int varsLength = this.queryElement.getVariableIndexes().length;
            if ( srcVarIndexes != null ) {
                QueryElementFactHandle qeh = (QueryElementFactHandle) childLeftTuple.getLastHandle();
                Object[] resultObjects = (Object[]) qeh.getObject();
                for ( int j = 0, i = varsLength; i < varIndexes.length; i++, j++ ) {
                    Variable v = trgVars.get( j );
                    v.setValue( resultObjects[i] );
                }
            }

            temp = childLeftTuple;
            this.sink.doPropagateAssertLeftTuple( context,
                                                  workingMemory,
                                                  childLeftTuple,
                                                  childLeftTuple.getLeftTupleSink() );
            if ( srcVarIndexes != null ) {
                QueryElementFactHandle qeh = (QueryElementFactHandle) childLeftTuple.getLastHandle();
                for ( int i = 0; i < trgVars.size(); i++ ) {
                    Variable v = trgVars.get( i );
                    v.unSet();
                }
            }

            childLeftTuple = childLeftTuple.getLeftParentNext();
            temp.setLeftParentNext( null );

        }
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
                                                       boolean tupleMemoryEnabled) {
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

            Variable[] vars = query.getVariables();
            Object[] objects = new Object[this.variables.length];

            for ( int i = 0, length = this.variables.length; i < length; i++ ) {
                objects[i] = vars[this.variables[i]].getValue();
            }

            QueryElementFactHandle handle = new QueryElementFactHandle( objects );
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
            throw new UnsupportedOperationException( getClass().getCanonicalName() + " does not support the getResults() method." );
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
        if ( leftTuple.firstChild != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        retractLeftTuple( leftTuple, context, workingMemory );
        assertLeftTuple( leftTuple, context, workingMemory );
        
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
        if ( leftTuple != null ) {
            leftTuple.reAdd(); //
            // LeftTuple previously existed, so retract it
            retractLeftTuple( leftTuple, context, workingMemory );
        } else {
            leftTuple = new LeftTuple( factHandle,
                                       this,
                                       this.tupleMemoryEnabled )  ;          
        }
        assertLeftTuple( leftTuple, context, workingMemory );
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
