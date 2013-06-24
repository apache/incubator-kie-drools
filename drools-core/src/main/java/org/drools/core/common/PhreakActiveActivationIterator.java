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
    private DefaultAgenda      agenda;

    List<AgendaItem> items;

    java.util.Iterator itemsIter;

    PhreakActiveActivationIterator() {

    }

    private PhreakActiveActivationIterator(InternalWorkingMemory wm) {
        agenda = (DefaultAgenda) wm.getAgenda();
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
