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

import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.ObjectHashMap;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Iterator;

import static org.drools.core.common.ClassAwareObjectStore.getActualClass;

/**
 * The Truth Maintenance System is responsible for tracking two things. Firstly
 * It maintains a Map to track the classes with the same Equality, using the
 * EqualityKey. The EqualityKey has an internal datastructure which references
 * all the handles which are equal. Secondly It maintains another map tracking
 * the  justificiations for logically asserted facts.
 */
public class TruthMaintenanceSystem {

    private NamedEntryPoint ep;

    private ObjectTypeConfigurationRegistry typeConfReg;

    private ObjectHashMap         equalityKeyMap;

    private BeliefSystem          defaultBeliefSystem;

    private AssertBehaviour       assertBehaviour;

    public TruthMaintenanceSystem() {}

    public TruthMaintenanceSystem(StatefulKnowledgeSessionImpl wm,
                                  NamedEntryPoint ep) {
        this.ep = ep;

        assertBehaviour = ep.getKnowledgeBase().getConfiguration().getAssertBehaviour();

        typeConfReg = ep.getObjectTypeConfigurationRegistry();

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


    public InternalFactHandle insert(Object object,
                                     Object tmsValue,
                                     RuleImpl rule,
                                     Activation activation) {
        ObjectTypeConf typeConf = typeConfReg.getObjectTypeConf( ep.getEntryPoint(),  object );
        if ( !typeConf.isTMSEnabled()) {
            enableTMS(object, typeConf);
        }

        // get the key for other "equal" objects, returns null if none exist
        EqualityKey key = get(object);


        InternalFactHandle fh = null;
        if ( key == null ) {
            // no EqualityKey exits, so we construct one. We know it can only be justified.
            fh =  ep.getHandleFactory().newFactHandle(object, typeConf, ep.getInternalWorkingMemory(), ep );
            key = new EqualityKey( fh, EqualityKey.JUSTIFIED );
            fh.setEqualityKey( key );
            put(key);
        } else {
            fh = key.getLogicalFactHandle();
            if ( fh == null ) {
                // The EqualityKey exists, but this is the first logical object in the key.
                fh =  ep.getHandleFactory().newFactHandle(object, typeConf, ep.getInternalWorkingMemory(), ep );
                key.setLogicalFactHandle( fh );
                fh.setEqualityKey( key );
            }
        }

        // Any logical propagations are handled via the TMS.addLogicalDependency
        fh = addLogicalDependency(fh,
                                  object,
                                  tmsValue,
                                  activation,
                                  activation.getPropagationContext(),
                                  rule,
                                  typeConf);

        return fh;
    }

    public void delete(FactHandle fh) {
        if ( fh == null ) {
            return;
        }
        InternalFactHandle ifh = (InternalFactHandle) fh;
        // This will clear out the logical entries for the FH. However the FH and EqualityKey remain, if it's stated

        // Update the equality key, which maintains a list of stated FactHandles
        final EqualityKey key = ifh.getEqualityKey();

        if ( key.getLogicalFactHandle() != fh ) {
            throw new IllegalArgumentException( "The FactHandle did not originate from TMS : " + fh);
        }

        InternalWorkingMemory wm = ep.getInternalWorkingMemory();

        final PropagationContext propagationContext = ep.getPctxFactory().createPropagationContext( wm.getNextPropagationIdCounter(), PropagationContext.DELETION,
                                                                                                    null, null, ifh,  ep.entryPoint);

        TruthMaintenanceSystemHelper.removeLogicalDependencies( ifh, propagationContext );

    }

    public EqualityKey get(final EqualityKey key) {
        return (EqualityKey) this.equalityKeyMap.get( key );
    }

    public EqualityKey get(final Object object) {
        EqualityKey key = (EqualityKey) this.equalityKeyMap.get( object );

        if ( key == null && assertBehaviour == AssertBehaviour.EQUALITY ) {
            // Edge case: another object X, equivalent (equals+hashcode) to "object" Y
            // has been previously stated. However, if X is a subclass of Y, TMS
            // may have not been enabled yet, and key would be null.
            InternalFactHandle fh = ep.getObjectStore().getHandleForObject(object);
            if ( fh != null ) {
                key = fh.getEqualityKey();
                if ( key == null ) {
                    // we use the FH's Object here, not the inserted object
                    ObjectTypeConf typeC = this.typeConfReg.getObjectTypeConf( ep.getEntryPoint(), fh.getObject() );
                    enableTMS( fh.getObject(), typeC );
                    key = fh.getEqualityKey();
                }
            }
        }

        return key;
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

    public InternalFactHandle addLogicalDependency(final InternalFactHandle handle,
                                                   final Object object,
                                                   final Object value,
                                                   final Activation activation,
                                                   final PropagationContext context,
                                                   final RuleImpl rule,
                                                   final ObjectTypeConf typeConf) {
        return addLogicalDependency( handle, object, value, activation, context, rule, typeConf, false );
    }

    public InternalFactHandle addLogicalDependency(final InternalFactHandle handle,
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
            beliefSet = beliefSystem.insert( node, beliefSet, context, typeConf );
        }
        return beliefSet.getFactHandle();
    }

    public void clear() {
        this.equalityKeyMap.clear();
    }

    public BeliefSystem getBeliefSystem() {
        return defaultBeliefSystem;
    }

    /**
     * TMS will be automatically enabled when the first logical insert happens.
     *
     * We will take all the already asserted objects of the same type and initialize
     * the equality map.
     *
     * @param object the logically inserted object.
     * @param conf the type's configuration.
     */
    private void enableTMS(Object object, ObjectTypeConf conf) {
        Iterator<InternalFactHandle> it = ((ClassAwareObjectStore) ep.getObjectStore()).iterateFactHandles(getActualClass(object));

        while (it.hasNext()) {
            InternalFactHandle handle = it.next();
            if (handle != null && handle.getEqualityKey() == null) {
                EqualityKey key = new EqualityKey(handle);
                handle.setEqualityKey(key);
                key.setStatus(EqualityKey.STATED);
                put(key);
            }
        }

        // Enable TMS for this type.
        conf.enableTMS();
    }
}
