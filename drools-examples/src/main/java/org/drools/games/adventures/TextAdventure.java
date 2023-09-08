/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.games.adventures;

import org.drools.games.adventures.AdventureFrame.JTableChannel;
import org.drools.games.adventures.AdventureFrame.JTextAreaChannel;
import org.drools.games.adventures.model.Character;
import org.drools.games.adventures.model.LookCommand;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TextAdventure {

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        new TextAdventure().init(kc, true);
    }

    public TextAdventure() {
    }

    public void init(final KieContainer kc, final boolean exitOnClose) {
        
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                GameEngine engine = new GameEngine();
                engine.createGame(kc);

                createFrame(engine, exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
            }
        } );
        
    }
    
    public static void createFrame(GameEngine engine, int onClose) {
        UserSession session = new UserSession();

        Map<String, Character> characterMap = ( Map<String, Character> ) engine.getData().get("characters");
        session.setCharacter(characterMap.get("hero"));

        AdventureFrame frame = new AdventureFrame(session, engine, onClose);
        frame.setVisible( true );

        session.getChannels().put( "output", new JTextAreaChannel( frame.getOutputTextArea() ) );
        session.getChannels().put( "events",  new JTextAreaChannel( frame.getLocalEventsTextArea() ) );
        session.getChannels().put( "exits", new JTableChannel( frame.getExitsTable() ) );
        session.getChannels().put( "things", new JTableChannel( frame.getThingsTable()) );
        session.getChannels().put( "inventory", new JTableChannel( frame.getInventoryTable()) );


        engine.ksession.insert( session );
        LookCommand lc = new LookCommand(characterMap.get("hero"));
        lc.setSession(session);
        engine.getKieSession().insert(lc);
        engine.ksession.fireAllRules();
    }
}
