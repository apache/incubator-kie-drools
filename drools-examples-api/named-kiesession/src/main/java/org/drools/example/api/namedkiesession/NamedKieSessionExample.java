package org.drools.example.api.namedkiesession;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.PrintStream;

public class NamedKieSessionExample {

    public void go(PrintStream out) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();

        KieSession kSession = kContainer.newKieSession("ksession1");
        kSession.setGlobal("out", out);
        kSession.insert(new Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        kSession.fireAllRules();
    }


    public static void main(String[] args) {
        new NamedKieSessionExample().go(System.out);
    }

}
