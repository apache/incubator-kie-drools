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

package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JTMSBeliefSystem<M extends JTMSMode<M>>
        implements
        BeliefSystem<M> {
    protected static final transient Logger  log    = LoggerFactory.getLogger(JTMSBeliefSystem.class);
    public static                    boolean STRICT = false;

    private   TruthMaintenanceSystem tms;
    protected NamedEntryPoint        ep;

    public JTMSBeliefSystem(NamedEntryPoint ep,
                            TruthMaintenanceSystem tms) {
        this.ep = ep;
        this.tms = tms;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    @Override
    public BeliefSet<M> insert( M mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf ) {
        if ( log.isTraceEnabled() ) {
            log.trace( "TMSInsert {} {}", payload, mode.getValue() );
        }

        JTMSBeliefSet jtmsBeliefSet = (JTMSBeliefSet) beliefSet;
        boolean wasEmpty = jtmsBeliefSet.isEmpty();
        boolean wasNegated = jtmsBeliefSet.isNegated();
        boolean wasDecided = jtmsBeliefSet.isDecided();
        InternalFactHandle fh =  jtmsBeliefSet.getFactHandle();

        jtmsBeliefSet.add( mode );

        if ( !wasEmpty && wasDecided && fh.isNegated() != beliefSet.isNegated() ) {
            // if it was decided, first remove it and re-add it. So it's in the correct map
            ep.getObjectStore().removeHandle(fh);
            fh.setNegated( beliefSet.isNegated() );
            ep.getObjectStore().addHandle(fh, fh.getObject() );
        } else {
            fh.setNegated( beliefSet.isNegated() );
        }


        if ( wasEmpty && jtmsBeliefSet.isDecided()  ) {
            ep.insert(jtmsBeliefSet.getFactHandle(),
                      payload,
                      rule,
                      activation,
                      typeConf,
                      null);
        } else {
            processBeliefSet(rule, activation, payload, context, jtmsBeliefSet, wasDecided,wasNegated, fh);
        }
        return beliefSet;
    }


    public BeliefSet<M> insert(LogicalDependency<M> node,
                               BeliefSet<M> beliefSet,
                               PropagationContext context,
                               ObjectTypeConf typeConf) {
        return insert( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context, typeConf );
    }

    public void read(LogicalDependency<M> node,
                     BeliefSet<M> beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        throw new UnsupportedOperationException( "This is not serializable yet" );
    }

    @Override
    public void delete(LogicalDependency<M> node,
                       BeliefSet<M> beliefSet,
                       PropagationContext context) {
        delete( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context );
    }

    @Override
    public void delete( M mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context ) {
        if ( log.isTraceEnabled() ) {
            log.trace( "TMSDelete {} {}", payload, mode.getValue() );
        }

        JTMSBeliefSet<M> jtmsBeliefSet = (JTMSBeliefSet<M>) beliefSet;
        boolean wasDecided = jtmsBeliefSet.isDecided();
        boolean wasNegated = jtmsBeliefSet.isNegated();

        InternalFactHandle fh =  jtmsBeliefSet.getFactHandle();

        beliefSet.remove( mode );

        if ( wasDecided && fh.isNegated() != beliefSet.isNegated() ) {
            // if it was decided, first remove it and re-add it. So it's in the correct map
            ep.getObjectStore().removeHandle(fh);
            fh.setNegated( beliefSet.isNegated() );
            ep.getObjectStore().addHandle(fh, fh.getObject() );
        } else {
            fh.setNegated( beliefSet.isNegated() );
        }


        if ( beliefSet.isEmpty() && fh.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED ) {
            // the set is empty, so delete form the EP, so things are cleaned up.
            ep.delete(fh, fh.getObject(), getObjectTypeConf(beliefSet), (RuleImpl) context.getRule(), (Activation) context.getLeftTupleOrigin() );
        } else  if ( !(processBeliefSet( rule, activation, payload, context, jtmsBeliefSet, wasDecided, wasNegated, fh) && beliefSet.isEmpty())  ) {
            //  The state of the BS did not change, but maybe the prime did
            if ( fh.getObject() == payload ) {
                // prime, node.object which is the current fh.object,  has changed and object is decided, so update
                String value;
                Object object = null;

                if ( jtmsBeliefSet.isNegated() ) {
                    value = MODE.NEGATIVE.getId();
                    // Find the new node, and update the handle to it, Negatives iterate from the last
                    for ( JTMSMode entry = (JTMSMode) jtmsBeliefSet.getLast(); entry != null; entry = (JTMSMode) entry.getPrevious() ) {
                        if ( entry.getValue().equals( value ) ) {
                            object = entry.getLogicalDependency().getObject();
                            break;
                        }
                    }
                } else {
                    value = MODE.POSITIVE.getId();
                    // Find the new node, and update the handle to it, Positives iterate from the front
                    for ( JTMSMode entry = (JTMSMode) jtmsBeliefSet.getFirst(); entry != null; entry = (JTMSMode) entry.getNext() ) {
                        if ( entry.getValue() == null || entry.getValue().equals( value ) ) {
                            object = entry.getLogicalDependency().getObject();
                            break;
                        }
                    }
                }

                // Equality might have changed on the object, so remove (which uses the handle id) and add back in
                if ( fh.getObject() != object ) {
                    ((NamedEntryPoint) fh.getEntryPoint()).getObjectStore().updateHandle( fh, object );
                    ((NamedEntryPoint) fh.getEntryPoint() ).update( fh, fh.getObject(), allSetButTraitBitMask(), object.getClass(), null );
                }
            }
        }

        if ( beliefSet.isEmpty() ) {
            // if the beliefSet is empty, we must null the logical handle
            EqualityKey key = fh.getEqualityKey();
            key.setLogicalFactHandle( null );
            key.setBeliefSet(null);

            if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                // if it's stated, there will be other handles, so leave it in the TMS
                tms.remove( key );
            }
        }
    }

    private boolean processBeliefSet(RuleImpl rule, Activation activation, Object payload, PropagationContext pctx, JTMSBeliefSet<M> jtmsBeliefSet, boolean wasDecided, boolean wasNegated, InternalFactHandle fh) {
        if ( !wasDecided && jtmsBeliefSet.isDecided()  ) {
            ep.insert(jtmsBeliefSet.getFactHandle(),
                      payload,
                      rule,
                      activation,
                      getObjectTypeConf(jtmsBeliefSet),
                      null);
            return true;
        } else if ( wasDecided && !jtmsBeliefSet.isDecided() ) {
            // Handle Conflict
            if ( STRICT ) {
                throw new IllegalStateException( "FATAL : A fact and its negation have been asserted " + jtmsBeliefSet.getFactHandle().getObject() );
            }

            // was decided, now is not, so must be removed from the network. Leave in EP though, we only delete from that when the set is empty
            ep.delete(fh, fh.getObject(), getObjectTypeConf(jtmsBeliefSet), (RuleImpl) pctx.getRule(), (Activation) pctx.getLeftTupleOrigin() );
            return true;
        } else if (wasNegated != jtmsBeliefSet.isNegated()) {
            // was decided, still is decided by the negation changed. This must be propagated through the engine
            // This does not happen for pure JTMS, but does for DFL.
            final PropagationContext updatePctx = ep.getPctxFactory().createPropagationContext(ep.getInternalWorkingMemory().getNextPropagationIdCounter(), PropagationContext.MODIFICATION,
                                                                                                       (RuleImpl) pctx.getRule(), (pctx.getLeftTupleOrigin() == null) ? null : pctx.getLeftTupleOrigin(),
                                                                                                       fh, ep.getEntryPoint());
            ep.update(fh, fh.getObject(), fh.getObject(),  getObjectTypeConf(jtmsBeliefSet), (RuleImpl) pctx.getRule(), updatePctx );
        }
        return false;
    }

    public void stage(PropagationContext context,
                      BeliefSet<M> beliefSet) {
        InternalFactHandle bfh = beliefSet.getFactHandle();
        // Remove the FH from the network
        ep.delete(bfh, bfh.getObject(), getObjectTypeConf(beliefSet),(RuleImpl) context.getRule(), null);
    }

    public void unstage(PropagationContext context,
                        BeliefSet<M> beliefSet) {
        InternalFactHandle bfh = beliefSet.getFactHandle();

        // Add the FH back into the network
        ep.insert(bfh, bfh.getObject(), (RuleImpl) context.getRule(), null, getObjectTypeConf(beliefSet), null );
    }

    private ObjectTypeConf getObjectTypeConf(BeliefSet<M> jtmsBeliefSet) {
        InternalFactHandle fh = jtmsBeliefSet.getFactHandle();
        ObjectTypeConfigurationRegistry reg;
        ObjectTypeConf typeConf;
        reg = ep.getObjectTypeConfigurationRegistry();
        typeConf = reg.getObjectTypeConf( ep.getEntryPoint(), fh.getObject() );
        return typeConf;
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new JTMSBeliefSetImpl( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation<M> activation,
                                                  BeliefSet<M> beliefSet,
                                                  Object object,
                                                  Object value) {
        JTMSMode<M> mode = asMode( value );
        SimpleLogicalDependency dep =  new SimpleLogicalDependency(activation, beliefSet, object, mode);
        mode.setLogicalDependency( dep );

        return dep;
    }

    @Override
    public M asMode( Object value ) {
        JTMSMode<M> mode;
        if ( value == null ) {
            mode = new JTMSMode(MODE.POSITIVE.getId(), this);
        } else if ( value instanceof String ) {
            if ( MODE.POSITIVE.getId().equals( value ) ) {
                mode = new JTMSMode(MODE.POSITIVE.getId(), this);
            }   else {
                mode = new JTMSMode(MODE.NEGATIVE.getId(), this);
            }
        } else {
            mode = new JTMSMode(((MODE)value).getId(), this);
        }
        return (M) mode;
    }
}
