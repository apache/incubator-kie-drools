package org.drools.serialization.protobuf.iterators;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.reteoo.Tuple;
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

    private Tuple                 currentTuple;

    ActivationIterator() {

    }

    private ActivationIterator(InternalWorkingMemory wm,
                               KieBase kbase) {
        this.wm = wm;

        nodeIter = TerminalNodeIterator.iterator( kbase );

        // Find the first node with Activations an set it.
        while ( currentTuple == null && (node = (TerminalNode) nodeIter.next()) != null ) {
            if ( !(node instanceof RuleTerminalNode) ) {
                continue;
            }
            leftTupleIter = LeftTupleIterator.iterator( wm, node );
            this.currentTuple = leftTupleIter.next();
        }
    }

    public static Iterator iterator(InternalWorkingMemory wm) {
        return PhreakActivationIterator.iterator(wm);
    }

    public static Iterator iterator(KieSession ksession) {
        return iterator((WorkingMemoryEntryPoint) ksession);
    }

    public static Iterator iterator(WorkingMemoryEntryPoint ksession ) {
        ReteEvaluator reteEvaluator = ksession.getReteEvaluator();
        return PhreakActivationIterator.iterator(reteEvaluator);
    }

    public Object next() {
        InternalMatch acc = null;
        if ( this.currentTuple != null ) {
            Object obj = currentTuple.getContextObject();
            acc = obj == Boolean.TRUE ? null : (InternalMatch)obj;
            currentTuple = leftTupleIter.next();

            while ( currentTuple == null && (node = (TerminalNode) nodeIter.next()) != null ) {
                if ( !(node instanceof RuleTerminalNode) ) {
                    continue;
                }                    
                leftTupleIter = LeftTupleIterator.iterator( wm, node );            
                this.currentTuple = leftTupleIter.next();
            }
        }

        return acc;
    }

}
