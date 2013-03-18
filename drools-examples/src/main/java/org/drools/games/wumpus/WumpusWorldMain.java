package org.drools.games.wumpus;

import org.drools.games.wumpus.view.GameView;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.runtime.Channel;
import org.kie.runtime.StatefulKnowledgeSession;

import static org.kie.io.ResourceFactory.newClassPathResource;
import static org.kie.io.ResourceType.DRL;

public class WumpusWorldMain {

    public static void main(String[] args) {
        new WumpusWorldMain().init(true);
    }

    public WumpusWorldMain() {
    }

    public void init(boolean exitOnClose) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.batch().add( newClassPathResource( "init.drl", getClass() ), DRL )
                        .add( newClassPathResource( "commands.drl", getClass() ), DRL )
                        .add( newClassPathResource( "shoot.drl", getClass() ), DRL)
                        .add( newClassPathResource( "ui.drl", GameView.class ), DRL )
                        .add( newClassPathResource( "paintCave.drl", GameView.class ), DRL )
                        .add( newClassPathResource( "paintSensor.drl", GameView.class ), DRL )
                        .add(  newClassPathResource( "score.drl", getClass() ), DRL )
                        .add( newClassPathResource( "sensorArray.drl", getClass() ), DRL ).build();   
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }
        KnowledgeBase serverKBase = KnowledgeBaseFactory.newKnowledgeBase( );
        serverKBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.batch().add( newClassPathResource( "client.drl", getClass() ), DRL ).build();     
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase clientKBase = KnowledgeBaseFactory.newKnowledgeBase();
        clientKBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );        

        final StatefulKnowledgeSession serverKsession = serverKBase.newStatefulKnowledgeSession();        
        final StatefulKnowledgeSession clientKsession = clientKBase.newStatefulKnowledgeSession();
        
        serverKsession.getChannels().put( "sensors", new Channel() {            
            public void send(Object object) {
                clientKsession.insert( object );
                clientKsession.fireAllRules();
            }
        } );
        
        clientKsession.getChannels().put( "commands", new Channel() {            
            public void send(Object object) {
                serverKsession.insert( object ); 
                serverKsession.fireAllRules();
            }
        } );

        WumpusWorldConfiguration wumpusWorldConfiguration = new WumpusWorldConfiguration();
        wumpusWorldConfiguration.setExitOnClose(exitOnClose);
        serverKsession.setGlobal("wumpusWorldConfiguration", wumpusWorldConfiguration);
        serverKsession.setGlobal("randomInteger",new java.util.Random() );
        serverKsession.fireAllRules();        
        clientKsession.fireAllRules();
        

    }
    

}
