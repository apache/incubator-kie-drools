package org.drools.examples.wumpus;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.event.rule.DefaultWorkingMemoryEventListener;
import org.drools.examples.wumpus.view.GameView;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class WumpusWorldServer {

    public static void main(String[] args) throws InterruptedException  {
        WumpusWorldServer ww = new WumpusWorldServer();
        ww.init();
    }

    public void init() throws InterruptedException {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add( ResourceFactory.newClassPathResource( "init.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }        
        
        kbuilder.add( ResourceFactory.newClassPathResource( "commands.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }

        kbuilder.add( ResourceFactory.newClassPathResource( "collision.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }
        
        kbuilder.add( ResourceFactory.newClassPathResource( "ui.drl", GameView.class ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }    
        
        kbuilder.add( ResourceFactory.newClassPathResource( "paintCave.drl", GameView.class ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }   
        
        kbuilder.add( ResourceFactory.newClassPathResource( "paintSensor.drl", GameView.class ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }          
        
        kbuilder.add( ResourceFactory.newClassPathResource( "score.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }          

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
//        ksession.addEventListener( new DebugAgendaEventListener() );
//        ksession.addEventListener( new DebugWorkingMemoryEventListener() );
        ksession.setGlobal("randomInteger",new java.util.Random() );
        ksession.fireAllRules();        
    }
    

}
