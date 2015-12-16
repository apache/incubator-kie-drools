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

package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.FactHandle;

public class BayesBeliefSystem<M extends BayesHardEvidence<M>> implements BeliefSystem<M> {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public BayesBeliefSystem(NamedEntryPoint ep,
                             TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    @Override
    public BeliefSet<M> insert(LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
        return insert( node.getMode(),
                       node.getJustifier().getRule(),
                       node.getJustifier(),
                       node.getObject(),
                       beliefSet,
                       context,
                       typeConf );
    }

    @Override
    public BeliefSet<M> insert( M mode, RuleImpl rule, Activation activation, Object ldPayload, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf ) {
        boolean wasEmpty = beliefSet.isEmpty();
        boolean wasDecided = beliefSet.isDecided();

        beliefSet.add( mode );

        BayesHardEvidence<M> evidence = beliefSet.getFirst();

        PropertyReference propRef = (PropertyReference) ldPayload;

        BayesFact bayesFact = (BayesFact) propRef.getInstance();
        BayesInstance bayesInstance = bayesFact.getBayesInstance();

        FactHandle fh = beliefSet.getFactHandle();

        BayesVariable var = ( BayesVariable ) bayesInstance.getFieldNames().get( propRef.getName() );
        if ( wasDecided && !beliefSet.isDecided() ) {
            // was decided, not undecided
            bayesInstance.setDecided(var, false);
            bayesInstance.setLikelyhood( var,null );
        } else {
            // either it was empty, or it was undecided and now decided.
            bayesInstance.setDecided(var, true);
            bayesInstance.setLikelyhood( var, evidence.getDistribution() );
        }
        return beliefSet;
    }

    @Override
    public void delete(LogicalDependency<M> node,
                       BeliefSet<M> beliefSet,
                       PropagationContext context) {
        delete( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context );
    }

    @Override
    public void delete( M mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context ) {
        boolean wasDecided = beliefSet.isDecided();

        beliefSet.remove( mode );

//        if ( !wasUndecided && !beliefSet.isDecided() ) {
//            // was decided before, still decided, so do nothing.
//            return;
//        }

        PropertyReference propRef = (PropertyReference) payload;
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

        BayesHardEvidence<M> evidence = beliefSet.getFirst();

        if ( wasDecided && !beliefSet.isDecided() ) {
            // was decided, now undecided
            bayesInstance.setDecided(var, false);
            bayesInstance.unsetLikelyhood( var );
        } else if ( !wasDecided && beliefSet.isDecided() ) {
            // was undecided, now decided
            bayesInstance.setDecided(var, true);
            bayesInstance.setLikelyhood( var, evidence.getDistribution() );
        }  // else no change
    }

    @Override
    public void stage(PropagationContext context, BeliefSet<M> beliefSet) {

    }

    @Override
    public void unstage(PropagationContext context, BeliefSet<M> beliefSet) {

    }

    @Override
    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new NonConflictingModeSet(fh, this);
    }

    @Override
    public LogicalDependency<M> newLogicalDependency(Activation<M> activation, BeliefSet<M> beliefSet, Object object, Object value) {
        BayesHardEvidence<M> mode = (M) value;
        SimpleLogicalDependency dep = new SimpleLogicalDependency( activation, beliefSet, object, (M) value );
        mode.setLogicalDependency( dep );

        return dep;
    }

    @Override
    public void read(LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf) {

    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return null;
    }

    @Override
    public M asMode( Object value ) {
        return (M) value;
    }

}
