package org.drools.example.cdi.cdiexample;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.api.cdi.KSession;
import org.kie.runtime.KieSession;

import javax.inject.Inject;
import java.io.PrintStream;

public class CDIExample {

    @Inject
    @KSession("ksession1")
    KieSession kSession;

    public void go(PrintStream out) {
        kSession.setGlobal("out", out);
        kSession.insert(new Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        kSession.fireAllRules();
    }

    public static void main(String[] args) {
        Weld w = new Weld();

        WeldContainer wc = w.initialize();
        CDIExample bean = wc.instance().select(CDIExample.class).get();
        bean.go(System.out);

        w.shutdown();
    }

}
