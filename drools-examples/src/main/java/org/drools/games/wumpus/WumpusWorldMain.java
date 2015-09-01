/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.wumpus;

import org.drools.games.wumpus.view.GameUI;
import org.kie.api.KieServices;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class WumpusWorldMain {

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        new WumpusWorldMain().init(kc, true);
    }

    public WumpusWorldMain() {
    }

    public void init(final KieContainer kc, boolean exitOnClose) {
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
