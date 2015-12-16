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

package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Tuple;
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
        if ( this.currentTuple != null ) {
            Object obj = currentTuple.getContextObject();
            acc = obj == Boolean.TRUE ? null : (Activation)obj;
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
