package org.drools.examples.fibonacci;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class FibonacciExample {

    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        KieSession ksession = kc.newKieSession("FibonacciKS");

        ksession.insert( new Fibonacci( 50 ) );
        ksession.fireAllRules();

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
