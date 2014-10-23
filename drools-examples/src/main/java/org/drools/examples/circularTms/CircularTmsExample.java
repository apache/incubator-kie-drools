package org.drools.examples.circularTms;

import org.drools.examples.buspass.Person;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Scanner;

public class CircularTmsExample {
    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("CircularTmsKS");

        FactHandle fh = ksession.insert( "A" );
        ksession.fireAllRules();
        pause();

        ksession.delete( fh );
        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }

    public static void pause() {
        System.out.println( "Pressure enter to contnue" );
        Scanner keyboard = new Scanner(System.in);
        keyboard.nextLine();
    }
}
