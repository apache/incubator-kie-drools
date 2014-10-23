package org.drools.examples.buspass;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class BussPassGoodExample {
    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("BussPassGoodKS");

        Person p = new Person("Yoda", 15);
        FactHandle fh = ksession.insert(p);
        ksession.fireAllRules();

        p.setAge( 16 );
        ksession.update( fh, p );
        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }
}
