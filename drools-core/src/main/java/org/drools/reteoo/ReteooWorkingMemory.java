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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.drools.common.TupleStartEqualsConstraint;
import org.drools.common.TupleStartEqualsConstraint.TupleStartEqualsConstraintContextEntry;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.FastIterator;
import org.drools.core.util.RightTupleList;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterHelper;
import org.drools.marshalling.impl.ProtobufMessages;
import org.drools.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.marshalling.impl.ProtobufMessages.ActionQueue.Assert;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.AccumulateNode.ActivitySource;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.Environment;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.impl.LiveQueryImpl;
import org.drools.runtime.rule.impl.OpenQueryViewChangedEventListenerAdapter;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;

/**
 * Implementation of <code>WorkingMemory</code>.
 */
public class ReteooWorkingMemory extends AbstractWorkingMemory implements ReteooWorkingMemoryInterface {

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
                                     this,
                                     ( queryObject.getQuery() != null ) ? queryObject.getQuery().getParameters()  : new Declaration[0] );
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

            propagationContext.evaluateActionQueue( this );
            
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

        public WorkingMemoryReteAssertAction(MarshallerReaderContext context,
                                             Action _action) {
            Assert _assert = _action.getAssert();
            this.factHandle = context.handles.get( _assert.getHandleId() );
            this.removeLogical = _assert.getRemoveLogical();
            this.updateEqualsMap = _assert.getUpdateEqualsMap();

            if ( _assert.hasTuple() ) {
                String pkgName = _assert.getOriginPkgName();
                String ruleName = _assert.getOriginRuleName();
                Package pkg = context.ruleBase.getPackage( pkgName );
                this.ruleOrigin = pkg.getRule( ruleName );
                this.leftTuple = context.filter.getTuplesCache().get( PersisterHelper.createActivationKey( pkgName, ruleName, _assert.getTuple() ) );
            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeShort( WorkingMemoryAction.WorkingMemoryReteAssertAction );

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

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            ProtobufMessages.ActionQueue.Assert.Builder _assert = ProtobufMessages.ActionQueue.Assert.newBuilder();
            _assert.setHandleId( this.factHandle.getId() )
                   .setRemoveLogical( this.removeLogical )
                   .setUpdateEqualsMap( this.updateEqualsMap );

            if ( this.leftTuple != null ) {
                ProtobufMessages.Tuple.Builder _tuple = ProtobufMessages.Tuple.newBuilder();
                for( LeftTuple entry = this.leftTuple; entry != null; entry = entry.getParent() ) {
                    _tuple.addHandleId( entry.getLastHandle().getId() );
                }
                _assert.setOriginPkgName( ruleOrigin.getPackageName() )
                       .setOriginRuleName( ruleOrigin.getName() )
                       .setTuple( _tuple.build() );
            }
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.ASSERT )
                    .setAssert( _assert.build() )
                    .build();
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            factHandle = (InternalFactHandle) in.readObject();
            removeLogical = in.readBoolean();
            updateEqualsMap = in.readBoolean();
            ruleOrigin = (Rule) in.readObject();
            leftTuple = (LeftTuple) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( factHandle );
            out.writeBoolean( removeLogical );
            out.writeBoolean( updateEqualsMap );
            out.writeObject( ruleOrigin );
            out.writeObject( leftTuple );
        }

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
            context.evaluateActionQueue( workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
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

        public InternalFactHandle getFactHandle() {
            return factHandle;
        }

        public void setFactHandle(InternalFactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public ObjectTypeNode getNode() {
            return node;
        }

        public void setNode(ObjectTypeNode node) {
            this.node = node;
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = context.handles.get( context.readInt() );
            final int nodeId = context.readInt();
            this.node = (ObjectTypeNode) context.sinks.get( Integer.valueOf( nodeId ) );
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context,
                                             Action _action) {
            this.factHandle = context.handles.get( _action.getExpire().getHandleId() );
            this.node = (ObjectTypeNode) context.sinks.get( Integer.valueOf( _action.getExpire().getNodeId() ) );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeShort( WorkingMemoryAction.WorkingMemoryReteExpireAction );
            context.writeInt( this.factHandle.getId() );
            context.writeInt( this.node.getId() );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.EXPIRE )
                    .setExpire( ProtobufMessages.ActionQueue.Expire.newBuilder()
                                .setHandleId( this.factHandle.getId() )
                                .setNodeId( this.node.getId() )
                                .build() )
                    .build();
        }

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

                context.evaluateActionQueue( workingMemory );
                // if no activations for this expired event
                if ( ((EventFactHandle) factHandle).getActivationsCount() == 0 ) {
                    // remove it from the object store and clean up resources
                    ((EventFactHandle) factHandle).getEntryPoint().retract( factHandle );
                }
                context.evaluateActionQueue( workingMemory );
            }

        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }
    }

    public static class EvaluateResultConstraints
        implements
        WorkingMemoryAction {

        private ActivitySource        source;
        private LeftTuple             leftTuple;
        private PropagationContext    context;
        private InternalWorkingMemory workingMemory;
        private AccumulateMemory      memory;
        private AccumulateContext     accctx;
        private boolean               useLeftMemory;
        private AccumulateNode        node;

        public EvaluateResultConstraints(PropagationContext context) {
            this.context = context;
        }

        public EvaluateResultConstraints(ActivitySource source,
                                         LeftTuple leftTuple,
                                         PropagationContext context,
                                         InternalWorkingMemory workingMemory,
                                         AccumulateMemory memory,
                                         AccumulateContext accctx,
                                         boolean useLeftMemory,
                                         AccumulateNode node) {
            this.source = source;
            this.leftTuple = leftTuple;
            this.context = context;
            this.workingMemory = workingMemory;
            this.memory = memory;
            this.accctx = accctx;
            this.useLeftMemory = useLeftMemory;
            this.node = node;
        }

        public EvaluateResultConstraints(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            final AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
            accctx.setAction( null );
            node.evaluateResultConstraints( source,
                                            leftTuple,
                                            context,
                                            workingMemory,
                                            memory,
                                            accctx,
                                            useLeftMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public ActivitySource getSource() {
            return source;
        }

        public void setSource(ActivitySource source) {
            this.source = source;
        }

        public String toString() {
            return "[ResumeInsertAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }
    }

    public static class QueryInsertAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;

        private InternalFactHandle factHandle;

        private LeftTuple          leftTuple;
        private QueryElementNode   node;

        public QueryInsertAction(PropagationContext context) {
            this.context = context;
        }

        public QueryInsertAction(PropagationContext context,
                                 InternalFactHandle factHandle,
                                 LeftTuple leftTuple,
                                 QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryInsertAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            // we null this as it blocks this query being called, to avoid re-entrant issues. i.e. scheduling an insert and then an update, before the insert is executed
            ((DroolsQuery) this.factHandle.getObject()).setAction( null );
            workingMemory.getEntryPointNode().assertQuery( factHandle,
                                                           context,
                                                           workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public String toString() {
            return "[QueryInsertAction facthandle=" + factHandle + ",\n        leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }
    }

    public static class QueryUpdateAction
        implements
        WorkingMemoryAction {
        private PropagationContext context;

        private InternalFactHandle factHandle;

        private LeftTuple          leftTuple;
        private QueryElementNode   node;

        public QueryUpdateAction(PropagationContext context) {
            this.context = context;
        }

        public QueryUpdateAction(PropagationContext context,
                                 InternalFactHandle factHandle,
                                 LeftTuple leftTuple,
                                 QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryUpdateAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            workingMemory.getEntryPointNode().modifyQuery( factHandle,
                                                           context,
                                                           workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public String toString() {
            return "[QueryInsertModifyAction facthandle=" + factHandle + ",\n        leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
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
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            InternalFactHandle factHandle = (InternalFactHandle) leftTuple.getObject();
            if ( node.isOpenQuery() ) {
                // iterate to the query terminal node, as the child leftTuples will get picked up there                
                workingMemory.getEntryPointNode().retractObject( factHandle,
                                                                 context,
                                                                 workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( workingMemory.getEntryPoint(),
                                                                                                                                       factHandle.getObject() ),
                                                                 workingMemory );
                //workingMemory.getFactHandleFactory().destroyFactHandle( factHandle );
            } else {
                // get child left tuples, as there is no open query
                if ( leftTuple.getFirstChild() != null ) {
                    node.getSinkPropagator().propagateRetractLeftTuple( leftTuple,
                                                                        context,
                                                                        workingMemory );
                }
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public String toString() {
            return "[QueryRetractAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }
    }

    public static class QueryResultInsertAction
        implements
        WorkingMemoryAction {

        private PropagationContext context;

        private LeftTuple          leftTuple;

        private InternalFactHandle factHandle;

        private QueryElementNode   node;

        public QueryResultInsertAction(PropagationContext context) {
            this.context = context;
        }

        public QueryResultInsertAction(PropagationContext context,
                                       InternalFactHandle factHandle,
                                       LeftTuple leftTuple,
                                       QueryElementNode node) {
            this.context = context;
            this.factHandle = factHandle;
            this.leftTuple = leftTuple;
            this.node = node;
        }

        public QueryResultInsertAction(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            RightTupleList rightTuples = query.getResultInsertRightTupleList();
            query.setResultInsertRightTupleList( null ); // null so further operations happen on a new stack element

            for ( RightTuple rightTuple = rightTuples.getFirst(); rightTuple != null; ) {
                RightTuple tmp = (RightTuple) rightTuple.getNext();
                rightTuples.remove( rightTuple );
                for ( LeftTuple childLeftTuple = rightTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getRightParentNext() ) {
                    node.getSinkPropagator().doPropagateAssertLeftTuple( context,
                                                                         workingMemory,
                                                                         childLeftTuple,
                                                                         childLeftTuple.getLeftTupleSink() );
                }
                rightTuple = tmp;
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
            return "[QueryEvaluationAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
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
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            RightTupleList rightTuples = query.getResultRetractRightTupleList();
            query.setResultRetractRightTupleList( null ); // null so further operations happen on a new stack element

            for ( RightTuple rightTuple = rightTuples.getFirst(); rightTuple != null; ) {
                RightTuple tmp = (RightTuple) rightTuple.getNext();
                rightTuples.remove( rightTuple );
                this.node.getSinkPropagator().propagateRetractRightTuple( rightTuple,
                                                                          context,
                                                                          workingMemory );
                rightTuple = tmp;
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public LeftTuple getLeftTuple() {
            return this.leftTuple;
        }

        public String toString() {
            return "[QueryResultRetractAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
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
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            DroolsQuery query = (DroolsQuery) factHandle.getObject();
            RightTupleList rightTuples = query.getResultUpdateRightTupleList();
            query.setResultUpdateRightTupleList( null ); // null so further operations happen on a new stack element

            for ( RightTuple rightTuple = rightTuples.getFirst(); rightTuple != null; ) {
                RightTuple tmp = (RightTuple) rightTuple.getNext();
                rightTuples.remove( rightTuple );
                this.node.getSinkPropagator().propagateModifyChildLeftTuple( rightTuple.firstChild,
                                                                             rightTuple.firstChild.getLeftParent(),
                                                                             context,
                                                                             workingMemory,
                                                                             true );
                rightTuple = tmp;
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

        public String toString() {
            return "[QueryResultUpdateAction leftTuple=" + leftTuple + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
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
        }

        public void write(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException( "Should not be present in network on serialisation" );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            leftTuple.setLeftTupleSink( this.node );
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

            if ( leftTuple.getLeftParent() == null ) {
                // It's not an open query, as we aren't recording parent chains, so we need to clear out right memory

                Object node = workingMemory.getNodeMemory( this.node );

                RightTupleMemory rightMemory = null;
                if ( node instanceof BetaMemory ) {
                    rightMemory = ((BetaMemory) node).getRightTupleMemory();
                } else if ( node instanceof AccumulateMemory ) {
                    rightMemory = ((AccumulateMemory) node).betaMemory.getRightTupleMemory();
                }

                
                final TupleStartEqualsConstraint constraint = TupleStartEqualsConstraint.getInstance();
                TupleStartEqualsConstraintContextEntry contextEntry = new TupleStartEqualsConstraintContextEntry();
                contextEntry.updateFromTuple( workingMemory, leftTuple );
                
                FastIterator rightIt = rightMemory.fastIterator();
                RightTuple temp = null;
                for ( RightTuple rightTuple = rightMemory.getFirst( leftTuple,
                                                                    (InternalFactHandle) context.getFactHandle() ); rightTuple != null; ) {
                    temp = (RightTuple) rightIt.next( rightTuple );
                    
                    if ( constraint.isAllowedCachedLeft( contextEntry, rightTuple.getFactHandle() ) ) {
                        rightMemory.remove( rightTuple );
                    }                                        
                    rightTuple = temp;
                }
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

        public String toString() {
            return "[QueryRiaFixerNodeFixer leftTuple=" + leftTuple + ",\n        retract=" + retract + "]\n";
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }
    }

    public EntryPoint getEntryPoint() {
        return this.defaultEntryPoint.getEntryPoint();
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        return this;
    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        List list = new ArrayList();
        
        for ( Iterator it = iterateFactHandles(); it.hasNext(); ) {
            FactHandle fh = ( FactHandle) it.next();
            list.add(  fh );
        }
        
        return list;
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
