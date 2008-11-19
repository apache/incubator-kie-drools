package org.drools.examples;

import java.io.InputStreamReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.runtime.StatefulKnowledgeSession;

public class FibonacciExample {

    public static void main(final String[] args) throws Exception {

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.addResource( new InputStreamReader( FibonacciExample.class.getResourceAsStream( "Fibonacci.drl" ) ),
                             KnowledgeType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( ksession );
        logger.setFileName( "log/fibonacci" );

        ksession.insert( new Fibonacci( 10 ) );

        ksession.fireAllRules();

        logger.writeToDisk();

        ksession.dispose(); // Stateful rule session must always be disposed when finished

    }

    public static class Fibonacci {
        private int  sequence;

        private long value;

        public Fibonacci() {

        }

        public Fibonacci(final int sequence) {
            this.sequence = sequence;
            this.value = -1;
        }

        public int getSequence() {
            return this.sequence;
        }

        public void setValue(final long value) {
            this.value = value;
        }

        public long getValue() {
            return this.value;
        }

        public String toString() {
            return "Fibonacci(" + this.sequence + "/" + this.value + ")";
        }
    }

}
