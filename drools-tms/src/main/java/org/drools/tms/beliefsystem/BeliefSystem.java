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

package org.drools.tms.beliefsystem;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.tms.LogicalDependency;
import org.drools.tms.agenda.TruthMaintenanceSystemActivation;

public interface BeliefSystem<M extends ModedAssertion<M>> {
    
    /**
     * TypeConf is already available, so we pass it, to avoid additional lookups
     */
    BeliefSet<M> insert(LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf);

    BeliefSet<M> insert( M mode, RuleImpl rule, TruthMaintenanceSystemActivation activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf);

    /**
     * The typeConf has not yet been looked up, so we leave it to the implementation to decide if it needs it or not.
     */
    void delete(LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context);
    
    void delete(M mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context);

    BeliefSet newBeliefSet(InternalFactHandle fh);
    
    LogicalDependency newLogicalDependency(TruthMaintenanceSystemActivation<M> activation, BeliefSet<M> beliefSet, Object object, Object value);

    void read(LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf);

    void stage(PropagationContext context, BeliefSet<M> beliefSet);

    void unstage(PropagationContext context, BeliefSet<M> beliefSet);
    
    TruthMaintenanceSystem getTruthMaintenanceSystem();

    M asMode( Object value );
}
