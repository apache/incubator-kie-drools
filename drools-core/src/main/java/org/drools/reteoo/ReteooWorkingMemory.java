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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.SessionConfiguration;
import org.drools.base.DroolsQuery;
import org.drools.base.InternalViewChangedEventListener;
import org.drools.base.NonCloningQueryViewListener;
import org.drools.base.QueryRowWithSubruleIndex;
import org.drools.base.StandardQueryViewChangedEventListener;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.BaseNode;
import org.drools.common.DefaultAgenda;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.QueryElementFactHandle;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.FastIterator;
import org.drools.core.util.RightTupleList;
import org.drools.definition.rule.Query;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.QueryElementNode.UnificationNodeViewChangedEventListener;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.runtime.Environment;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.Variable;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.impl.LiveQueryImpl;
import org.drools.runtime.rule.impl.OpenQueryViewChangedEventListenerAdapter;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;

/**
 * Implementation of <code>WorkingMemory</code>.
 */
public class ReteooWorkingMemory extends AbstractWorkingMemory {

    private static final long serialVersionUID = 510l;

    public ReteooWorkingMemory() {
        super();
    }

    public ReteooWorkingMemory(final int id,
                               final InternalRuleBase ruleBase) {
        this( id,
              ruleBase,
              SessionConfiguration.getDefaultInstance(),
              EnvironmentFactory.newEnvironment() );
    }

    /**
     * Construct.
     *
     * @param ruleBase
     *            The backing rule-base.
     */
    public ReteooWorkingMemory(final int id,
                               final InternalRuleBase ruleBase,
                               final SessionConfiguration config,
                               final Environment environment) {
        super( id,
               ruleBase,
               ruleBase.newFactHandleFactory(),
               config,
               environment );
        this.agenda = new DefaultAgenda( ruleBase );
        this.agenda.setWorkingMemory( this );
    }

    public ReteooWorkingMemory(final int id,
                               final InternalRuleBase ruleBase,
                               final SessionConfiguration config,
                               final Environment environment,
                               final WorkingMemoryEventSupport workingMemoryEventSupport,
                               final AgendaEventSupport agendaEventSupport) {
        super( id,
               ruleBase,
               ruleBase.newFactHandleFactory(),
               config,
               environment,
               workingMemoryEventSupport,
               agendaEventSupport );

        this.agenda = new DefaultAgenda( ruleBase );
        this.agenda.setWorkingMemory( this );
    }

    public ReteooWorkingMemory(final int id,
                               final InternalRuleBase ruleBase,
                               final FactHandleFactory handleFactory,
                               final InternalFactHandle initialFactHandle,
                               final long propagationContext,
                               final SessionConfiguration config,
                               final InternalAgenda agenda,
                               final Environment environment) {
        super( id,
               ruleBase,
               handleFactory,
               initialFactHandle,
               //ruleBase.newFactHandleFactory(context),
               propagationContext,
               config,
               environment );
        this.agenda = agenda;
        this.agenda.setWorkingMemory( this );
        //        InputPersister.readFactHandles( context );
        //        super.read( context );
    }

    public QueryResults getQueryResults(final String query) {
        return getQueryResults( query,
                                null );
    }

    @SuppressWarnings("unchecked")
    public QueryResults getQueryResults(final String queryName,
                                        final Object[] arguments) {

        try {
            startOperation();
            this.ruleBase.readLock();
            this.lock.lock();

            this.ruleBase.executeQueuedActions();
            executeQueuedActions();

            DroolsQuery queryObject = new DroolsQuery( queryName,
                                                       arguments,
                                                       getQueryListenerInstance(),
                                                       false );

            InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject,
                                                                          null,
                                                                          this,
                                                                          this );

            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.ASSERTION,
                                                                                      null,
                                                                                      null,
                                                                                      handle,
                                                                                      agenda.getActiveActivations(),
                                                                                      agenda.getDormantActivations(),
                                                                                      getEntryPoint() );

            getEntryPointNode().assertQuery( handle,
                                             propagationContext,
                                             this );

            propagationContext.evaluateActionQueue( this );

            this.handleFactory.destroyFactHandle( handle );

