package org.drools.games.numberguess;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class NumberGuessMain {

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession ksession = kc.newKieSession( "NumberGuessKS");

        ksession.insert( new GameRules( 25, 5 ) );
        ksession.insert( new Game() );

        ksession.fireAllRules();
    }

}
