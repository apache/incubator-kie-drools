package org.drools.testframework;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;

public class TestingEventListenerTest extends RuleUnit {

	public void testInclusive() throws Exception {
		HashSet<String> set = new HashSet<String>();
		set.add("rule1");
		set.add("rule2");

		StatefulSession session  = getWorkingMemory("test_rules.drl");
        TestingEventListener ls = new TestingEventListener(set, session.getRuleBase(), true);

        session.addEventListener(ls);

        session.insert(new Cheese());
        session.fireAllRules();

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule1"));
        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule2"));

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule3"));
        assertTrue(ls.firingCounts.containsKey("rule3"));
        assertFalse(ls.firingCounts.containsKey("rule4"));

        session.insert(new Cheese());
        session.fireAllRules();
        assertEquals(new Integer(2), (Integer) ls.firingCounts.get("rule1"));
        assertEquals(new Integer(2), (Integer) ls.firingCounts.get("rule2"));
        assertEquals(new Integer(2), (Integer) ls.firingCounts.get("rule3"));

	}


	public void testExclusive() throws Exception {
		HashSet<String> set = new HashSet<String>();
		set.add("rule3");


		StatefulSession session  = getWorkingMemory("test_rules.drl");

        TestingEventListener ls = new TestingEventListener(set, session.getRuleBase(), false);


        session.addEventListener(ls);

        session.insert(new Cheese());
        session.fireAllRules();

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule1"));
        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule2"));

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule3"));
        assertTrue(ls.firingCounts.containsKey("rule3"));
        assertFalse(ls.firingCounts.containsKey("rule4"));


	}

	public void testNoFilter() throws Exception {
		HashSet<String> set = new HashSet<String>();


		StatefulSession session  = getWorkingMemory("test_rules.drl");

        TestingEventListener ls = new TestingEventListener(set, session.getRuleBase(), false);

        session.addEventListener(ls);

        session.insert(new Cheese());

        List<String> list = new ArrayList<String>();
        session.setGlobal("list", list);
        session.fireAllRules();

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule1"));
        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule2"));
        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule3"));
        assertEquals(1, list.size());
	}


}
