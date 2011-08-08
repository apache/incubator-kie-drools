package org.drools.common;

import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.reteoo.ReteooRuleBase;
import org.junit.Test;
import static org.junit.Assert.*;

public class AgendaItemTest {
    
    @Test
    public void testAddition() {
        ReteooRuleBase rbase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        StatefulSession wm = rbase.newStatefulSession();
        
        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        AgendaItem item1 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item2 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item3 = agenda.createAgendaItem( null, 0, null, null );
        
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( wm );
        kcontext.setActivation( item1 );
        
        // set blockers
        kcontext.block( item2 );
        kcontext.block( item3 );
        
        assertNull( item1.getBlockers() );
        assertEquals( 2, item1.getBlocked().size() );
        assertEquals( 1, item2.getBlockers().size() );
        assertEquals( 1, item3.getBlockers().size() );
        
        kcontext.reset();
        kcontext.setActivation( item2 );
        kcontext.block( item3 );
        assertEquals( 2, item1.getBlocked().size() );
        
        assertEquals( 1, item2.getBlocked().size() );
        assertEquals( 1, item2.getBlockers().size() );
        assertEquals( 2, item3.getBlockers().size() );        
        
        // now check correctly unblocks when parent activation is retracted
        
        //agenda.createAgendaItem( tuple, salience, context, rtn )-
    }
    
    @Test
    public void testRemoval() {
        ReteooRuleBase rbase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        StatefulSession wm = rbase.newStatefulSession();
        
        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        AgendaItem item1 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item2 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item3 = agenda.createAgendaItem( null, 0, null, null );
        
        // use same data structure as testAddition
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( wm );
        kcontext.setActivation( item1 );
        
        // set blockers 
        kcontext.block( item2 );
        kcontext.block( item3 );
        
         // set blocked
        kcontext.reset();
        kcontext.setActivation( item2 );
        kcontext.block( item3 );
        
        // Check all references are updated correctly when item1 is retracted
        item1.removeAllBlockersAndBlocked(agenda);
        assertEquals( 0, item2.getBlockers().size() );        
        assertEquals( 1, item2.getBlocked().size() );
        assertEquals(1, item3.getBlockers().size());
        assertNull(item3.getBlocked());
        assertSame( item2, ((LogicalDependency)item2.getBlocked().getFirst()).getJustifier() );
        assertSame( item3, ((LogicalDependency)item2.getBlocked().getFirst()).getJustified() );
        
        // now retract item2
        item2.removeAllBlockersAndBlocked(agenda);
        assertEquals( 0, item3.getBlockers().size() );
        assertNull( item3.getBlocked() );
          
        assertNull( item2.getBlocked() );
        assertNull( item2.getBlocked() );
          
        assertNull( item1.getBlocked() );
        assertNull( item1.getBlocked() );
        
        // reblock with item2, so that we can retract item3
        kcontext.block( item3 );
        assertNull( item2.getBlockers() );        
        assertEquals( 1, item2.getBlocked().size() );
        assertEquals(1, item3.getBlockers().size());
        assertNull(item3.getBlocked());
        assertSame( item2, ((LogicalDependency)item2.getBlocked().getFirst()).getJustifier() );
        assertSame( item3, ((LogicalDependency)item2.getBlocked().getFirst()).getJustified() );        
        
        item3.removeAllBlockersAndBlocked(agenda);
        assertNull(item3.getBlockers() );
        assertNull( item3.getBlocked() );
          
        assertEquals( 0, item2.getBlocked().size() );
        assertNull( item2.getBlockers() );
          
        assertNull( item1.getBlocked() );
        assertNull( item1.getBlocked() );        
    }    
    
    @Test
    public void testUnblockAll() {
        ReteooRuleBase rbase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        StatefulSession wm = rbase.newStatefulSession();
        
        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        AgendaItem item1 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item2 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item3 = agenda.createAgendaItem( null, 0, null, null );
        
        // use same data structure as testAddition
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( wm );
        kcontext.setActivation( item1 );
        kcontext.block( item3 );
        
        kcontext.reset();
        kcontext.setActivation( item2 );
        kcontext.block( item3 );

        kcontext.unblockAll( item3 );
            
        assertEquals( 0, item3.getBlockers().size() );
        assertNull( item3.getBlocked() );
          
        assertEquals( 0, item2.getBlocked().size() );
        assertNull( item2.getBlockers() );
          
        assertEquals( 0, item1.getBlocked().size() );
        assertEquals( 0, item1.getBlocked().size() );              
    }        
    
    @Test
    public void testKnowledgeHelperUpdate() {
        ReteooRuleBase rbase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        StatefulSession wm = rbase.newStatefulSession();
        
        DefaultAgenda agenda = ( DefaultAgenda ) wm.getAgenda();
        AgendaItem item1 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item2 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item3 = agenda.createAgendaItem( null, 0, null, null );
        AgendaItem item4 = agenda.createAgendaItem( null, 0, null, null );
        
        // use same data structure as testAddition
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( wm );
        kcontext.setActivation( item1 );
        
        // set blockers 
        kcontext.block( item2 );
        kcontext.block( item3 );
        
         // set blocked
        kcontext.reset();
        kcontext.setActivation( item1 );
        kcontext.block( item4 );
        kcontext.cancelRemainingPreviousLogicalDependencies();
        
        // check only item4 is blocked
        assertEquals( 1, item1.getBlocked().size() );
        assertEquals( item4, ((LogicalDependency)item1.getBlocked().getFirst()).getJustified() );
        
        assertEquals( 0, item2.getBlockers().size() );
        assertEquals( 0, item3.getBlockers().size() );
        
        assertEquals( 1, item4.getBlockers().size() );
                
    }
    
}
