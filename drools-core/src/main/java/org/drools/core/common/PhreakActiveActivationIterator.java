/*
 * Copyright 2015 JBoss Inc
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

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.spi.Activation;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.LeftTupleList;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;

public class PhreakActiveActivationIterator
    implements
    Iterator {
    private InternalAgenda     agenda;

    List<AgendaItem> items;

    java.util.Iterator itemsIter;

    PhreakActiveActivationIterator() {

    }

    private PhreakActiveActivationIterator(InternalWorkingMemory wm) {
        agenda = (InternalAgenda) wm.getAgenda();
        items = new ArrayList<AgendaItem>();
        for ( InternalAgendaGroup group : agenda.getAgendaGroupsMap().values() ) {
            for ( Activation act : group.getActivations() ) {
                RuleAgendaItem item = ( RuleAgendaItem ) act;
                LeftTupleList list =  item.getRuleExecutor().getLeftTupleList();
                for ( LeftTuple lt = list.getFirst(); lt != null; lt = (LeftTuple) lt.getNext() ) {
                    items.add( (AgendaItem) lt );
                }
            }
        }
        itemsIter = items.iterator();
    }

    public static PhreakActiveActivationIterator iterator(InternalWorkingMemory wm) {
        return new PhreakActiveActivationIterator( wm );
    }

    public static PhreakActiveActivationIterator iterator(StatefulKnowledgeSession ksession) {
        return new PhreakActiveActivationIterator( ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory() );
    }

    public Object next() {
        if ( itemsIter.hasNext() ) {
            return itemsIter.next();
        } else {
            return null;
        }
    }

}
