package org.drools.common;

import org.drools.KnowledgeBase;
import org.drools.core.util.Iterator;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.Activation;

public class ActivationIterator
    implements
    Iterator {
    private InternalWorkingMemory wm;

    private Iterator              nodeIter;

    private TerminalNode          node;
    
    private LeftTupleIterator leftTupleIter;

    private LeftTuple             currentLeftTuple;

    ActivationIterator() {

    }

    private ActivationIterator(InternalWorkingMemory wm,
                               KnowledgeBase kbase) {
        this.wm = wm;

        nodeIter = TerminalNodeIterator.iterator( kbase );

        // Find the first node with Activations an set it.
        while ( currentLeftTuple == null && (node = (TerminalNode) nodeIter.next()) != null ) {
            if ( !(node instanceof RuleTerminalNode) ) {
                continue;
            }
            leftTupleIter = LeftTupleIterator.iterator( wm, node );            
            this.currentLeftTuple = (LeftTuple) leftTupleIter.next();
        }
    }

    public static ActivationIterator iterator(InternalWorkingMemory wm) {
        return new ActivationIterator( wm,
                                       new KnowledgeBaseImpl( wm.getRuleBase() ) );
    }

    public static ActivationIterator iterator(StatefulKnowledgeSession ksession) {
        return new ActivationIterator( ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory(),
                                       ksession.getKnowledgeBase() );
    }

    public Object next() {
        Activation acc = null;
        if ( this.currentLeftTuple != null ) {
            acc = (Activation) currentLeftTuple.getObject();
            this.currentLeftTuple = ( LeftTuple ) leftTupleIter.next();

            while ( currentLeftTuple == null  && (node = (TerminalNode) nodeIter.next()) != null ) {
                if ( !(node instanceof RuleTerminalNode) ) {
                    continue;
                }                    
                leftTupleIter = LeftTupleIterator.iterator( wm, node );            
                this.currentLeftTuple = (LeftTuple) leftTupleIter.next();
            }
        }

        return acc;
    }

}
