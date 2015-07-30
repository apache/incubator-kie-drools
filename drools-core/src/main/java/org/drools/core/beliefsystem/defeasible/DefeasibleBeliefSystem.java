/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

public class DefeasibleBeliefSystem<M extends DefeasibleMode<M>> extends JTMSBeliefSystem<M>  {

    public DefeasibleBeliefSystem(NamedEntryPoint ep, TruthMaintenanceSystem tms) {
        super(ep, tms);
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new DefeasibleBeliefSet(this, fh);
    }

    @Override
    public BeliefSet<M> insert( M mode, RuleImpl rule, Activation activation, Object payload, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf ) {
        if ( mode.getLogicalDependency() == null ) {
            LogicalDependency<M> dep = newLogicalDependency( activation, beliefSet, payload, mode );
            mode = dep.getMode();
        }
        return super.insert( mode, rule, activation, payload, beliefSet, context, typeConf );
    }

    public LogicalDependency<M> newLogicalDependency(Activation<M> activation, BeliefSet<M> beliefSet, Object object, Object value) {
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
