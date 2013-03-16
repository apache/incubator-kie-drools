package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSet.MODE;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.SimpleLogicalDependency;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystem.LogicalCallback;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.LinkedListEntry;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

public class JTMSBeliefSystem
        implements
        BeliefSystem {
    public static boolean          STRICT = false;

    private TruthMaintenanceSystem tms;

    private NamedEntryPoint        defEP;
    private NamedEntryPoint        negEP;

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
        boolean wasConflicting = jtmsBeliefSet.isConflicting();

        jtmsBeliefSet.add( node.getJustifierEntry() );

        if ( wasEmpty ) {
            // Insert Belief
            insertBelief( node, typeConf, jtmsBeliefSet, wasNegated, wasConflicting );
        } else if ( !wasConflicting && jtmsBeliefSet.isConflicting() ) {
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
            } else {
                fh = jtmsBeliefSet.getPositiveFactHandle(); // we can't lose the postive handle
                jtmsBeliefSet.setPositiveFactHandle( null );
                fullyRetract = false; // Positives only retract from the rete network, handle must remain                
            }

            retractOrUpdateBelief( node, context, fh, false, fullyRetract );

        }
    }

    private void insertBelief(LogicalDependency node,
                              ObjectTypeConf typeConf,
                              JTMSBeliefSet jtmsBeliefSet,
                              boolean wasNegated,
                              boolean wasConflicting) {
        if ( jtmsBeliefSet.isNegated() ) {
            jtmsBeliefSet.setNegativeFactHandle( (InternalFactHandle) negEP.insert( node.getObject() ) );
            
            // As the neg partition is always stated, it'll have no equality key.
            // However the equality key is needed to stop duplicate LogicalCallbacks, so we manually set it
            // **MDP could this be a problem during derialization, where the 
            jtmsBeliefSet.getNegativeFactHandle().setEqualityKey( jtmsBeliefSet.getFactHandle().getEqualityKey() ); 
            jtmsBeliefSet.setPositiveFactHandle( null );
            if ( !(wasNegated || wasConflicting) ) {
                defEP.getObjectStore().removeHandle( jtmsBeliefSet.getFactHandle() ); // Make sure the FH is no longer visible in the default ObjectStore
            }
        } else {
            jtmsBeliefSet.setPositiveFactHandle( jtmsBeliefSet.getFactHandle() ); // Use the BeliefSet FH for positive facts
            jtmsBeliefSet.setNegativeFactHandle( null );

            if ( wasNegated || wasConflicting ) {
                jtmsBeliefSet.getFactHandle().setObject( node.getObject() ); // Set the Object, as it may have been a negative initialization, before conflict
                defEP.getObjectStore().addHandle( jtmsBeliefSet.getPositiveFactHandle(), jtmsBeliefSet.getPositiveFactHandle().getObject() ); // Make sure the FH is visible again
            }
            defEP.insert( jtmsBeliefSet.getPositiveFactHandle(),
                          node.getObject(),
                          node.getJustifier().getRule(),
                          node.getJustifier(),
                          typeConf );
        }
    }

    private void retractOrUpdateBelief(LogicalDependency node,
                                       PropagationContext context,
                                       InternalFactHandle fh,
                                       boolean update,
                                       boolean fullyRetract) {
        JTMSBeliefSet jtmsBeliefSet = ( JTMSBeliefSet ) fh.getEqualityKey().getBeliefSet();
        if ( jtmsBeliefSet.getWorkingMemoryAction() == null ) {
            // doesn't exist, so create it
            WorkingMemoryAction action = new TruthMaintenanceSystem.LogicalCallback( fh,
                                                                                     context,
                                                                                     node.getJustifier(),
                                                                                     update,
                                                                                     fullyRetract ); // Only negative is fully retracted.
            ((NamedEntryPoint) fh.getEntryPoint()).enQueueWorkingMemoryAction( action );
        } else {
            // it exists (update required due to previous change in prime), so just update it's actions
            LogicalCallback callback = ( LogicalCallback ) jtmsBeliefSet.getWorkingMemoryAction();
            callback.setFullyRetract( fullyRetract );
            callback.setUpdate( update );
        }
    }

    public void read(LogicalDependency node,
                     BeliefSet beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        throw new UnsupportedOperationException( "This is not serializale yet" );
    }

    public void delete(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context) {
        JTMSBeliefSet jtmsBeliefSet = (JTMSBeliefSet) beliefSet;
        boolean wasConflicting = jtmsBeliefSet.isConflicting();
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
                retractOrUpdateBelief( node, context, jtmsBeliefSet.getNegativeFactHandle(), false, true );
            }

            if ( !(context.getType() == PropagationContext.DELETION && context.getFactHandle() == beliefSet.getFactHandle()) ) { // don't start retract, if the FH is already in the process of being retracted
                // do not if the FH is the root of the context, it means it's already in the process of retraction
                retractOrUpdateBelief( node, context, (InternalFactHandle) jtmsBeliefSet.getFactHandle(), false, true );
            }
        } else if ( wasConflicting && !jtmsBeliefSet.isConflicting() ) {
            insertBelief( node,
                          defEP.getObjectTypeConfigurationRegistry().getObjectTypeConf( defEP.getEntryPoint(), node.getObject() ),
                          jtmsBeliefSet,
                          wasNegated,
                          wasConflicting );
        } else if ( primeChanged ) {
            // we know there must be at least one more of the same type, as they are still in conflict
            String value;
            InternalFactHandle handle;
            Object object = null;
            if ( jtmsBeliefSet.isNegated() ) {
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
                value = MODE.POSITIVE.getId();
                handle = jtmsBeliefSet.getPositiveFactHandle();
                // Find the new node, and update the handle to it, Positives iterate from the front
                for ( LinkedListEntry<LogicalDependency> entry = (LinkedListEntry<LogicalDependency>) jtmsBeliefSet.getFirst(); entry != null; entry = (LinkedListEntry<LogicalDependency>) entry.getNext() ) {
                    if ( entry.getObject().getValue().equals( value ) ) {
                        object = entry.getObject().getObject();
                        break;
                    }
                }
            }

            // Equality might have changed on the object, so remove (which uses the handle id) and add back in
            ((NamedEntryPoint) handle.getEntryPoint()).getObjectStore().updateHandle( handle, object );

            retractOrUpdateBelief( node, context, handle, true, false ); // fully retract must be false, as this is an update
        }
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new JTMSBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, value );
    }
}
