/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.PhreakRuleTerminalNode;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

public class AlphaTerminalNode extends LeftInputAdapterNode {

    public AlphaTerminalNode() { }

    public AlphaTerminalNode(final int id,
                             final ObjectSource source,
                             final BuildContext context) {
        super(id, source, context);
    }

    @Override
    public void assertObject( InternalFactHandle factHandle, PropagationContext propagationContext, InternalWorkingMemory workingMemory ) {
        InternalAgenda agenda = workingMemory.getAgenda();
        Sink[] sinks = getSinks();
        for (int i = 0; i < sinks.length; i++) {
            TerminalNode rtn = ( TerminalNode ) getSinks()[i];
            RuleAgendaItem agendaItem = getRuleAgendaItem( workingMemory, agenda, rtn, true );
            LeftTuple leftTuple = rtn.createLeftTuple( factHandle, true );
            leftTuple.setPropagationContext( propagationContext );

            if ( rtn.getRule().getAutoFocus() && !agendaItem.getAgendaGroup().isActive() ) {
                agenda.setFocus( agendaItem.getAgendaGroup() );
            }

            PhreakRuleTerminalNode.doLeftTupleInsert( rtn, agendaItem.getRuleExecutor(), agenda, agendaItem, leftTuple );
        }
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        InternalAgenda agenda = workingMemory.getAgenda();
        Sink[] sinks = getSinks();

        for (int i = 0; i < sinks.length; i++) {
            TerminalNode rtn = ( TerminalNode ) getSinks()[i];
            ObjectTypeNode.Id otnId = rtn.getLeftInputOtnId();
            LeftTuple leftTuple = processDeletesFromModify(modifyPreviousTuples, context, workingMemory, otnId);

            RuleAgendaItem agendaItem = getRuleAgendaItem( workingMemory, agenda, rtn, true );
            RuleExecutor executor = agendaItem.getRuleExecutor();

            if ( leftTuple != null && leftTuple.getInputOtnId().equals( otnId ) ) {
                modifyPreviousTuples.removeLeftTuple(partitionId);
                leftTuple.reAdd();
                if ( context.getModificationMask().intersects( rtn.getLeftInferredMask() ) ) {
                    leftTuple.setPropagationContext( context );
                    PhreakRuleTerminalNode.doLeftTupleUpdate( rtn, executor, agenda, leftTuple );
                    if (leftTuple instanceof Activation ) {
                        ((Activation) leftTuple).setActive( true );
                    }
                }
            } else {
                if ( context.getModificationMask().intersects( rtn.getLeftInferredMask() ) ) {
                    leftTuple = rtn.createLeftTuple( factHandle, true );
                    leftTuple.setPropagationContext( context );
                    PhreakRuleTerminalNode.doLeftTupleInsert( rtn, executor, agenda, agendaItem, leftTuple );
                }
            }
        }
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        InternalAgenda agenda = workingMemory.getAgenda();
        leftTuple.setPropagationContext( context );
        TerminalNode rtn = ( TerminalNode ) leftTuple.getTupleSink();
        PhreakRuleTerminalNode.doLeftDelete( agenda, getRuleAgendaItem( workingMemory, agenda, rtn, false ).getRuleExecutor(), leftTuple );
    }

    public static RuleAgendaItem getRuleAgendaItem( InternalWorkingMemory workingMemory, InternalAgenda agenda, TerminalNode rtn, boolean linkPmem ) {
        PathMemory pathMemory = workingMemory.getNodeMemory( rtn );
        if (linkPmem) {
            pathMemory.doLinkRule( agenda );
        }
        return pathMemory.getRuleAgendaItem();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if ( object.getClass() != AlphaTerminalNode.class || this.hashCode() != object.hashCode() ) {
            return false;
        }
        return this.getObjectSource().getId() == ((AlphaTerminalNode)object).getObjectSource().getId() && this.sinkMask.equals( ((AlphaTerminalNode) object).sinkMask );
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
