package org.drools.examples.diagnostics;

import org.drools.examples.buspass.Person;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class DiagnosticsExample {
    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("DiagnosticsKS");


        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }
}
