package org.drools.leaps;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.drools.common.DefaultAgenda;
import org.drools.rule.Query;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;

/**
 * 
 * @author Alexander Bagerman
 * 
 */
public class LeapsAgenda extends DefaultAgenda {
    /**
     * 
     */
    private static final long  serialVersionUID = 7985611305408622557L;

    private LeapsWorkingMemory workingMemory;

    public LeapsAgenda(final LeapsWorkingMemory workingMemory) {
        super( workingMemory );
        this.workingMemory = workingMemory;
    }

    public synchronized void fireActivation( final Activation activation )
            throws ConsequenceException {
        if (activation.getRule( ) instanceof Query) {
            // put query results to the working memory location
            this.workingMemory.addToQueryResults( activation.getRule( ).getName( ),
                                                  activation.getTuple( ) );
        }
        else {
            if (activation.getRule( ).getActivationGroup( ) == null
                    || ( activation.getRule( ).getActivationGroup( ) != null && this.getActivationGroup( activation.getRule( )
                                                                                                                   .getActivationGroup( ) )
                                                                                    .isEmpty( ) )) {
                // fire regular rule
                super.fireActivation( activation );
                ((LeapsTuple)activation.getTuple( )).setWasFired( true );
                if (activation.getRule( ).getActivationGroup( ) != null) {
                    this.getActivationGroup( activation.getRule( ).getActivationGroup( ) );
                }
            }
        }
    }
    
    /**
     * to accomodate the difference between rete and leaps in storing 
     * activations. we pull activations from rule to activations map
     * we store in working memory to facilitate activations removal 
     * when rule is removed from the memory
     * 
     */
    public Activation[] getActivations() {
        final List list = this.workingMemory.getActivations();
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }
}
