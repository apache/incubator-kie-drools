package org.drools.reteoo;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.drools.QueryResults;
import org.drools.SessionConfiguration;
import org.drools.base.DroolsQuery;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultAgenda;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.rule.EntryPoint;
import org.drools.rule.Package;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.runtime.Environment;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
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
    private static final long serialVersionUID = 400L;
    
    public ReteooWorkingMemory() {
        super();
    }

    public ReteooWorkingMemory(final int id,
                               final InternalRuleBase ruleBase) {
        this( id,
              ruleBase,
              new SessionConfiguration(),
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
                               final FactHandleFactory handleFactory,
                               final InitialFactHandle initialFactHandle,
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

    //    public void write(WMSerialisationOutContext context) throws IOException, ClassNotFoundException {
    //        this.handleFactory.write( context );
    //        
    ////        context.writeInt( this.initialFactHandle.getId() );
    ////        context.writeLong( this.initialFactHandle.getRecency() );        
    //        
    //        OutputPersister.writeFactHandles( context );
    //        super.write( context );
    //    }

    public QueryResults getQueryResults(final String query) {
        return getQueryResults( query,
                                null );
    }

    public QueryResults getQueryResults(final String query,
                                        final Object[] arguments) {

        Object object = new DroolsQuery( query,
                                         arguments );
        InternalFactHandle handle = this.handleFactory.newFactHandle( object,
                                                                      this.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
                                                                                                                                   object ),
                                                                      this );

        insert( handle,
                object,
                null,
                null,
                this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                    object ) );

        final QueryTerminalNode node = (QueryTerminalNode) this.queryResults.remove( query );
        Query queryObj = null;
        List list = null;

        if ( node == null ) {
            // There are no results, first check the query object actually exists
            final org.drools.rule.Package[] pkgs = this.ruleBase.getPackages();
            for ( int i = 0; i < pkgs.length; i++ ) {
                final Rule rule = pkgs[i].getRule( query );
                if ( (rule != null) && (rule instanceof Query) ) {
                    queryObj = (Query) rule;
                    break;
                }
            }

            this.handleFactory.destroyFactHandle( handle );

            if ( queryObj == null ) {
                throw new IllegalArgumentException( "Query '" + query + "' does not exist" );
            }
            list = Collections.EMPTY_LIST;
        } else {
            list = (List) this.getNodeMemory( node );

            if ( list == null ) {
                list = Collections.EMPTY_LIST;
            } else {
                this.clearNodeMemory( node );
            }
            queryObj = (Query) node.getRule();

            this.handleFactory.destroyFactHandle( handle );
        }

        return new QueryResults( list,
                                 queryObj,
                                 this );
    }

    void setQueryResults(final String query,
                         final QueryTerminalNode node) {
        if ( this.queryResults == Collections.EMPTY_MAP ) {
            this.queryResults = new HashMap();
        }
        this.queryResults.put( query,
                               node );
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
            if( this.factHandle.isValid() ) {
                // if the fact is still in the working memory (since it may have been previously retracted already
                final PropagationContext context = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                               PropagationContext.EXPIRATION,
                                                                               null,
                                                                               null,
                                                                               this.factHandle );
                ((EventFactHandle)factHandle).setExpired( true );
                this.node.retractObject( factHandle,
                                         context,
                                         workingMemory );
                
                // if no activations for this expired event
                if( ((EventFactHandle)factHandle).getActivationsCount() == 0 ) {
                    // remove it from the object store and clean up resources
                    ((EventFactHandle)factHandle).getEntryPoint().retract( factHandle );
                }
            }
        }
    }
  public EntryPoint getEntryPoint() {
        return this.entryPoint;
  }
   public InternalWorkingMemory getInternalWorkingMemory() {
        return this;
    }

    public Collection< ? extends FactHandle> getFactHandles() {
        throw new UnsupportedOperationException("this is implementedby StatefulKnowledgeImpl");
    }

    public Collection< ? extends FactHandle> getFactHandles(ObjectFilter filter) {
        throw new UnsupportedOperationException("this is implementedby StatefulKnowledgeImpl");
    }

    public Collection< ? extends Object> getObjects() {
        throw new UnsupportedOperationException("this is implementedby StatefulKnowledgeImpl");
    }

    public Collection< ? extends Object> getObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException("this is implementedby StatefulKnowledgeImpl");
    }

}
