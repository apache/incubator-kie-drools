package org.drools.examples;

import java.io.InputStreamReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.runtime.StatefulKnowledgeSession;

public class FibonacciExample {

    public static void main(final String[] args) throws Exception {

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.addResource( new InputStreamReader( FibonacciExample.class.getResourceAsStream( "Fibonacci.drl" ) ),
                             KnowledgeType.DRL );

        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        final StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        //        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        //        logger.setFileName( "log/fibonacci" );

        session.insert( new Fibonacci( 10 ) );

        session.fireAllRules();

        //        logger.writeToDisk();

        session.dispose(); // Stateful rule session must always be disposed when finished

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
