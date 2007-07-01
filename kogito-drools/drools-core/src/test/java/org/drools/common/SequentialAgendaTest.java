package org.drools.common;

import org.drools.rule.Rule;
import org.drools.spi.Activation;

import junit.framework.TestCase;

public class SequentialAgendaTest extends TestCase {
    public void testgetNext() {
        SequentialAgendaGroupImpl agenda = new SequentialAgendaGroupImpl( "test", null );
        
        agenda.add( createActivation( 5 ) );
        
        agenda.add( createActivation( 49 ) );
        
        agenda.add( createActivation( 108 ) );
        
        agenda.add( createActivation( 320 ) );
        
        agenda.add( createActivation( 1053 ) );
        
        assertEquals( 5, agenda.getNext().getRule().getLoadOrder() );
        assertEquals( 49, agenda.getNext().getRule().getLoadOrder() );
        assertEquals( 108, agenda.getNext().getRule().getLoadOrder() );
        assertEquals( 320, agenda.getNext().getRule().getLoadOrder() );
        assertEquals( 1053, agenda.getNext().getRule().getLoadOrder() );
        assertNull( agenda.getNext() );
        
    }
    
    public Activation createActivation(int index) {
        Rule rule = new Rule( "test rule");
        rule.setLoadOrder( index );
        final AgendaItem item = new AgendaItem( 0,
                                                null,
                                                0,
                                                null,
                                                rule,
                                                null );   
        return item;
    }
}
