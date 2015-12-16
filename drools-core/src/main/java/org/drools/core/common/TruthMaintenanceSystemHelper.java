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

package org.drools.core.common;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.util.LinkedList;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.kie.internal.runtime.beliefs.Mode;

public class TruthMaintenanceSystemHelper {

    public static void removeLogicalDependencies(final InternalFactHandle handle, final PropagationContext propagationContext ) {
        final BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();
        if ( beliefSet != null && !beliefSet.isEmpty() ) {
            beliefSet.cancel(propagationContext);
        }
    }
    
    public static void clearLogicalDependencies(final InternalFactHandle handle, final PropagationContext propagationContext ) {
        final BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();
        if ( beliefSet != null && !beliefSet.isEmpty() ) {
            beliefSet.clear(propagationContext);
        }
    }    
    
    
    public static <M extends ModedAssertion<M>> void removeLogicalDependencies(final Activation<M> activation,
                                                                               final PropagationContext context,
                                                                               final RuleImpl rule) {
        final LinkedList<LogicalDependency<M>> list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }

        for ( LogicalDependency<M> node = list.getFirst(); node != null; node = node.getNext() ) {
            removeLogicalDependency( node, context );
        }
        activation.setLogicalDependencies( null );
    }

    public static <M extends ModedAssertion<M>> void removeLogicalDependency(final LogicalDependency<M> node,
                                                                             final PropagationContext context) {
        final BeliefSet<M> beliefSet = ( BeliefSet ) node.getJustified();
        beliefSet.getBeliefSystem().delete( node, beliefSet, context );
    }
}
