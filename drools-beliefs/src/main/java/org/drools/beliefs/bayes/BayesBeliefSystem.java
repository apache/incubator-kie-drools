package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleBeliefSet;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.beliefs.Mode;

public class BayesBeliefSystem implements BeliefSystem<BayesHardEvidence> {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public BayesBeliefSystem(NamedEntryPoint ep,
                             TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    @Override
    public void insert(LogicalDependency<BayesHardEvidence> node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
        boolean wasEmpty = beliefSet.isEmpty();
        boolean wasUndecided = beliefSet.isUndecided();

        beliefSet.add( (LinkedListNode) node.getMode() );

        BayesHardEvidence evidence = (BayesHardEvidence) beliefSet.getFirst();

        PropertyReference propRef = (PropertyReference) node.getObject();

        BayesFact bayesFact = (BayesFact) propRef.getInstance();
        BayesInstance bayesInstance = bayesFact.getBayesInstance();

        FactHandle fh = beliefSet.getFactHandle();

        BayesVariable var = ( BayesVariable ) bayesInstance.getFieldNames().get( propRef.getName() );
        if ( !wasUndecided && beliefSet.isUndecided() ) {
            // was decided, not undecided
            bayesInstance.setDecided(var, false);
            bayesInstance.setLikelyhood( var,null );
        } else {
            // either it was empty, or it was undecided and now decided.
            bayesInstance.setDecided(var, true);
            bayesInstance.setLikelyhood( var, evidence.getDistribution() );
        }
    }

    @Override
    public void delete(LogicalDependency<BayesHardEvidence> node, BeliefSet beliefSet, PropagationContext context) {
        boolean wasUndecided = beliefSet.isUndecided();

        beliefSet.remove( (LinkedListNode) node.getMode() );

//        if ( !wasUndecided && !beliefSet.isUndecided() ) {
//            // was decided before, still decided, so do nothing.
//            return;
//        }

        PropertyReference propRef = (PropertyReference)node.getObject();
        BayesFact bayesFact = (BayesFact) propRef.getInstance();
        BayesInstance bayesInstance = bayesFact.getBayesInstance();
        BayesVariable var = ( BayesVariable ) bayesInstance.getFieldNames().get( propRef.getName() );

        boolean empty = beliefSet.isEmpty();
        if ( empty) {
            // if the last one was just removed,
            // then if there was a conflict it was resolved when the second to last was removed
            bayesInstance.unsetLikelyhood( var );
            return;
        }

        BayesHardEvidence evidence = (BayesHardEvidence) beliefSet.getFirst();

        if ( !wasUndecided && beliefSet.isUndecided() ) {
            // was decided, now undecided
            bayesInstance.setDecided(var, false);
            bayesInstance.unsetLikelyhood( var );
        } else if ( wasUndecided && !beliefSet.isUndecided() ) {
            // was undecided, now decided
            bayesInstance.setDecided(var, true);
            bayesInstance.setLikelyhood( var, evidence.getDistribution() );
        }  // else no change
    }

    @Override
    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new BayesBeliefSet(fh, this);
    }

    @Override
    public LogicalDependency newLogicalDependency(Activation activation, BeliefSet beliefSet, Object object, Object value) {
        BayesHardEvidence mode = ( BayesHardEvidence ) value;
        SimpleLogicalDependency dep = new SimpleLogicalDependency( activation, beliefSet, object, (Mode) value );
        mode.setLogicalDependency( dep );

        return dep;
    }

    @Override
    public void read(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {

    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return null;
    }

}
