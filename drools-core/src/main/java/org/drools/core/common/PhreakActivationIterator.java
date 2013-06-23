package org.drools.core.common;

import org.drools.core.util.Iterator;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.kie.api.KieBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class PhreakActivationIterator
    implements
    Iterator {
    private InternalWorkingMemory wm;

    private Iterator              nodeIter;

    private TerminalNode          node;

    private Iterator<LeftTuple>   leftTupleIter;

    private LeftTuple             currentLeftTuple;

    PhreakActivationIterator() {

    }

    private PhreakActivationIterator(InternalWorkingMemory wm,
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

    public static PhreakActivationIterator iterator(InternalWorkingMemory wm) {
        return new PhreakActivationIterator( wm,
                                       new KnowledgeBaseImpl( wm.getRuleBase() ) );
    }

    public static PhreakActivationIterator iterator(StatefulKnowledgeSession ksession) {
        return new PhreakActivationIterator( ((InternalWorkingMemoryEntryPoint) ksession).getInternalWorkingMemory(),
                                       ksession.getKieBase() );
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
