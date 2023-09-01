/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.tms.beliefsystem.defeasible;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.common.PropagationContext;
import org.drools.tms.LogicalDependency;
import org.drools.tms.agenda.TruthMaintenanceSystemInternalMatch;
import org.drools.tms.beliefsystem.BeliefSet;
import org.drools.tms.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.tms.beliefsystem.jtms.JTMSBeliefSystem;

public class DefeasibleBeliefSystem<M extends DefeasibleMode<M>> extends JTMSBeliefSystem<M>  {

    public DefeasibleBeliefSystem( InternalWorkingMemoryEntryPoint ep, TruthMaintenanceSystem tms ) {
        super(ep, tms);
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new DefeasibleBeliefSet(this, fh);
    }

    @Override
    public BeliefSet<M> insert(M mode, RuleImpl rule, TruthMaintenanceSystemInternalMatch activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
        if ( mode.getLogicalDependency() == null ) {
            LogicalDependency<M> dep = newLogicalDependency( activation, beliefSet, payload, mode );
            mode = dep.getMode();
        }
        return super.insert( mode, rule, activation, payload, beliefSet, context, typeConf );
    }

    public LogicalDependency<M> newLogicalDependency(TruthMaintenanceSystemInternalMatch<M> activation, BeliefSet<M> beliefSet, Object object, Object value) {
        M mode = asMode( value );
        DefeasibleLogicalDependency<M> dep = new DefeasibleLogicalDependency(activation, beliefSet, object, mode);
        mode.setLogicalDependency( dep );
        mode.initDefeats();
        return dep;
    }

    public M asMode(Object value) {
        DefeasibleMode<M> mode;
        if ( value == null ) {
            mode = new DefeasibleMode(MODE.POSITIVE.getId(), this);
        } else if ( value instanceof String ) {
            if ( MODE.POSITIVE.getId().equals( value ) ) {
                mode = new DefeasibleMode(MODE.POSITIVE.getId(), this);
            }   else {
                mode = new DefeasibleMode(MODE.NEGATIVE.getId(), this);
            }
        } else if ( value instanceof DefeasibleMode ) {
            return (M) value;
        } else {
            mode = new DefeasibleMode(((MODE)value).getId(), this);
        }
        return (M) mode;
    }
}
