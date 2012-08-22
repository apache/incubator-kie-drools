package org.drools.games.pong;

import static org.drools.builder.ResourceType.DRL;
import static org.drools.io.ResourceFactory.newClassPathResource;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.ConsequenceException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.batch().add( newClassPathResource( "init.drl", getClass()  ), DRL )
                        .add( newClassPathResource( "game.drl",  getClass()  ), DRL )
                        .add( newClassPathResource( "keys.drl",  getClass()  ), DRL )
                        .add( newClassPathResource( "move.drl",  getClass()  ), DRL )
                        .add( newClassPathResource( "collision.drl",  getClass()  ), DRL )
                        .add( newClassPathResource( "ui.drl", getClass() ), DRL ).build();   
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( config );        
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        

        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        //ksession.addEventListener( new DebugAgendaEventListener() );
        PongConfiguration pconf = new PongConfiguration();
        pconf.setExitOnClose(exitOnClose);
        ksession.setGlobal("pconf", pconf);
        
//        ksession.addEventListener( new DefaultAgendaEventListener() {
//            public void beforeActivationFired(BeforeActivationFiredEvent event)  {
//                System.out.println( "b: " + event.getActivation().getRule().getName() + " : " + event.getActivation().getFactHandles() );
//            }        
//            public void afterActivationFired(AfterActivationFiredEvent event)  {
//                System.out.println( "a: " + event.getActivation().getRule().getName() + " : " + event.getActivation().getFactHandles() );
//            }
////            public void activationCreated(ActivationCreatedEvent event)  {
////                System.out.println( "cr: " + event.getActivation().getRule().getName() + " : " + event.getActivation().getFactHandles() );
////            }
////            public void activationCancelled(ActivationCancelledEvent event)  {
////                System.out.println( "cl: " + event.getActivation().getRule().getName() + " : " + event.getActivation().getFactHandles() );
////            }                      
//            
//        });

        runKSession(ksession);
    }

    public void runKSession(final StatefulKnowledgeSession ksession) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                // run forever
                ksession.fireUntilHalt();
            }
        });
    }

}
