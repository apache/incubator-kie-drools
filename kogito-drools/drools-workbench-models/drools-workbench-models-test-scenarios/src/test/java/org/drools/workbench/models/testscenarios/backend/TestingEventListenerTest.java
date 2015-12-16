/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public class TestingEventListenerTest extends RuleUnit {

    @Test
    public void testInclusive() throws Exception {
        HashSet<String> set = new HashSet<String>();
        set.add( "rule1" );
        set.add( "rule2" );

        KieSession session = getKieSession( "test_rules.drl" );

        TestingEventListener ls = new TestingEventListener();
        //TestingEventListener.stubOutRules(set, session.getRuleBase(), true);

        session.addEventListener( ls );

        session.insert( new Cheese() );
        session.fireAllRules( ls.getAgendaFilter( set, true ) );

        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule1" ) );
        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule2" ) );

        //assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule3"));
        assertFalse( ls.firingCounts.containsKey( "rule3" ) );
        assertFalse( ls.firingCounts.containsKey( "rule4" ) );

        session.insert( new Cheese() );
        session.fireAllRules( ls.getAgendaFilter( set, true ) );
        assertEquals( new Integer( 2 ), (Integer) ls.firingCounts.get( "rule1" ) );
        assertEquals( new Integer( 2 ), (Integer) ls.firingCounts.get( "rule2" ) );
        assertFalse( ls.firingCounts.containsKey( "rule3" ) );
        assertEquals( 4, ls.totalFires );

    }

    @Test
    public void testExclusive() throws Exception {
        HashSet<String> set = new HashSet<String>();
        set.add( "rule3" );

        KieSession session = getKieSession( "test_rules.drl" );

        TestingEventListener ls = new TestingEventListener();
        //TestingEventListener.stubOutRules(set, session.getRuleBase(), false);

        session.addEventListener( ls );

        session.insert( new Cheese() );
        session.fireAllRules( ls.getAgendaFilter( set, false ) );

        //assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule1"));
        //assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule2"));

        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule2" ) );
        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule1" ) );
        assertFalse( ls.firingCounts.containsKey( "rule3" ) );
        assertFalse( ls.firingCounts.containsKey( "rule4" ) );

    }

    @Test
    public void testNoFilter() throws Exception {
        HashSet<String> set = new HashSet<String>();

        KieSession session = getKieSession( "test_rules.drl" );

        TestingEventListener ls = new TestingEventListener();
        //TestingEventListener.stubOutRules(set, session.getRuleBase(), false);

        session.addEventListener( ls );

        session.insert( new Cheese() );

        List<String> list = new ArrayList<String>();
        session.setGlobal( "list", list );
        session.fireAllRules( ls.getAgendaFilter( set, false ) );

        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule1" ) );
        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule2" ) );
        assertEquals( new Integer( 1 ), (Integer) ls.firingCounts.get( "rule3" ) );

        String[] summary = ls.getRulesFiredSummary();
        assertEquals( 3, summary.length );
        assertNotNull( summary[ 0 ] );
        assertFalse( summary[ 1 ].equals( "" ) );

        assertEquals( 1, list.size() );
    }

}
