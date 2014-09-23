package org.drools.examples.backwardchaining;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class HouseOfDoomMain {

    public static void main(String[] args) throws Exception {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession( "HouseOfDoomKS");

        ksession.insert( new Location("Office", "House") );
        ksession.insert( new Location("Kitchen", "House") );
        ksession.insert( new Location("Knife", "Kitchen") );
        ksession.insert( new Location("Cheese", "Kitchen") );
        ksession.insert( new Location("Desk", "Office") );
        ksession.insert( new Location("Chair", "Office") );
        ksession.insert( new Location("Computer", "Desk") );
        ksession.insert( new Location("Draw", "Desk") );

        ksession.insert( "go1" );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go2" );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go3" );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( new Location("Key", "Draw") );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go4" );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go5" );
        ksession.fireAllRules();
    }
}
