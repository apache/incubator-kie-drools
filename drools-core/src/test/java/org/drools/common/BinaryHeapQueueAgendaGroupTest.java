package org.drools.common;

import static org.junit.Assert.*;

import org.drools.RuleBaseFactory;
import org.junit.Test;

public class BinaryHeapQueueAgendaGroupTest {
    
    @Test
    public void test1() {
        BinaryHeapQueueAgendaGroup queue = new BinaryHeapQueueAgendaGroup("xxx", ( InternalRuleBase ) RuleBaseFactory.newRuleBase());
        
        insertItem( queue, new AgendaItem( 8, null, 0, null, null ) );
        insertItem( queue, new AgendaItem( 6, null, 0, null, null ) );
        insertItem( queue, new AgendaItem( 4, null, 0, null, null ) );
        insertItem( queue, new AgendaItem( 2, null, 0, null, null ) );        
        insertItem( queue, new AgendaItem( 0, null, -100, null, null ) );
        insertItem( queue, new AgendaItem( 5, null, -101, null, null ) );
        insertItem( queue, new AgendaItem( 1, null, -100, null, null ) );
        insertItem( queue, new AgendaItem( 3, null, -101, null, null ) );
        insertItem( queue, new AgendaItem( 7, null, -101, null, null ) );
        insertItem( queue, new AgendaItem( 9, null, -101, null, null ) );        
                
        System.out.println( queue );
        
        while ( !queue.isEmpty() ) {
            System.out.println( queue.getNext() );
        }
        
//        assertEquals( 100, queue.getNext().getSalience() );
//        assertEquals( -100, queue.getNext().getSalience() );
//        assertEquals( -101, queue.getNext().getSalience() );
//        assertEquals( -101, queue.getNext().getSalience() );
    }
    
    private void insertItem(BinaryHeapQueueAgendaGroup queue, AgendaItem item) {
        item.setAgendaGroup( queue );
        queue.add( item );
        
    }
}
