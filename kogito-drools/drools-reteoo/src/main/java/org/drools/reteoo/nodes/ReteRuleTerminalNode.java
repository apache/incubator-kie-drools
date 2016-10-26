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

package org.drools.reteoo.nodes;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

public class ReteRuleTerminalNode extends RuleTerminalNode {

    public ReteRuleTerminalNode() {
    }

    public ReteRuleTerminalNode(int id, LeftTupleSource source, RuleImpl rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        super(id, source, rule, subrule, subruleIndex, context);
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        //check if the rule is not effective or
        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      this,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.equals( context.getTerminalNodeOrigin() )) ) {
            leftTuple.setContextObject( Boolean.TRUE );
            return;
        }

        final InternalAgenda agenda = workingMemory.getAgenda();

        boolean fire = agenda.createActivation( leftTuple,  context,
                                                workingMemory,  this );
        if( fire && !fireDirect ) {
            agenda.addActivation( (AgendaItem) leftTuple.getContextObject() );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        InternalAgenda agenda = workingMemory.getAgenda();

        // we need the inserted facthandle so we can update the network with new Activation
        Object o = leftTuple.getContextObject();
        if ( o != Boolean.TRUE) {  // would be true due to lock-on-active blocking activation creation
            AgendaItem match = (AgendaItem) o;
            if ( match != null && match.isQueued() ) {
                // already activated, do nothing
                // although we need to notify the inserted Activation, as it's declarations may have changed.
                agenda.modifyActivation( match, true );
                return;
            }
        }

        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      this,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.equals( context.getTerminalNodeOrigin() )) ) {
            return;
        }

        // o (AgendaItem) could be null, if this was staged as an insert but not processed, then pushed as a update
        if ( o == null || o  == Boolean.TRUE ) {
            // set to Boolean.TRUE when lock-on-active stops an Activation being created
            leftTuple.setContextObject( null );
        }
        boolean fire = agenda.createActivation( leftTuple, context, workingMemory, this );
        if ( fire && !isFireDirect() ) {
            agenda.modifyActivation( (AgendaItem) leftTuple.getContextObject(), false );
        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        Object obj = leftTuple.getContextObject();


        // activation can be null if the LeftTuple previous propagated into a no-loop
        // or could be true due to lock-on-active blocking activation creation
        if ( obj == null || obj == Boolean.TRUE) {
            return;
        }

        Activation activation = (Activation) obj;
        activation.setMatched( false );

        InternalAgenda agenda = workingMemory.getAgenda();

        agenda.cancelActivation( leftTuple,
                                 context,
                                 activation,
                                 this );

        ((RuleTerminalNodeLeftTuple)leftTuple).setActivationUnMatchListener(null);
    }

    public void cancelMatch(AgendaItem match, InternalWorkingMemoryActions workingMemory) {
        match.cancel();
        if ( match.isQueued() ) {
            match.getTuple().retractTuple( match.getPropagationContext(), workingMemory );
        }
    }


    public void attach( BuildContext context ) {
        super.attach( context );
        if (context == null ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.Type.RULE_ADDITION, null, null, null);
            getLeftTupleSource().updateSink(this, propagationContext, workingMemory);
        }
    }



}
