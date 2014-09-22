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

package org.drools.core.common;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.ObjectHashMap;
import org.kie.internal.runtime.beliefs.Mode;

/**
 * The Truth Maintenance System is responsible for tracking two things. Firstly
 * It maintains a Map to track the classes with the same Equality, using the
 * EqualityKey. The EqualityKey has an internal datastructure which references
 * all the handles which are equal. Secondly It maintains another map tracking
 * the  justificiations for logically asserted facts.
 */
public class TruthMaintenanceSystem {

    private StatefulKnowledgeSessionImpl wm;

    private ObjectHashMap         equalityKeyMap;

    private BeliefSystem          defaultBeliefSystem;

    public TruthMaintenanceSystem() {}

    public TruthMaintenanceSystem(StatefulKnowledgeSessionImpl wm,
                                  NamedEntryPoint ep) {
        this.wm = wm;

        //this.justifiedMap = new ObjectHashMap();
        this.equalityKeyMap = new ObjectHashMap();
        this.equalityKeyMap.setComparator( EqualityKeyComparator.getInstance() );


        defaultBeliefSystem = wm.getKnowledgeBase().getConfiguration().getComponentFactory().getBeliefSystemFactory().createBeliefSystem(wm.getSessionConfiguration().getBeliefSystemType(), ep, this);
    }

    public ObjectHashMap getEqualityKeyMap() {
        return this.equalityKeyMap;
    }

    public Object put(final EqualityKey key) {
        return this.equalityKeyMap.put( key,
                                   key,
                                   false );
    }

    public EqualityKey get(final EqualityKey key) {
        return (EqualityKey) this.equalityKeyMap.get( key );
    }

    public EqualityKey get(final Object object) {
        return (EqualityKey) this.equalityKeyMap.get( object );
    }

    public EqualityKey remove(final EqualityKey key) {
        return (EqualityKey) this.equalityKeyMap.remove( key );
    }

    /**
     * Adds a justification for the FactHandle to the justifiedMap.
     *
     * @param handle
     * @param activation
     * @param context
     * @param rule
     * @param typeConf 
     */
    public void readLogicalDependency(final InternalFactHandle handle,
                                      final Object object,
                                      final Object value,
                                      final Activation activation,
                                      final PropagationContext context,
                                      final RuleImpl rule,
                                      final ObjectTypeConf typeConf) {
        addLogicalDependency( handle, object, value, activation, context, rule, typeConf, true );
    }

    public void addLogicalDependency(final InternalFactHandle handle,
                                     final Object object,
                                     final Object value,
                                     final Activation activation,
                                     final PropagationContext context,
                                     final RuleImpl rule,
                                     final ObjectTypeConf typeConf) {
        addLogicalDependency( handle, object, value, activation, context, rule, typeConf, false );
    }

    public void addLogicalDependency(final InternalFactHandle handle,
                                     final Object object,
                                     final Object value,
                                     final Activation activation,
                                     final PropagationContext context,
                                     final RuleImpl rule,
                                     final ObjectTypeConf typeConf,
                                     final boolean read) {
        BeliefSystem beliefSystem = defaultBeliefSystem;
        if ( value != null && value instanceof Mode & !( value instanceof SimpleMode ) ) {
            Mode mode = (Mode) value;
            beliefSystem = (BeliefSystem) mode.getBeliefSystem();
        }

        BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();
        if ( beliefSet == null ) {
            if ( context.getType() == PropagationContext.MODIFICATION ) {
                // if this was a  update, chances  are its trying  to retract a logical assertion
            }
            beliefSet = beliefSystem.newBeliefSet( handle );
            handle.getEqualityKey().setBeliefSet( beliefSet );
        }

        final LogicalDependency node = beliefSystem.newLogicalDependency( activation, beliefSet, object, value );
        activation.getRule().setHasLogicalDependency( true );

        activation.addLogicalDependency( node );


        if ( read ) {
            // used when deserialising
            beliefSystem.read( node, beliefSet, context, typeConf );
        } else {
            beliefSystem.insert( node, beliefSet, context, typeConf );
        }
    }

    public void clear() {
        this.equalityKeyMap.clear();
    }

    public BeliefSystem getBeliefSystem() {
        return defaultBeliefSystem;
    } 
}
