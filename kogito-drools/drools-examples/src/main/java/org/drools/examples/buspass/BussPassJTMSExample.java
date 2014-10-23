package org.drools.examples.buspass;

import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.io.ResourceFactory;

import java.util.Scanner;

public class BussPassJTMSExample {
    public static void main(final String[] args) {
        KieContainer kc;
        KieSession ksession;

        try {
            System.setProperty("drools.negatable", "on");
            kc = KieServices.Factory.get().getKieClasspathContainer();
            ksession = kc.newKieSession("BussPassJTMSKS");
        } catch( Exception e) {
            throw new RuntimeException( e );
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        Person yoda = new Person("Yoda", 15);
        ksession.insert(yoda);
        FactHandle badYodaFh = ksession.insert(new BadBehaviour(yoda));
        ksession.fireAllRules();
        pause();

        Person darth = new Person("Darth", 15);
        ksession.insert(darth);
        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }

    public static void pause() {
        System.out.println( "Pressure enter to continue" );
        Scanner keyboard = new Scanner(System.in);
        keyboard.nextLine();
    }
}
