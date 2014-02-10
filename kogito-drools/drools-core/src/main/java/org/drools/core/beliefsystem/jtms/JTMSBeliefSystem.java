package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.rule.Rule;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

public class JTMSBeliefSystem
        implements
        BeliefSystem {
    public static boolean          STRICT = false;

    private TruthMaintenanceSystem tms;

    protected NamedEntryPoint        defEP;
    protected NamedEntryPoint        negEP;

    public JTMSBeliefSystem(NamedEntryPoint ep,
                            TruthMaintenanceSystem tms) {
        this.defEP = ep;
        this.tms = tms;
        initMainEntryPoints();
    }

    private void initMainEntryPoints() {
        negEP = (NamedEntryPoint) defEP.getWorkingMemoryEntryPoint( MODE.NEGATIVE.getId() );
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    public void insert(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        JTMSBeliefSet jtmsBeliefSet = (JTMSBeliefSet) beliefSet;

        boolean wasEmpty = jtmsBeliefSet.isEmpty();
        boolean wasNegated = jtmsBeliefSet.isNegated();
        boolean wasUndecided = jtmsBeliefSet.isUndecided();

        jtmsBeliefSet.add( node.getJustifierEntry() );

        if ( wasEmpty ) {
            // Insert Belief
            if ( ! jtmsBeliefSet.isUndecided() ) {
                insertBelief( node, typeConf, jtmsBeliefSet, context, wasEmpty, wasNegated, wasUndecided );
            } else {
                defEP.getObjectStore().removeHandle( jtmsBeliefSet.getFactHandle() );
            }
        } else if ( !wasUndecided && jtmsBeliefSet.isUndecided() ) {
            // Handle Conflict
            if ( STRICT ) {
                throw new IllegalStateException( "FATAL : A fact and its negation have been asserted " + jtmsBeliefSet.getFactHandle().getObject() );
            }

            InternalFactHandle fh;
            boolean fullyRetract;
            if ( wasNegated ) {
                fh = jtmsBeliefSet.getNegativeFactHandle();
                jtmsBeliefSet.setNegativeFactHandle( null );
                fullyRetract = true; // Only fully retract negatives

                ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, context.getRuleOrigin(), node.getJustifier() );
            } else {
                fh = jtmsBeliefSet.getPositiveFactHandle();
                jtmsBeliefSet.setPositiveFactHandle( null );
                fullyRetract = false; // Positives only retract from the rete network, handle must remain

                NamedEntryPoint nep = (NamedEntryPoint) fh.getEntryPoint() ;

//                final ObjectTypeConf typeConf = nep.getObjectTypeConfigurationRegistry().getObjectTypeConf( nep.getEntryPoint(),
//                                                                                                            handle.getObject() );

                nep.getEntryPointNode().retractObject( fh, context, typeConf, nep.getInternalWorkingMemory() );
            }

        } else {
            if ( wasUndecided && ! jtmsBeliefSet.isUndecided() ) {
                insertBelief( node, typeConf, jtmsBeliefSet, context, wasEmpty, wasNegated, wasUndecided );
            }
        }
    }

    protected void insertBelief(LogicalDependency node,
                              ObjectTypeConf typeConf,
                              JTMSBeliefSet jtmsBeliefSet,
                              PropagationContext context,
                              boolean wasEmpty,
                              boolean wasNegated,
                              boolean isUndecided) {
        if ( jtmsBeliefSet.isNegated() && ! jtmsBeliefSet.isUndecided() ) {
            jtmsBeliefSet.setNegativeFactHandle( (InternalFactHandle) negEP.insert( node.getObject() ) );

            // As the neg partition is always stated, it'll have no equality key.
            // However the equality key is needed to stop duplicate LogicalCallbacks, so we manually set it
            // @FIXME **MDP could this be a problem during derialization, where the
            jtmsBeliefSet.getNegativeFactHandle().setEqualityKey( jtmsBeliefSet.getFactHandle().getEqualityKey() );
            jtmsBeliefSet.setPositiveFactHandle( null );
            if ( !(wasNegated || isUndecided) ) {
                defEP.getObjectStore().removeHandle( jtmsBeliefSet.getFactHandle() ); // Make sure the FH is no longer visible in the default ObjectStore
            }
        } else if ( jtmsBeliefSet.isPositive() && ! jtmsBeliefSet.isUndecided() ) {
            jtmsBeliefSet.setPositiveFactHandle( jtmsBeliefSet.getFactHandle() ); // Use the BeliefSet FH for positive facts
            jtmsBeliefSet.setNegativeFactHandle( null );

            if ( !wasEmpty && (wasNegated || isUndecided ) ) {
                jtmsBeliefSet.getFactHandle().setObject( node.getObject() ); // Set the Object, as it may have been a negative initialization, before conflict
                defEP.getObjectStore().addHandle( jtmsBeliefSet.getPositiveFactHandle(), jtmsBeliefSet.getPositiveFactHandle().getObject() ); // Make sure the FH is visible again
            }
            defEP.insert( jtmsBeliefSet.getPositiveFactHandle(),
                          node.getObject(),
                          node.getJustifier().getRule(),
                          node.getJustifier(),
                          typeConf,
                          null );
        }
    }


    public void read(LogicalDependency node,
                     BeliefSet beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        throw new UnsupportedOperationException( "This is not serializable yet" );
    }

    public void delete(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context) {
        JTMSBeliefSet jtmsBeliefSet = (JTMSBeliefSet) beliefSet;
        boolean wasConflicting = jtmsBeliefSet.isUndecided();
        boolean wasNegated = jtmsBeliefSet.isNegated();

        // If the prime object is removed, we need to update the FactHandle, and tell the callback to update
        boolean primeChanged = false;

        if ( (jtmsBeliefSet.getPositiveFactHandle() != null && jtmsBeliefSet.getPositiveFactHandle().getObject() == node.getObject()) ||
             (jtmsBeliefSet.getNegativeFactHandle() != null && jtmsBeliefSet.getNegativeFactHandle().getObject() == node.getObject()) ) {
            primeChanged = true;
        }

        beliefSet.remove( node.getJustifierEntry() );
        if ( beliefSet.isEmpty() ) {
            if ( wasNegated ) {
                defEP.getObjectStore().addHandle( beliefSet.getFactHandle(), beliefSet.getFactHandle().getObject() ); // was negated, so add back in, so main retract works
                InternalFactHandle fh = jtmsBeliefSet.getNegativeFactHandle();
                ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, (Rule) context.getRuleOrigin(), node.getJustifier() );
            }

            if ( !(context.getType() == PropagationContext.DELETION && context.getFactHandle() == beliefSet.getFactHandle()) ) { // don't start retract, if the FH is already in the process of being retracted
                // do not if the FH is the root of the context, it means it's already in the process of retraction
                InternalFactHandle fh = jtmsBeliefSet.getFactHandle();
                ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, (Rule) context.getRuleOrigin(), node.getJustifier() );
            }

        } else if ( wasConflicting && !jtmsBeliefSet.isUndecided() ) {
            insertBelief( node,
                          defEP.getObjectTypeConfigurationRegistry().getObjectTypeConf( defEP.getEntryPoint(), node.getObject() ),
                          jtmsBeliefSet,
                          context,
                          false,
                          wasNegated,
                          wasConflicting );
        } else if ( primeChanged ) {
            // we know there must be at least one more of the same type, as they are still in conflict
            String value;
            InternalFactHandle handle;
            Object object = null;
            if ( jtmsBeliefSet.isNegated() ) {
                if ( ! wasNegated ) {
                    jtmsBeliefSet.setNegativeFactHandle( jtmsBeliefSet.getPositiveFactHandle() );
                    jtmsBeliefSet.setPositiveFactHandle( null );
                }
                value = MODE.NEGATIVE.getId();
                handle = jtmsBeliefSet.getNegativeFactHandle();
                // Find the new node, and update the handle to it, Negatives iterate from the last
                for ( LinkedListEntry<LogicalDependency> entry = (LinkedListEntry<LogicalDependency>) jtmsBeliefSet.getLast(); entry != null; entry = (LinkedListEntry<LogicalDependency>) entry.getPrevious() ) {
                    if ( entry.getObject().getValue().equals( value ) ) {
                        object = entry.getObject().getObject();
                        break;
                    }
                }
            } else {
                if ( wasNegated ) {
                    jtmsBeliefSet.setPositiveFactHandle( jtmsBeliefSet.getNegativeFactHandle() );
                    jtmsBeliefSet.setNegativeFactHandle( null );
                }
                value = MODE.POSITIVE.getId();
                handle = jtmsBeliefSet.getPositiveFactHandle();
                // Find the new node, and update the handle to it, Positives iterate from the front
                for ( LinkedListEntry<LogicalDependency> entry = (LinkedListEntry<LogicalDependency>) jtmsBeliefSet.getFirst(); entry != null; entry = (LinkedListEntry<LogicalDependency>) entry.getNext() ) {
                    if ( entry.getObject().getValue() == null || entry.getObject().getValue().equals( value ) ) {
                        object = entry.getObject().getObject();
                        break;
                    }
                }
            }

            // Equality might have changed on the object, so remove (which uses the handle id) and add back in
            if ( handle.getObject() != object ) {
                ((NamedEntryPoint) handle.getEntryPoint()).getObjectStore().updateHandle( handle, object );
                ((NamedEntryPoint) handle.getEntryPoint() ).update( handle, true, handle.getObject(), Long.MAX_VALUE, Object.class, null );
            }
        }
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new JTMSBeliefSetImpl( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, value );
    }
}
