package org.drools.games.invaders;

import org.drools.games.GameConfiguration;
import org.drools.games.GameUI;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Invaders1Main {

    public static void main(String[] args) {
        new Invaders1Main().init(true);
    }

    public Invaders1Main() {
    }

    public void init(boolean exitOnClose) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession ksession = kc.newKieSession( "Invaders1KS");

        GameConfiguration cong = new GameConfiguration();
        cong.setExitOnClose(exitOnClose);

        GameUI ui = new GameUI(ksession, cong);
        ui.init();

        runKSession(ksession);
    }

    public void runKSession(final KieSession ksession) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                // run forever
                try {
                    ksession.fireUntilHalt();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        });
    }
}
