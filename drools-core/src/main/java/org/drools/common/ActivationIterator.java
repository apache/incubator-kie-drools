package org.drools.common;

import org.drools.core.util.Iterator;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.spi.Activation;
import org.kie.KieBase;
import org.kie.runtime.StatefulKnowledgeSession;

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
                               KieBase kbase) {
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
        return new ActivationIterator( ((InternalWorkingMemoryEntryPoint) ksession).getInternalWorkingMemory(),
                                       ksession.getKieBase() );
    }

    public Object next() {
        Activation acc = null;
        if ( this.currentLeftTuple != null ) {
            Object obj = currentLeftTuple.getObject();
            acc = obj == Boolean.TRUE ? null : (Activation)obj;
            currentLeftTuple = ( LeftTuple ) leftTupleIter.next();

            while ( currentLeftTuple == null && (node = (TerminalNode) nodeIter.next()) != null ) {
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
