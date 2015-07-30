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

package org.drools.reteoo.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.LogicalDependency;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.RuleEngineOption;

import static org.junit.Assert.*;

public class AgendaItemTest {
    
    @Test
    public void testAddition() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setOption( RuleEngineOption.RETEOO );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        InternalAgenda agenda = ( InternalAgenda ) ksession.getAgenda();
        RuleTerminalNodeLeftTuple item1 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item2 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item3 = new RuleTerminalNodeLeftTuple();
        agenda.createAgendaItem(item1, 0, null, null, null);
        agenda.createAgendaItem(item2, 0, null, null, null);
        agenda.createAgendaItem(item3, 0, null, null, null);
        
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( ksession );
        kcontext.setActivation( item1 );
        
        // set blockers
        kcontext.blockMatch( item2 );
        kcontext.blockMatch( item3 );
        
        assertNull( item1.getBlockers() );
        assertEquals( 2, item1.getBlocked().size() );
        assertEquals( 1, item2.getBlockers().size() );
        assertEquals( 1, item3.getBlockers().size() );
        
        kcontext.reset();
        kcontext.setActivation( item2 );
        kcontext.blockMatch( item3 );
        assertEquals( 2, item1.getBlocked().size() );
        
        assertEquals( 1, item2.getBlocked().size() );
        assertEquals( 1, item2.getBlockers().size() );
        assertEquals( 2, item3.getBlockers().size() );        
        
        // now check correctly unblocks when parent activation is deleted
        
        //agenda.createAgendaItem( tuple, salience, context, rtn )-
    }
    
    @Test
    public void testRemoval() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setOption( RuleEngineOption.RETEOO );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        InternalAgenda agenda = ( InternalAgenda ) ksession.getAgenda();
        RuleTerminalNodeLeftTuple item1 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item2 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item3 = new RuleTerminalNodeLeftTuple();
        agenda.createAgendaItem(item1, 0, null, null, null);
        agenda.createAgendaItem(item2, 0, null, null, null);
        agenda.createAgendaItem(item3, 0, null, null, null);
        
        // use same data structure as testAddition
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( ksession );
        kcontext.setActivation( item1 );
        
        // set blockers 
        kcontext.blockMatch( item2 );
        kcontext.blockMatch( item3 );
        
         // set blocked
        kcontext.reset();
        kcontext.setActivation( item2 );
        kcontext.blockMatch( item3 );
        
        // Check all references are updated correctly when item1 is deleted
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
        kcontext.blockMatch( item3 );
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
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setOption( RuleEngineOption.RETEOO );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        InternalAgenda agenda = ( InternalAgenda ) ksession.getAgenda();
        RuleTerminalNodeLeftTuple item1 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item2 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item3 = new RuleTerminalNodeLeftTuple();
        agenda.createAgendaItem(item1, 0, null, null, null);
        agenda.createAgendaItem(item2, 0, null, null, null);
        agenda.createAgendaItem(item3, 0, null, null, null);
        
        // use same data structure as testAddition
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( ksession );
        kcontext.setActivation( item1 );
        kcontext.blockMatch( item3 );
        
        kcontext.reset();
        kcontext.setActivation( item2 );
        kcontext.blockMatch( item3 );

        kcontext.unblockAllMatches( item3 );
            
        assertEquals( 0, item3.getBlockers().size() );
        assertNull( item3.getBlocked() );
          
        assertEquals( 0, item2.getBlocked().size() );
        assertNull( item2.getBlockers() );
          
        assertEquals( 0, item1.getBlocked().size() );
        assertEquals( 0, item1.getBlocked().size() );              
    }        
    
    @Test
    public void testKnowledgeHelperUpdate() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setOption( RuleEngineOption.RETEOO );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        InternalAgenda agenda = ( InternalAgenda ) ksession.getAgenda();
        RuleTerminalNodeLeftTuple item1 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item2 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item3 = new RuleTerminalNodeLeftTuple();
        RuleTerminalNodeLeftTuple item4 = new RuleTerminalNodeLeftTuple();
        agenda.createAgendaItem(item1, 0, null, null, null);
        agenda.createAgendaItem(item2, 0, null, null, null);
        agenda.createAgendaItem(item3, 0, null, null, null);
        agenda.createAgendaItem(item4, 0, null, null, null);
        
        // use same data structure as testAddition
        DefaultKnowledgeHelper kcontext = new DefaultKnowledgeHelper( ksession );
        kcontext.setActivation( item1 );
        
        // set blockers 
        kcontext.blockMatch( item2 );
        kcontext.blockMatch( item3 );
        
         // set blocked
        kcontext.reset();
        kcontext.setActivation( item1 );
        kcontext.blockMatch( item4 );
        kcontext.cancelRemainingPreviousLogicalDependencies();
        
        // check only item4 is blocked
        assertEquals( 1, item1.getBlocked().size() );
        assertEquals( item4, ((LogicalDependency)item1.getBlocked().getFirst()).getJustified() );
        
        assertEquals( 0, item2.getBlockers().size() );
        assertEquals( 0, item3.getBlockers().size() );
        
        assertEquals( 1, item4.getBlockers().size() );
                
    }
    
}
