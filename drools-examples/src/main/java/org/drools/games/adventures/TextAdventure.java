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

package org.drools.games.adventures;

import java.awt.EventQueue;
import java.util.Map;

import javax.swing.JFrame;

import org.drools.games.adventures.AdventureFrame.JTableChannel;
import org.drools.games.adventures.AdventureFrame.JTextAreaChannel;
import org.drools.games.adventures.model.*;
import org.drools.games.adventures.model.Character;

public class TextAdventure {

    public static void main(String[] args) {
        new TextAdventure().init(true);
    }

    public TextAdventure() {
    }

    public void init(final boolean exitOnClose) {
        
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                GameEngine engine = new GameEngine();
                engine.createGame();

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
