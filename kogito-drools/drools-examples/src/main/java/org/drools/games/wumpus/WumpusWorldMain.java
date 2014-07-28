package org.drools.games.wumpus;

import org.drools.games.wumpus.view.GameUI;
import org.drools.games.wumpus.view.GameView;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.runtime.Channel;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.kie.internal.io.ResourceFactory.newClassPathResource;
import static org.kie.api.io.ResourceType.DRL;

public class WumpusWorldMain {

    public static void main(String[] args) {
        new WumpusWorldMain().init(true);
    }

    public WumpusWorldMain() {
    }

    public void init(boolean exitOnClose) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession serverKsession = kc.newKieSession( "WumpusMainKS");
        final KieSession clientKsession = kc.newKieSession("WumpusClientKS");

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

        GameUI gameUI = new GameUI(serverKsession, wumpusWorldConfiguration);
        serverKsession.insert(gameUI  );
        serverKsession.insert(gameUI.getGameView()  );


        new Thread(new Runnable() {
            public void run() {
                serverKsession.fireUntilHalt();
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                clientKsession.fireUntilHalt();
            }
        }).start();
    }
    

}
