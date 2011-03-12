/*
 * Copyright 2010 JBoss Inc
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

package org.drools.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;

public class SequentialAgendaTest {
    @Test
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
                                                new RuleTerminalNode(0, null, rule, new GroupElement(), new BuildContext( new ReteooRuleBase("x"), null )  ) );
        return item;
    }
}
