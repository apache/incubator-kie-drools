package org.drools.games.pong;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.kie.internal.io.ResourceFactory.newClassPathResource;
import static org.kie.api.io.ResourceType.DRL;

public class PongMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new PongMain().init(true);
    }

    public PongMain() {
    }

    public void init(boolean exitOnClose) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession ksession = kc.newKieSession( "PongKS");
        PongConfiguration pconf = new PongConfiguration();
        pconf.setExitOnClose(exitOnClose);
        ksession.setGlobal("pconf", pconf);

        //ksession.getAgenda().getAgendaGroup("Init").setFocus( );

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
