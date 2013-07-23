package org.drools.core.reteoo;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

public class ReteRuleTerminalNode extends RuleTerminalNode {

    public ReteRuleTerminalNode() {
    }

    public ReteRuleTerminalNode(int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        super(id, source, rule, subrule, subruleIndex, context);
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        if( unlinkingEnabled ) {
            context = findMostRecentPropagationContext( leftTuple,
                                                        context );
        }

        //check if the rule is not effective or
        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      this,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() )) ) {
            leftTuple.setObject( Boolean.TRUE );
            return;
        }

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        boolean fire = agenda.createActivation( leftTuple,  context,
                                                workingMemory,  this );
        if( fire && !fireDirect ) {
            agenda.addActivation( (AgendaItem) leftTuple.getObject() );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        if( unlinkingEnabled ) {
            context = findMostRecentPropagationContext( leftTuple,
                                                        context );
        }

        InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        // we need the inserted facthandle so we can update the network with new Activation
        Object o = leftTuple.getObject();
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
             (this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() )) ) {
            return;
        }

        // o (AgendaItem) could be null, if this was staged as an insert but not processed, then pushed as a update
        if ( o == null || o  == Boolean.TRUE ) {
            // set to Boolean.TRUE when lock-on-active stops an Activation being created
            leftTuple.setObject( null );
        }
        boolean fire = agenda.createActivation( leftTuple, context, workingMemory, this );
        if ( fire && !isFireDirect() ) {
            agenda.modifyActivation( (AgendaItem) leftTuple.getObject(), false );
        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        Object obj = leftTuple.getObject();


        // activation can be null if the LeftTuple previous propagated into a no-loop
        // or could be true due to lock-on-active blocking activation creation
        if ( obj == null || obj == Boolean.TRUE) {
            return;
        }

        Activation activation = (Activation) obj;
        activation.setMatched( false );

        InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        agenda.cancelActivation( leftTuple,
                                 context,
                                 workingMemory,
                                 activation,
                                 this );

        ((RuleTerminalNodeLeftTuple)leftTuple).setActivationUnMatchListener(null);
    }


    public void attach( BuildContext context ) {
        super.attach( context );
        if (context == null ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory =((InternalRuleBase)workingMemory.getRuleBase()).getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
            getLeftTupleSource().updateSink(this, propagationContext, workingMemory);
        }
    }

}
