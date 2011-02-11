/**
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
import java.util.Collection;
import java.util.List;

import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.SessionConfiguration;
import org.drools.base.DroolsQuery;
import org.drools.base.InternalViewChangedEventListener;
import org.drools.base.NonCloningQueryViewListener;
import org.drools.base.StandardQueryViewChangedEventListener;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultAgenda;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
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
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class ReteooWorkingMemory extends AbstractWorkingMemory {

    /**
     *
     */
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
    public QueryResults getQueryResults(final String query,
                                        final Object[] arguments) {

        try {
            startOperation();
            this.ruleBase.readLock();
            this.lock.lock();
            DroolsQuery queryObject = new DroolsQuery( query,
                                                       arguments,
                                                       getQueryListenerInstance(),
                                                       false );
            
            ObjectTypeConf objectTypeConf = this.defaultEntryPoint.getObjectTypeConfigurationRegistry().getObjectTypeConf( this.defaultEntryPoint.getEntryPoint(),
                                                                                                                           queryObject );
            InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject,
                                                                          objectTypeConf,
                                                                          this,
                                                                          this );

            insert( handle,
                    queryObject,
                    null,
                    null,
                    objectTypeConf );

            this.handleFactory.destroyFactHandle( handle );

            Declaration[] declarations = new Declaration[0];
            if ( queryObject.getQuery() != null ) {
                // this is null when there are no query results, thus the query object is never set
                declarations = queryObject.getQuery().getDeclarations();
            }

            return new QueryResults( (List<FactHandle[]>) queryObject.getQueryResultCollector().getResults(),
                                     declarations,
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
            DroolsQuery queryObject = new DroolsQuery( query,
                                                       arguments,
                                                       new OpenQueryViewChangedEventListenerAdapter( listener ),
                                                       true );
            InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject,
                                                                          this.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
                                                                                                                                       queryObject ),
                                                                          this,
                                                                          this );

            insert( handle,
                    queryObject,
                    null,
                    null,
                    this.defaultEntryPoint.getObjectTypeConfigurationRegistry().getObjectTypeConf( this.defaultEntryPoint.getEntryPoint(),
                                                                                                   queryObject ) );

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

            getEntryPointNode().retractObject( factHandle,
                                               null,
                                               this.getObjectTypeConfigurationRegistry().getObjectTypeConf( this.getEntryPoint(),
                                                                                                            factHandle.getObject() ),
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
        }
        
        public void execute(InternalKnowledgeRuntime kruntime) {
        	execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
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

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            factHandle = (InternalFactHandle) in.readObject();
            node = (ObjectTypeNode) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( factHandle );
            out.writeObject( node );
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

                // if no activations for this expired event
                if ( ((EventFactHandle) factHandle).getActivationsCount() == 0 ) {
                    // remove it from the object store and clean up resources
                    ((EventFactHandle) factHandle).getEntryPoint().retract( factHandle );
                }
            }
        }
        
        public void execute(InternalKnowledgeRuntime kruntime) {
        	execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
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
