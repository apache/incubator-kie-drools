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

package org.drools.tms.beliefsystem.simple;

import java.io.IOException;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.tms.TruthMaintenanceSystemEqualityKey;
import org.drools.tms.beliefsystem.BeliefSet;
import org.drools.core.common.InternalFactHandle;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.common.PropagationContext;

import static org.drools.base.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

public class BeliefSystemLogicalCallback extends PropagationEntry.AbstractPropagationEntry implements WorkingMemoryAction {

    protected InternalFactHandle handle;
    protected PropagationContext context;
    protected InternalMatch internalMatch;

    protected boolean update;
    protected boolean fullyRetract;

    public BeliefSystemLogicalCallback() {

    }

    public BeliefSystemLogicalCallback(final InternalFactHandle handle,
        final PropagationContext context,
        final InternalMatch internalMatch,
        final boolean update,
        final boolean fullyRetract) {
        this.handle = handle;
        this.context = context;
        this.internalMatch = internalMatch;
        this.update = update;
        this.fullyRetract = fullyRetract;
    }

    public BeliefSystemLogicalCallback(MarshallerReaderContext context) throws IOException {
        this.handle = context.getHandles().get( context.readLong() );
        this.context = context.getPropagationContexts().get( context.readLong() );
        this.internalMatch = (InternalMatch) context.getTerminalTupleMap().get(context.readInt());
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isFullyRetract() {
        return fullyRetract;
    }

    public void setFullyRetract(boolean fullyRetract) {
        this.fullyRetract = fullyRetract;
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        NamedEntryPoint nep = (NamedEntryPoint) handle.getEntryPoint(reteEvaluator) ;

        BeliefSet bs = ((TruthMaintenanceSystemEqualityKey)handle.getEqualityKey()).getBeliefSet();
        bs.setWorkingMemoryAction( null );

        if ( update ) {
            if ( !bs.isEmpty() ) {
                // We need the isEmpty check, in case the BeliefSet was made empty (due to retract) after this was scheduled
                nep.update( handle, handle.getObject(), allSetButTraitBitMask(), Object.class, null );
            }
        } else  {
            if ( fullyRetract ) {
                nep.delete( this.handle, context.getRuleOrigin(), this.internalMatch.getTuple().getTupleSink());
            } else {
                ObjectTypeConf typeConf = nep.getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf( nep.getEntryPoint(), handle.getObject() );
                nep.getEntryPointNode().retractObject( handle, context, typeConf, reteEvaluator );
            }
        }
    }
}
