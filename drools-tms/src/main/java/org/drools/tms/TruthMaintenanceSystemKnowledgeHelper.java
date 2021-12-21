/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.tms;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.Mode;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.SimpleMode;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedList;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.drools.tms.beliefsystem.simple.SimpleLogicalDependency;
import org.kie.api.runtime.rule.Match;

public class TruthMaintenanceSystemKnowledgeHelper<T extends ModedAssertion<T>> extends DefaultKnowledgeHelper {

    private LinkedList<LogicalDependency<T>> previousJustified;

    private LinkedList<LogicalDependency<SimpleMode>> previousBlocked;

    public TruthMaintenanceSystemKnowledgeHelper() { }

    public TruthMaintenanceSystemKnowledgeHelper(ReteEvaluator reteEvaluator) {
        super(reteEvaluator);
    }

    public void setActivation(final Activation agendaItem) {
        this.previousJustified = agendaItem.getLogicalDependencies();
        this.previousBlocked = agendaItem.getBlocked();
        super.setActivation(agendaItem);
    }

    public void reset() {
        super.reset();
        this.previousJustified = null;
        this.previousBlocked = null;
    }

    public void blockMatch(Match act) {
        AgendaItem targetMatch = ( AgendaItem ) act;
        // iterate to find previous equal logical insertion
        LogicalDependency<SimpleMode> dep = null;
        if ( this.previousBlocked != null ) {
            for ( dep = this.previousBlocked.getFirst(); dep != null; dep = dep.getNext() ) {
                if ( targetMatch ==  dep.getJustified() ) {
                    this.previousBlocked.remove( dep );
                    break;
                }
            }
        }

        if ( dep == null ) {
            SimpleMode mode = new SimpleMode();
            dep = new SimpleLogicalDependency( activation, targetMatch, mode );
            mode.setObject( dep );
        }
        this.activation.addBlocked( dep );

        super.blockMatch(act);
    }

    public InternalFactHandle insertLogical(Object object, Object value) {
        if ( object == null ) {
            // prevent nulls from being inserted logically
            return null;
        }

        if ( !activation.isMatched() ) {
            // Activation is already unmatched, can't do logical insertions against it
            return null;
        }
        // iterate to find previous equal logical insertion
        LogicalDependency<T> dep = null;
        if ( this.previousJustified != null ) {
            for ( dep = this.previousJustified.getFirst(); dep != null; dep = dep.getNext() ) {
                if ( object.equals( ((BeliefSet)dep.getJustified()).getFactHandle().getObject() ) ) {
                    this.previousJustified.remove( dep );
                    break;
                }
            }
        }

        if ( dep != null ) {
            // Add the previous matching logical dependency back into the list
            this.activation.addLogicalDependency( dep );
            return ( (BeliefSet) dep.getJustified() ).getFactHandle();
        } else {
            // no previous matching logical dependency, so create a new one
            return TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(toStatefulKnowledgeSession()).insert( object, value, this.activation );
        }
    }

    public void cancelRemainingPreviousLogicalDependencies() {
        if ( this.previousJustified != null ) {
            for ( LogicalDependency<T> dep = this.previousJustified.getFirst(); dep != null; dep = dep.getNext() ) {
                TruthMaintenanceSystem.removeLogicalDependency( dep, activation.getPropagationContext() );
            }
        }

        if ( this.previousBlocked != null ) {
            for (LogicalDependency<SimpleMode> dep = this.previousBlocked.getFirst(); dep != null; ) {
                LogicalDependency<SimpleMode> tmp = dep.getNext();
                this.previousBlocked.remove( dep );

                AgendaItem justified = ( AgendaItem ) dep.getJustified();
                justified.getBlockers().remove( dep.getMode());
                if (justified.getBlockers().isEmpty() ) {
                    RuleAgendaItem ruleAgendaItem = justified.getRuleAgendaItem();
                    toStatefulKnowledgeSession().getAgenda().stageLeftTuple(ruleAgendaItem, justified);
                }
                dep = tmp;
            }
        }
    }

    public InternalFactHandle bolster( final Object object, final Object value ) {

        if ( object == null || ! activation.isMatched() ) {
            return null;
        }

        InternalFactHandle handle = getFactHandleFromWM( object );
        NamedEntryPoint ep = (NamedEntryPoint) reteEvaluator.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() );
        ObjectTypeConf otc = ep.getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf( ep.getEntryPoint(), object );

        BeliefSet beliefSet = null;
        if ( handle == null ) {
            handle = RuntimeComponentFactory.get().getFactHandleFactoryService().newFactHandle( object, otc, reteEvaluator, ep );
        }
        if ( handle.getEqualityKey() == null ) {
            handle.setEqualityKey( new EqualityKey( handle, EqualityKey.STATED ) );
        } else {
            beliefSet = handle.getEqualityKey().getBeliefSet();
        }

        BeliefSystem beliefSystem = value instanceof Mode ? ((Mode) value).getBeliefSystem() : TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(toStatefulKnowledgeSession()).getBeliefSystem();
        if ( beliefSet == null ) {
            beliefSet = beliefSystem.newBeliefSet( handle );
            handle.getEqualityKey().setBeliefSet( beliefSet );
        }

        return beliefSystem.insert( beliefSystem.asMode( value ),
                                    activation.getRule(),
                                    activation,
                                    object,
                                    beliefSet,
                                    activation.getPropagationContext(),
                                    otc ).getFactHandle();
    }

}