            BaseNode[] nodes = this.ruleBase.getReteooBuilder().getTerminalNodes( queryObject.getQuery() );

            List<Map<String, Declaration>> decls = new ArrayList<Map<String, Declaration>>();
            if ( nodes != null ) {
                for ( BaseNode node : nodes ) {
                    decls.add( ((QueryTerminalNode) node).getSubrule().getOuterDeclarations() );
                }
            }

            executeQueuedActions();

            return new QueryResults( (List<QueryRowWithSubruleIndex>) queryObject.getQueryResultCollector().getResults(),
                                     decls.toArray( new Map[decls.size()] ),
                                     this );
        } finally {
            this.lock.unlock();
            this.ruleBase.readUnlock();
            endOperation();
        }
    }

    private InternalViewChangedEventListener getQueryListenerInstance() {
        switch ( this.config.getQueryListenerOption() ) {
            case STANDARD :
                return new StandardQueryViewChangedEventListener();
            case LIGHTWEIGHT :
                return new NonCloningQueryViewListener();
        }
        return null;
    }

    public LiveQuery openLiveQuery(final String query,
                                   final Object[] arguments,
                                   final ViewChangedEventListener listener) {

        try {
            startOperation();
            this.ruleBase.readLock();
            this.lock.lock();

            this.ruleBase.executeQueuedActions();
            executeQueuedActions();

            DroolsQuery queryObject = new DroolsQuery( query,
                                                       arguments,
                                                       new OpenQueryViewChangedEventListenerAdapter( listener ),
                                                       true );
            InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject,
                                                                          null,
                                                                          this,
                                                                          this );

            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.ASSERTION,
                                                                                      null,
                                                                                      null,
                                                                                      handle,
                                                                                      agenda.getActiveActivations(),
                                                                                      agenda.getDormantActivations(),
                                                                                      getEntryPoint() );

            getEntryPointNode().assertQuery( handle,
                                             propagationContext,
                                             this );

            propagationContext.evaluateActionQueue( this );

            executeQueuedActions();

            return new LiveQueryImpl( this,
                                      handle );
        } finally {
            this.lock.unlock();
            this.ruleBase.readUnlock();
            endOperation();
        }
    }

    public void closeLiveQuery(final InternalFactHandle factHandle) {

        try {
            startOperation();
            this.ruleBase.readLock();
            this.lock.lock();

            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.ASSERTION,
                                                                                      null,
                                                                                      null,
                                                                                      factHandle,
                                                                                      agenda.getActiveActivations(),
                                                                                      agenda.getDormantActivations(),
                                                                                      getEntryPoint() );

            getEntryPointNode().retractQuery( factHandle,
                                              propagationContext,
                                              this );

            getFactHandleFactory().destroyFactHandle( factHandle );

        } finally {
            this.lock.unlock();
            this.ruleBase.readUnlock();
            endOperation();
        }
    }

    public static class WorkingMemoryReteAssertAction
        implements
        WorkingMemoryAction {
        private InternalFactHandle factHandle;

        private boolean            removeLogical;

        private boolean            updateEqualsMap;

        private Rule               ruleOrigin;

        private LeftTuple          leftTuple;

        public WorkingMemoryReteAssertAction(final InternalFactHandle factHandle,
                                             final boolean removeLogical,
                                             final boolean updateEqualsMap,
                                             final Rule ruleOrigin,
                                             final LeftTuple leftTuple) {
            this.factHandle = factHandle;
            this.removeLogical = removeLogical;
            this.updateEqualsMap = updateEqualsMap;
            this.ruleOrigin = ruleOrigin;
            this.leftTuple = leftTuple;
        }

        public WorkingMemoryReteAssertAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = context.handles.get( context.readInt() );
            this.removeLogical = context.readBoolean();
            this.updateEqualsMap = context.readBoolean();

            if ( context.readBoolean() ) {
                String pkgName = context.readUTF();
                String ruleName = context.readUTF();
                Package pkg = context.ruleBase.getPackage( pkgName );
                this.ruleOrigin = pkg.getRule( ruleName );
            }
            if ( context.readBoolean() ) {
                this.leftTuple = context.terminalTupleMap.get( context.readInt() );
            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeInt( WorkingMemoryAction.WorkingMemoryReteAssertAction );

            context.writeInt( this.factHandle.getId() );
            context.writeBoolean( this.removeLogical );
            context.writeBoolean( this.updateEqualsMap );

            if ( this.ruleOrigin != null ) {
                context.writeBoolean( true );
                context.writeUTF( ruleOrigin.getPackage() );
                context.writeUTF( ruleOrigin.getName() );
            } else {
                context.writeBoolean( false );
            }

            if ( this.leftTuple != null ) {
                context.writeBoolean( true );
                context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
            } else {
                context.writeBoolean( false );
            }

        }

        //        public void readExternal(ObjectInput in) throws IOException,
        //                                                ClassNotFoundException {
        //            factHandle = (InternalFactHandle) in.readObject();
        //            removeLogical = in.readBoolean();
        //            updateEqualsMap = in.readBoolean();
        //            ruleOrigin = (Rule) in.readObject();
        //            leftTuple = (LeftTuple) in.readObject();
        //        }
        //
        //        public void writeExternal(ObjectOutput out) throws IOException {
        //            out.writeObject( factHandle );
        //            out.writeBoolean( removeLogical );
        //            out.writeBoolean( updateEqualsMap );
        //            out.writeObject( ruleOrigin );
        //            out.writeObject( leftTuple );
        //        }

        public void execute(InternalWorkingMemory workingMemory) {

            final PropagationContext context = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                           PropagationContext.ASSERTION,
                                                                           this.ruleOrigin,
                                                                           this.leftTuple,
                                                                           this.factHandle );
            ReteooRuleBase ruleBase = (ReteooRuleBase) workingMemory.getRuleBase();
            ruleBase.assertObject( this.factHandle,
                                   this.factHandle.getObject(),
                                   context,
                                   workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }
        
    }

    public static class WorkingMemoryReteExpireAction
        implements
        WorkingMemoryAction {

        private InternalFactHandle factHandle;
        private ObjectTypeNode     node;

        public WorkingMemoryReteExpireAction(final InternalFactHandle factHandle,
                                             final ObjectTypeNode node) {
            this.factHandle = factHandle;
            this.node = node;
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = context.handles.get( context.readInt() );
            final int nodeId = context.readInt();
            this.node = (ObjectTypeNode) context.sinks.get( Integer.valueOf( nodeId ) );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeInt( WorkingMemoryAction.WorkingMemoryReteExpireAction );
            context.writeInt( this.factHandle.getId() );
            context.writeInt( this.node.getId() );
        }

        //
        //        public void readExternal(ObjectInput in) throws IOException,
        //                                                ClassNotFoundException {
        //            factHandle = (InternalFactHandle) in.readObject();
        //            node = (ObjectTypeNode) in.readObject();
        //        }
        //
        //        public void writeExternal(ObjectOutput out) throws IOException {
        //            out.writeObject( factHandle );
        //            out.writeObject( node );
        //        }

        public void execute(InternalWorkingMemory workingMemory) {
            if ( this.factHandle.isValid() ) {
                // if the fact is still in the working memory (since it may have been previously retracted already
                final PropagationContext context = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                               PropagationContext.EXPIRATION,
                                                                               null,
                                                                               null,
                                                                               this.factHandle );
                ((EventFactHandle) factHandle).setExpired( true );
                this.node.retractObject( factHandle,
                                         context,
                                         workingMemory );

                // if no activations for this expired event
                if ( ((EventFactHandle) factHandle).getActivationsCount() == 0 ) {
                    // remove it from the object store and clean up resources
                    ((EventFactHandle) factHandle).getEntryPoint().retract( factHandle );
                }
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }
    }

    public static class QueryInsertModifyAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;

        private InternalFactHandle factHandle;

        private LeftTuple          leftTuple;
        private int[]              varIndexes;
        private List<Integer>      srcVarIndexes;
        private QueryElementNode   node;

        public QueryInsertModifyAction(PropagationContext context) {
            this.context = context;
        }

        public QueryInsertModifyAction(PropagationContext context,
                                        InternalFactHandle factHandle,
                                        LeftTuple leftTuple,
                                        int[] varIndexes,
                                        List<Integer> srcVarIndexes,
                                        QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.varIndexes = varIndexes;
            this.srcVarIndexes = srcVarIndexes;
            this.node = node;
        }

        public QueryInsertModifyAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
