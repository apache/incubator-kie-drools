package org.drools.testframework;

import java.io.InputStreamReader;
import java.util.HashSet;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;

import junit.framework.TestCase;

public class TestingEventListenerTest extends TestCase {

	public void testInclusive() throws Exception {
		HashSet set = new HashSet();
		set.add("rule1");
		set.add("rule2");

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader(this.getClass().getResourceAsStream( "test_rules.drl" )) );

        assertFalse(builder.getErrors().toString(), builder.hasErrors());

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage(builder.getPackage());

        TestingEventListener ls = new TestingEventListener(set, rb, true);

        StatefulSession session = rb.newStatefulSession();
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
		HashSet set = new HashSet();
		set.add("rule3");


        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader(this.getClass().getResourceAsStream( "test_rules.drl" )) );

        assertFalse(builder.getErrors().toString(), builder.hasErrors());

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage(builder.getPackage());

        TestingEventListener ls = new TestingEventListener(set, rb, false);

        StatefulSession session = rb.newStatefulSession();
        session.addEventListener(ls);

        session.insert(new Cheese());
        session.fireAllRules();

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule1"));
        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule2"));

        assertEquals(new Integer(1), (Integer) ls.firingCounts.get("rule3"));
        assertTrue(ls.firingCounts.containsKey("rule3"));
        assertFalse(ls.firingCounts.containsKey("rule4"));


	}


}
