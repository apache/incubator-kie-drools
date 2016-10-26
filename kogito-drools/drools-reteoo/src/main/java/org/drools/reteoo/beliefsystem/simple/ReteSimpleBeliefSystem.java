/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.reteoo.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.BeliefSystemLogicalCallback;
import org.drools.core.beliefsystem.simple.SimpleBeliefSet;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class ReteSimpleBeliefSystem
        implements
        BeliefSystem<SimpleMode> {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public ReteSimpleBeliefSystem(NamedEntryPoint ep,
                                  TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    @Override
    public SimpleMode asMode( Object value ) {
        return new SimpleMode();
    }

    public BeliefSet<SimpleMode> insert(LogicalDependency<SimpleMode> node,
                                        BeliefSet<SimpleMode> beliefSet,
                                        PropagationContext context,
                                        ObjectTypeConf typeConf) {
        return insert( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context, typeConf );
    }

    @Override
    public BeliefSet<SimpleMode> insert( SimpleMode mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<SimpleMode> beliefSet, PropagationContext context, ObjectTypeConf typeConf ) {

        boolean empty = beliefSet.isEmpty();

        beliefSet.add( mode );

        if ( empty ) {
            InternalFactHandle handle = beliefSet.getFactHandle();

            ep.insert( handle,
                       handle.getObject(),
                       rule,
                       activation,
                       typeConf );
        }
        return beliefSet;
    }


    public void read(LogicalDependency<SimpleMode> node,
                     BeliefSet<SimpleMode> beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        //insert(node, beliefSet, context, typeConf );
        beliefSet.add( node.getMode() );
    }

    @Override
    public void stage( PropagationContext context, BeliefSet<SimpleMode> beliefSet ) {
        
    }

    @Override
    public void unstage( PropagationContext context, BeliefSet<SimpleMode> beliefSet ) {

    }

    @Override
    public void delete(LogicalDependency<SimpleMode> node,
                       BeliefSet<SimpleMode> beliefSet,
                       PropagationContext context) {
        delete( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context );
    }

    @Override
    public void delete( SimpleMode mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<SimpleMode> beliefSet, PropagationContext context ) {

        SimpleBeliefSet sBeliefSet = (SimpleBeliefSet) beliefSet;
        beliefSet.remove( mode );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        
        if ( beliefSet.isEmpty() &&
             !((context.getType() == PropagationContext.Type.DELETION || context.getType() == PropagationContext.Type.MODIFICATION) // retract and modifies clean up themselves
             &&
             context.getFactHandle() == bfh) ) {
            
            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                WorkingMemoryAction action = new BeliefSystemLogicalCallback( bfh,
                                                                              context,
                                                                              activation,
                                                                              false,
                                                                              true );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            } else {
                // was previous scheduled, so make sure it's a full retract and not an update
                BeliefSystemLogicalCallback callback = ( BeliefSystemLogicalCallback  ) sBeliefSet.getWorkingMemoryAction();
                callback.setUpdate( false );
                callback.setFullyRetract( true );
            }
            
        } else if ( !beliefSet.isEmpty() && beliefSet.getFactHandle().getObject() == payload ) {
            // prime has changed, to update new object                      
           // Equality might have changed on the object, so remove (which uses the handle id) and add back in
           ((NamedEntryPoint)bfh.getEntryPoint()).getObjectStore().updateHandle( bfh,  ((SimpleMode) beliefSet.getFirst()).getObject().getObject() );

            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                // Only schedule if we don't already have one scheduled
                WorkingMemoryAction action = new BeliefSystemLogicalCallback( bfh,
                                                                              context,
                                                                              activation,
                                                                              true,
                                                                              false );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            }
        }
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation<SimpleMode> activation,
                                                  BeliefSet<SimpleMode> beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, null );
    }
}
