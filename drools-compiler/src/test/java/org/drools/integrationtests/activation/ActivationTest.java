package org.drools.integrationtests.activation;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests proper rule activation when the first firing rule modifies or otherwise
 * manipulates with a fact which caused other rules to activate.
 * The activation of other previously activated rules should not be canceled.
 */
public class ActivationTest {

    private StatefulKnowledgeSession statefulSession;
    
    @Before
    public void setUp() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("activation.drl", this.getClass()),
                ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors().toString());
        }

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        this.statefulSession = kbase.newStatefulKnowledgeSession();
    }

    @After
    public void cleanUp() {
        if (this.statefulSession != null) {
            this.statefulSession.dispose();
        }
    }

    /**
     * When the first rule, which fires first, modifies a fact which caused other rules to activate,
     * all these rules should fire.
     */
    @Test
    public void testFireWhenModified() {
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        this.statefulSession.addEventListener(listener);

        this.statefulSession.insert(new ActivationTestKind(ActivationTestKind.Kind.MODIFY));
        this.statefulSession.insert(new ActivationTestFact());
        this.statefulSession.fireAllRules();

        assertTrue(listener.isRuleFired("rule1-modify"));
        assertTrue(listener.isRuleFired("rule2-modify"));
        assertTrue(listener.isRuleFired("rule3-modify"));
    }

    /**
     * When the first rule, which fires first, retracts a fact which caused other rules to activate,
     * all these rules should fire.
     */
    @Test
    public void testFireWhenRetracted() {
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        this.statefulSession.addEventListener(listener);

        this.statefulSession.insert(new ActivationTestKind(ActivationTestKind.Kind.RETRACT));
        this.statefulSession.insert(new ActivationTestFact());
        this.statefulSession.fireAllRules();

        assertTrue(listener.isRuleFired("rule1-retract"));
        assertTrue(listener.isRuleFired("rule2-retract"));
        assertTrue(listener.isRuleFired("rule3-retract"));
    }

    /**
     * When the first rule, which fires first, inserts a fact which caused other rules to activate,
     * all these rules should fire.
     */
    @Test
    public void testFireWhenInserted() {
        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        this.statefulSession.addEventListener(listener);

        this.statefulSession.insert(new ActivationTestKind(ActivationTestKind.Kind.INSERT));
        this.statefulSession.fireAllRules();

        assertTrue(listener.isRuleFired("rule1-insert"));
        assertTrue(listener.isRuleFired("rule2-insert"));
        assertTrue(listener.isRuleFired("rule3-insert"));
    }

    /**
     * Fact used to determine kind of activation test.
     */
    public static class ActivationTestKind {
        
        private final Kind kind;
        
        public ActivationTestKind(final Kind kind) {
            this.kind = kind;
        }

        public Kind getKind() {
            return this.kind;
        }

        /**
         * The kind of Activation test - whether fact modification, retraction
         * or insertion is being tested.
         */
        public enum Kind {
            MODIFY, RETRACT, INSERT;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.kind != null ? this.kind.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ActivationTestKind other = (ActivationTestKind) obj;
            if (this.kind != other.kind) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ActivationTestKind{" + "kind=" + kind + '}';
        }
        
    }

    /**
     * Test fact inserted into KB to test activation.
     */
    public static class ActivationTestFact {
        
        private String value;
        
        public ActivationTestFact() {
            this("default");
        }
        
        public ActivationTestFact(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ActivationTestFact other = (ActivationTestFact) obj;
            if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ActivationTestFact{" + "value=" + value + '}';
        }
        
    }   
}
