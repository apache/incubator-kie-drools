package org.drools.example.cdi.cdiexamplewithinclusion;

import org.drools.example.cdi.cdiexample.Message;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.api.cdi.KSession;
import org.kie.runtime.KieSession;

import javax.inject.Inject;
import java.io.PrintStream;

/**
 * Hello world!
 */
public class CDIExampleWithInclusion {

    @Inject
    @KSession("ksession2")
    KieSession kSession;

    public void go(PrintStream out) {
        kSession.setGlobal("out", out);
        kSession.insert(new Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        kSession.fireAllRules();

        kSession.insert(new Message("Dave", "Open the pod bay doors, HAL."));
        kSession.fireAllRules();
    }

    public static void main(String[] args) {
        Weld w = new Weld();

        WeldContainer wc = w.initialize();
        CDIExampleWithInclusion bean = wc.instance().select(CDIExampleWithInclusion.class).get();
        bean.go(System.out);

        w.shutdown();
    }

}
