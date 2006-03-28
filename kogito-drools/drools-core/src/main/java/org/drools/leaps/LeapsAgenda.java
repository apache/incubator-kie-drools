package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import org.drools.common.Agenda;
import org.drools.rule.Query;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;

/**
 * @author Alexander Bagerman
 * 
 */
public class LeapsAgenda extends Agenda {
    /**
     * 
     */
    private static final long serialVersionUID = 7985611305408622557L;

    private WorkingMemoryImpl workingMemory;

    public LeapsAgenda(WorkingMemoryImpl workingMemory) {
        super( workingMemory );
        this.workingMemory = workingMemory;
    }

    public synchronized void fireActivation(Activation activation) throws ConsequenceException {
        if ( activation.getRule() instanceof Query ) {
            // put query results to the working memory location
            this.workingMemory.addToQueryResults( activation.getRule().getName(),
                                                  activation.getTuple() );
        } else {
            // fire regular rule
            super.fireActivation( activation );
            // and remove tuple from
            LeapsTuple tuple = (LeapsTuple) activation.getTuple();
            Class[] classes = tuple.getLeapsRule().getExistsNotColumnsClasses();
            for ( int i = 0, length = classes.length; i < length; i++ ) {
                workingMemory.getFactTable( classes[i] ).removeTuple( tuple );
            }
        }
    }
}
