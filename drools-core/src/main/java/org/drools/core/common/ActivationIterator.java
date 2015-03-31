package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.util.Iterator;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class ActivationIterator
    implements
    Iterator {
    private InternalWorkingMemory wm;

    private Iterator              nodeIter;

    private TerminalNode          node;
    
    private Iterator<LeftTuple>   leftTupleIter;

    private LeftTuple             currentLeftTuple;

    ActivationIterator() {

    }

    private ActivationIterator(InternalWorkingMemory wm,
                               KieBase kbase) {
        this.wm = wm;

        nodeIter = TerminalNodeIterator.iterator( kbase );

        // Find the first node with Activations an set it.
        while ( currentLeftTuple == null && (node = (TerminalNode) nodeIter.next()) != null ) {
            if ( !(node instanceof RuleTerminalNode) ) {
                continue;
            }
            leftTupleIter = LeftTupleIterator.iterator( wm, node );            
            this.currentLeftTuple = leftTupleIter.next();
        }
    }

    public static Iterator iterator(InternalWorkingMemory wm) {
        if (wm.getKnowledgeBase().getConfiguration().isPhreakEnabled()) {
            return PhreakActivationIterator.iterator(wm);
        } else {
            return new ActivationIterator( wm,
                                           wm.getKnowledgeBase() );
        }

    }

    public static Iterator iterator(KieSession ksession) {
        return iterator((InternalWorkingMemoryEntryPoint) ksession);
    }

    public static Iterator iterator(InternalWorkingMemoryEntryPoint ksession) {
        InternalWorkingMemory wm = ksession.getInternalWorkingMemory();
        if (wm.getKnowledgeBase().getConfiguration().isPhreakEnabled()) {
            return PhreakActivationIterator.iterator(wm);
        } else {
            return new ActivationIterator( ksession.getInternalWorkingMemory(),
                                           ((KieSession)ksession).getKieBase() );
        }
    }

    public Object next() {
        Activation acc = null;
        if ( this.currentLeftTuple != null ) {
            Object obj = currentLeftTuple.getObject();
            acc = obj == Boolean.TRUE ? null : (Activation)obj;
            currentLeftTuple = leftTupleIter.next();

            while ( currentLeftTuple == null && (node = (TerminalNode) nodeIter.next()) != null ) {
                if ( !(node instanceof RuleTerminalNode) ) {
                    continue;
                }                    
                leftTupleIter = LeftTupleIterator.iterator( wm, node );            
                this.currentLeftTuple = leftTupleIter.next();
            }
        }

        return acc;
    }

}
