package org.drools.examples.diagnostics;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


/**
 * From http://www.ifitjams.com/start.gif
 */
public class DiagnosticsExample {
    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("DiagnosticsKS");


        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }
}
