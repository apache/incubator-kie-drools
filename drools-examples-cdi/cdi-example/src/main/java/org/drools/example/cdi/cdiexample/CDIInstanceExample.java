package org.drools.example.cdi.cdiexample;

import java.io.PrintStream;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

public class CDIInstanceExample {

    @Inject
    @KSession("ksession-optional")
    private Instance<KieSession> kSession;

    public void go(PrintStream out) {

        KieSession ksession = kSession.get();

        ksession.setGlobal("out", out);
        ksession.insert(new Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        ksession.fireAllRules();
    }

    public static void main(String[] args) {
        Weld w = new Weld();

        WeldContainer wc = w.initialize();
        CDIInstanceExample bean = wc.instance().select(CDIInstanceExample.class).get();
        bean.go(System.out);

        w.shutdown();
    }

}
