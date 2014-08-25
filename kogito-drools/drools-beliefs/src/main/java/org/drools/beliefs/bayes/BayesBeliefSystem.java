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
import org.kie.api.runtime.rule.FactHandle;

public class BayesBeliefSystem implements BeliefSystem {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public BayesBeliefSystem(NamedEntryPoint ep,
                             TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    @Override
    public void insert(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
        boolean wasEmpty = beliefSet.isEmpty();
        boolean wasUndecided = beliefSet.isUndecided();

        beliefSet.add(node.getJustifierEntry());

//        if ( !wasUndecided && !beliefSet.isUndecided() ) {
//            // was decided before, still decided, so do nothing.
//            return;
//        }

        node = (( LinkedListEntry<LogicalDependency>) beliefSet.getFirst()).getObject();

        BayesHardEvidence evidence = ( BayesHardEvidence ) node.getValue();
        PropertyReference propRef = (PropertyReference)node.getObject();

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
    public void delete(LogicalDependency node, BeliefSet beliefSet, PropagationContext context) {
        boolean wasUndecided = beliefSet.isUndecided();

        beliefSet.remove( node.getJustifierEntry() );

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

        node = (( LinkedListEntry<LogicalDependency>) beliefSet.getFirst()).getObject();
        BayesHardEvidence evidence = ( BayesHardEvidence ) node.getValue();

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
        return new BayesLogicalDependency(activation, beliefSet, object, value);
    }

    @Override
    public void read(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {

    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return null;
    }

}
