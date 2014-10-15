package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

public class JTMSBeliefSystem<M extends JTMSMode<M>>
        implements
        BeliefSystem<M> {
    public static boolean STRICT = false;

    private TruthMaintenanceSystem tms;

    protected NamedEntryPoint defEP;
    protected NamedEntryPoint negEP;

    public JTMSBeliefSystem(NamedEntryPoint ep,
                            TruthMaintenanceSystem tms) {
        this.defEP = ep;
        this.tms = tms;
        initMainEntryPoints();
    }

    private void initMainEntryPoints() {
        negEP = (NamedEntryPoint) defEP.getWorkingMemoryEntryPoint(MODE.NEGATIVE.getId());
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    public void insert(LogicalDependency<M> node,
                       BeliefSet<M> beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        JTMSBeliefSet jtmsBeliefSet = (JTMSBeliefSet) beliefSet;

        boolean wasEmpty = jtmsBeliefSet.isEmpty();
        boolean wasNegated = jtmsBeliefSet.isNegated();
        boolean wasUndecided = jtmsBeliefSet.isUndecided();

        jtmsBeliefSet.add( node.getMode() );

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

            deleteAfterChangedEntryPoint(node, context, typeConf, jtmsBeliefSet, wasNegated);

        } else {
            if ( wasUndecided && ! jtmsBeliefSet.isUndecided() ) {
                insertBelief( node, typeConf, jtmsBeliefSet, context, wasEmpty, wasNegated, wasUndecided );
            }
        }
    }

    private void deleteAfterChangedEntryPoint(LogicalDependency<M> node, PropagationContext context, ObjectTypeConf typeConf, JTMSBeliefSet<M> jtmsBeliefSet, boolean wasNegated) {
        InternalFactHandle fh;
        if ( wasNegated ) {
            fh = jtmsBeliefSet.getNegativeFactHandle();
            jtmsBeliefSet.setNegativeFactHandle( null );

            ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, context.getRuleOrigin(), node.getJustifier() );
        } else {
            fh = jtmsBeliefSet.getPositiveFactHandle();
            jtmsBeliefSet.setPositiveFactHandle( null );

            NamedEntryPoint nep = (NamedEntryPoint) fh.getEntryPoint() ;
            nep.getEntryPointNode().retractObject( fh, context, typeConf, nep.getInternalWorkingMemory() );
        }
    }

    protected void insertBelief(LogicalDependency<M> node,
                              ObjectTypeConf typeConf,
                              JTMSBeliefSet<M> jtmsBeliefSet,
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

            if ( typeConf == null ) {
                typeConf = getObjectTypeConf(jtmsBeliefSet, false);
            }

            defEP.insert( jtmsBeliefSet.getPositiveFactHandle(),
                          node.getObject(),
                          node.getJustifier().getRule(),
                          node.getJustifier(),
                          typeConf,
                          null );
        }
    }


    public void read(LogicalDependency<M> node,
                     BeliefSet<M> beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        throw new UnsupportedOperationException( "This is not serializable yet" );
    }

    public void delete(LogicalDependency<M> node,
                       BeliefSet<M> beliefSet,
                       PropagationContext context) {
        JTMSBeliefSet<M> jtmsBeliefSet = (JTMSBeliefSet<M>) beliefSet;
        boolean wasUndecided = jtmsBeliefSet.isUndecided();
        boolean wasNegated = jtmsBeliefSet.isNegated();

        // If the prime object is removed, we need to update the FactHandle, and tell the callback to update
        boolean primeChanged = false;

        if ( (jtmsBeliefSet.getPositiveFactHandle() != null && jtmsBeliefSet.getPositiveFactHandle().getObject() == node.getObject()) ||
             (jtmsBeliefSet.getNegativeFactHandle() != null && jtmsBeliefSet.getNegativeFactHandle().getObject() == node.getObject()) ) {
            primeChanged = true;
        }

        beliefSet.remove( node.getMode() );
        if ( beliefSet.isEmpty() ) {
            if ( wasNegated && ! wasUndecided ) {
                defEP.getObjectStore().addHandle( beliefSet.getFactHandle(), beliefSet.getFactHandle().getObject() ); // was negated, so add back in, so main retract works
                InternalFactHandle fh = jtmsBeliefSet.getNegativeFactHandle();
                ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, context.getRuleOrigin(), node.getJustifier() );
            }

            if ( !(context.getType() == PropagationContext.DELETION && context.getFactHandle() == beliefSet.getFactHandle()) ) { // don't start retract, if the FH is already in the process of being retracted
                // do not if the FH is the root of the context, it means it's already in the process of retraction
                InternalFactHandle fh = jtmsBeliefSet.getFactHandle();
                ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, context.getRuleOrigin(), node.getJustifier() );
            }

        } else if ( wasUndecided && !jtmsBeliefSet.isUndecided() ) {
            insertBelief( node,
                          defEP.getObjectTypeConfigurationRegistry().getObjectTypeConf( defEP.getEntryPoint(), node.getObject() ),
                          jtmsBeliefSet,
                          context,
                          false,
                          wasNegated,
                          wasUndecided );
        } else if ( primeChanged ) {
            // we know there must be at least one more of the same type, as they are still in conflict


            if ( ( wasNegated && !jtmsBeliefSet.isNegated() ) || ( !wasNegated && jtmsBeliefSet.isNegated() ) ) {
                // Prime has changed between NEG and POS, so update to correct EntryPoint

                ObjectTypeConf typeConf = getObjectTypeConf(jtmsBeliefSet, wasNegated );
                deleteAfterChangedEntryPoint(node, context, typeConf, jtmsBeliefSet, wasNegated);

                typeConf = null; // must null it, as insertBelief will reset the neg or the pos FH

                insertBelief( node,
                              typeConf,
                              jtmsBeliefSet,
                              context,
                              false,
                              wasNegated,
                              wasUndecided );
            } else {
                String value;
                Object object = null;
                InternalFactHandle fh = null;

                if ( jtmsBeliefSet.isNegated() ) {
                    value = MODE.NEGATIVE.getId();
                    fh = jtmsBeliefSet.getNegativeFactHandle();
                    // Find the new node, and update the handle to it, Negatives iterate from the last
                    for ( JTMSMode entry = (JTMSMode) jtmsBeliefSet.getLast(); entry != null; entry = (JTMSMode) entry.getPrevious() ) {
                        if ( entry.getValue().equals( value ) ) {
                            object = entry.getLogicalDependency().getObject();
                            break;
                        }
                    }
                } else {
                    value = MODE.POSITIVE.getId();
                    fh = jtmsBeliefSet.getPositiveFactHandle();
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
                    ((NamedEntryPoint) fh.getEntryPoint() ).update( fh, true, fh.getObject(), allSetButTraitBitMask(), object.getClass(), null );
                }
            }
        }
    }

    private ObjectTypeConf getObjectTypeConf(JTMSBeliefSet<M> jtmsBeliefSet, boolean neg) {
        InternalFactHandle fh;
        NamedEntryPoint nep;
        ObjectTypeConfigurationRegistry reg;
        ObjectTypeConf typeConf;// after the delete, get the new FH and its type conf
        if ( neg ) {
            fh =  jtmsBeliefSet.getNegativeFactHandle();
        } else {
            fh = jtmsBeliefSet.getPositiveFactHandle();
        }

        nep = (NamedEntryPoint) fh.getEntryPoint();
        reg = nep.getObjectTypeConfigurationRegistry();
        typeConf = reg.getObjectTypeConf( nep.getEntryPoint(), fh.getObject() );
        return typeConf;
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new JTMSBeliefSetImpl( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation<M> activation,
                                                  BeliefSet<M> beliefSet,
                                                  Object object,
                                                  Object value) {
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

        SimpleLogicalDependency dep =  new SimpleLogicalDependency(activation, beliefSet, object, mode);
        mode.setLogicalDependency( dep );

        return dep;
    }
}
