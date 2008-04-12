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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.base.DroolsQuery;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.ObjectTypeConfigurationRegistry;
import org.drools.common.PropagationContextImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.event.RuleBaseEventListener;
import org.drools.rule.EntryPoint;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

/**
 * Implementation of <code>WorkingMemory</code>.
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class ReteooWorkingMemory extends AbstractWorkingMemory
    implements
    Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = 400L;

    public ReteooWorkingMemory() {

    }

    /**
     * Construct.
     *
     * @param ruleBase
     *            The backing rule-base.
     */
    public ReteooWorkingMemory(final int id,
                               final InternalRuleBase ruleBase) {
        super( id,
               ruleBase,
               ruleBase.newFactHandleFactory() );
        this.agenda = new DefaultAgenda( this );
    }

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

        private Activation         activationOrigin;

        public WorkingMemoryReteAssertAction() {

        }

        public WorkingMemoryReteAssertAction(final InternalFactHandle factHandle,
                                             final boolean removeLogical,
                                             final boolean updateEqualsMap,
                                             final Rule ruleOrigin,
                                             final Activation activationOrigin) {
            super();
            this.factHandle = factHandle;
            this.removeLogical = removeLogical;
            this.updateEqualsMap = updateEqualsMap;
            this.ruleOrigin = ruleOrigin;
            this.activationOrigin = activationOrigin;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            factHandle = (InternalFactHandle) in.readObject();
            removeLogical = in.readBoolean();
            updateEqualsMap = in.readBoolean();
            ruleOrigin = (Rule) in.readObject();
            activationOrigin = (Activation) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( factHandle );
            out.writeBoolean( removeLogical );
            out.writeBoolean( updateEqualsMap );
            out.writeObject( ruleOrigin );
            out.writeObject( activationOrigin );
        }

        public void execute(InternalWorkingMemory workingMemory) {

            final PropagationContext context = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                           PropagationContext.ASSERTION,
                                                                           this.ruleOrigin,
                                                                           this.activationOrigin );
            ReteooRuleBase ruleBase = (ReteooRuleBase) workingMemory.getRuleBase();
            ruleBase.assertObject( this.factHandle,
                                   this.factHandle.getObject(),
                                   context,
                                   workingMemory );
        }
    }

}