//            this.factHandle = context.handles.get( context.readInt() );
//
//            this.leftTuple = context.terminalTupleMap.get( context.readInt() );
//            varIndexes = new int[context.readInt()];
//            for ( int i = 0; i < varIndexes.length; i++ ) {
//                varIndexes[i] = context.readInt();
//            }
//            node = (QueryElementNode) context.sinks.get( context.readInt() );
//            try {
//                srcVarIndexes = (List<Integer>) context.readObject();
//            } catch ( ClassNotFoundException e ) {
//                throw new RuntimeException( "Unable to Marshal",
//                                            e );
//            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );            
//            context.writeInt( WorkingMemoryAction.WorkingMemoryReteAssertAction );
//
//            context.writeInt( this.factHandle.getId() );
//
//            context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
//
//            context.writeInt( varIndexes.length );
//            for ( int i : varIndexes ) {
//                context.writeInt( varIndexes[i] );
//            }
//
//            context.writeObject( node.getId() );
//
//            context.writeObject( srcVarIndexes );
        }

        public void execute(InternalWorkingMemory workingMemory) {            
            if ( factHandle.getFirstLeftTuple() == null ) {
                workingMemory.getEntryPointNode().assertQuery( factHandle,
                                                               context,
                                                               workingMemory );
            } else {
                workingMemory.getEntryPointNode().modifyQuery( factHandle,
                                                               context,
                                                               workingMemory );
            }             
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
        
        public String toString() {
            return "[QueryInsertModifyAction facthandle=" + factHandle + ",\n        leftTuple=" + leftTuple +"]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }        
    }

    public static class QueryEvaluationAction
        implements
        WorkingMemoryAction {

        private PropagationContext context;

        private LeftTuple          leftTuple;
        
        private InternalFactHandle factHandle;

        private QueryElementNode   node;

        public QueryEvaluationAction(PropagationContext context) {
            this.context = context;
        }

        public QueryEvaluationAction(PropagationContext context,
                                                  InternalFactHandle factHandle, 
                                                  LeftTuple leftTuple,
                                                  QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryEvaluationAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
//            this.leftTuple = context.terminalTupleMap.get( context.readInt() );
//            node = (QueryElementNode) context.sinks.get( context.readInt() );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
//            context.writeInt( WorkingMemoryAction.WorkingMemoryReteAssertAction );
//            context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
//            context.writeObject( node.getId() );
        }

        public void execute(InternalWorkingMemory workingMemory) {                        
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            RightTupleList rightTuples = query.getRightTupleList();
            query.setRightTupleList( null ); // null so further operations happen on a new stack element
            
            for ( RightTuple rightTuple = rightTuples.getFirst(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                for( LeftTuple childLeftTuple = rightTuple.firstChild;childLeftTuple != null; childLeftTuple = ( LeftTuple ) childLeftTuple.getRightParentNext() ) { 
                  node.getSinkPropagator().doPropagateAssertLeftTuple( context,
                                                                       workingMemory,
                                                                       childLeftTuple,
                                                                       childLeftTuple.getLeftTupleSink() );                
                }
            }            

            // @FIXME, this should work, but it's closing needed fact handles
            // actually an evaluation 34 appears on the stack twice....
//            if ( !node.isOpenQuery() ) {
//                workingMemory.getFactHandleFactory().destroyFactHandle( this.factHandle );
//            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public LeftTuple getLeftTuple() {
            return this.leftTuple;
        }
        
        public String toString() {
            return "[QueryEvaluationAction leftTuple=" + leftTuple +"]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }           
    }

    public static class QueryRetractAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        private QueryElementNode   node;

        public QueryRetractAction(PropagationContext context) {
            this.context = context;
        }

        public QueryRetractAction(PropagationContext context,
                                    LeftTuple leftTuple,
                                    QueryElementNode node) {
            this.context = context;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryRetractAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
//            this.leftTuple = context.terminalTupleMap.get( context.readInt() );
//            node = (QueryElementNode) context.sinks.get( context.readInt() );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );            
//            context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
//            context.writeObject( node.getId() );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            InternalFactHandle factHandle = (InternalFactHandle) leftTuple.getObject();            
            if ( node.isOpenQuery() ) {
                workingMemory.getEntryPointNode().retractObject( factHandle,
                                                                 null,
                                                                 workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( workingMemory.getEntryPoint(),
                                                                                                                                       factHandle.getObject() ),
                                                                                                                                       workingMemory );
                workingMemory.getFactHandleFactory().destroyFactHandle( factHandle );
            }
            if ( leftTuple.getFirstChild() != null ) {
                node.getSinkPropagator().propagateRetractLeftTuple( leftTuple,
                                                                    context,
                                                                    workingMemory );
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
        
        public String toString() {
            return "[QueryRetractAction leftTuple=" + leftTuple +"]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }         
    }

    public static class QueryRetractInsertAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        private QueryElementNode   node;

        public QueryRetractInsertAction(PropagationContext context) {
            this.context = context;
        }

        public QueryRetractInsertAction(PropagationContext context,
                                        LeftTuple leftTuple,
                                        QueryElementNode node) {
            this.context = context;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryRetractInsertAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );            
//            this.leftTuple = context.terminalTupleMap.get( context.readInt() );
//            node = (QueryElementNode) context.sinks.get( context.readInt() );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );            
//            context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
//            context.writeObject( node.getId() );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            if ( leftTuple.getFirstChild() != null ) {
                node.getSinkPropagator().propagateRetractLeftTuple( leftTuple,
                                                                    context,
                                                                    workingMemory );
            }
            node.assertLeftTuple( leftTuple,
                                  context,
                                  workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
        
        public String toString() {
            return "[QueryRetractInsertAction leftTuple=" + leftTuple +"]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }           
    }

    public static class QueryResultRetractAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        private InternalFactHandle factHandle;
        private QueryElementNode   node;

        public QueryResultRetractAction(PropagationContext context,
                                        InternalFactHandle factHandle,
                                        LeftTuple leftTuple,
                                        QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryResultRetractAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
            //            this.factHandle = context.handles.get( context.readInt() );
            //
            //            this.leftTuple = context.terminalTupleMap.get( context.readInt() );
            //            varIndexes = new int[context.readInt()];
            //            for ( int i = 0; i < varIndexes.length; i++ ) {
            //                varIndexes[i] = context.readInt();
            //            }
            //            node = (QueryElementNode) context.sinks.get( context.readInt() );
            //            try {
            //                srcVarIndexes = (List<Integer>) context.readObject();
            //            } catch ( ClassNotFoundException e ) {
            //                throw new RuntimeException( "Unable to Marshal",
            //                                            e );
            //            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
            //            context.writeInt( WorkingMemoryAction.WorkingMemoryReteAssertAction );
            //
            //            context.writeInt( this.factHandle.getId() );
            //
            //            context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
            //
            //            context.writeInt( varIndexes.length );
            //            for ( int i : varIndexes ) {
            //                context.writeInt( varIndexes[i] );
            //            }
            //
            //            context.writeObject( node.getId() );
            //
            //            context.writeObject( srcVarIndexes );
        }
        
        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            RightTupleList rightTuples = query.getRightTupleList();
            query.setRightTupleList( null ); // null so further operations happen on a new stack element
            
            for ( RightTuple rightTuple = rightTuples.getFirst(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
              this.node.getSinkPropagator().propagateRetractRightTuple( rightTuple,
                                                                        context,
                                                                        workingMemory );
            }           
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public LeftTuple getLeftTuple() {
            return this.leftTuple;
        }
        
        public String toString() {
            return "[QueryResultRetractAction leftTuple=" + leftTuple +"]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }         
    }

    public static class QueryResultUpdateAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;
        private LeftTuple          leftTuple;
        InternalFactHandle         factHandle;
        private QueryElementNode   node;

        public QueryResultUpdateAction(PropagationContext context,
                                       InternalFactHandle factHandle, 
                                       LeftTuple leftTuple,
                                       QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryResultUpdateAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
            //        this.factHandle = context.handles.get( context.readInt() );
            //
            //        this.leftTuple = context.terminalTupleMap.get( context.readInt() );
            //        varIndexes = new int[context.readInt()];
            //        for ( int i = 0; i < varIndexes.length; i++ ) {
            //            varIndexes[i] = context.readInt();
            //        }
            //        node = (QueryElementNode) context.sinks.get( context.readInt() );
            //        try {
            //            srcVarIndexes = (List<Integer>) context.readObject();
            //        } catch ( ClassNotFoundException e ) {
            //            throw new RuntimeException( "Unable to Marshal",
            //                                        e );
            //        }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
            //        context.writeInt( WorkingMemoryAction.WorkingMemoryReteAssertAction );
            //
            //        context.writeInt( this.factHandle.getId() );
            //
            //        context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
            //
            //        context.writeInt( varIndexes.length );
            //        for ( int i : varIndexes ) {
            //            context.writeInt( varIndexes[i] );
            //        }
            //
            //        context.writeObject( node.getId() );
            //
            //        context.writeObject( srcVarIndexes );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            RightTupleList rightTuples = query.getRightTupleList();
            query.setRightTupleList( null ); // null so further operations happen on a new stack element
            
            for ( RightTuple rightTuple = rightTuples.getFirst(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                this.node.getSinkPropagator().propagateModifyChildLeftTuple( rightTuple.firstChild,
                                                                             rightTuple.firstChild.getLeftParent(),
                                                                             context,
                                                                             workingMemory,
                                                                             true );
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }
        
        public String toString() {
            return "[QueryResultUpdateAction leftTuple=" + leftTuple +"]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }          

    }

    public static class QueryRiaFixerNodeFixer
        implements
        WorkingMemoryAction {
        private PropagationContext context;

        private LeftTuple          leftTuple;
        private BetaNode           node;
        private boolean            retract;

        public QueryRiaFixerNodeFixer(PropagationContext context) {
            this.context = context;
        }

        public QueryRiaFixerNodeFixer(PropagationContext context,
                                      LeftTuple leftTuple,
                                      boolean retract,
                                      BetaNode node) {
            this.context = context;
            this.leftTuple = leftTuple;
            this.retract = retract;
            this.node = node;
        }

        public QueryRiaFixerNodeFixer(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
//            this.leftTuple = context.terminalTupleMap.get( context.readInt() );
//            this.node = (BetaNode) context.sinks.get( context.readInt() );

        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
//            context.writeInt( WorkingMemoryAction.WorkingMemoryReteAssertAction );
//
//            context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
//
//            context.writeObject( node.getId() );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            if ( leftTuple.getFirstChild() == null ) {
                this.node.assertLeftTuple( leftTuple,
                                           context,
                                           workingMemory );
            } else {
                if ( retract ) {
                    this.node.getSinkPropagator().propagateRetractLeftTuple( leftTuple,
                                                                             context,
                                                                             workingMemory );
                } else {
                    this.node.getSinkPropagator().propagateModifyChildLeftTuple( leftTuple,
                                                                                 context,
                                                                                 workingMemory,
                                                                                 true );
                }
            }

            Object node = workingMemory.getNodeMemory( this.node );

            RightTupleMemory rightMemory = null;
            if ( node instanceof BetaMemory ) {
                rightMemory = ((BetaMemory) node).getRightTupleMemory();
            } else if ( node instanceof AccumulateMemory ) {
                rightMemory = ((AccumulateMemory) node).betaMemory.getRightTupleMemory();
            }

            FastIterator rightIt = rightMemory.fastIterator();
            RightTuple temp = null;
            for ( RightTuple rightTuple = rightMemory.getFirst( leftTuple,
                                                                        (InternalFactHandle) context.getFactHandle() ); rightTuple != null; ) {
                temp = (RightTuple) rightIt.next( rightTuple );
                rightMemory.remove( rightTuple );
                rightTuple = temp;
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
        
        public String toString() {
            return "[QueryRiaFixerNodeFixer leftTuple=" + leftTuple +",\n        retract=" + retract + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub
            
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // TODO Auto-generated method stub
            
        }           
    }

    public EntryPoint getEntryPoint() {
        return this.defaultEntryPoint.getEntryPoint();
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        return this;
    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
    }

    public Collection<Object> getObjects() {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
    }

}
