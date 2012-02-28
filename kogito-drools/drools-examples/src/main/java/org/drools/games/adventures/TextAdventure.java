package org.drools.games.adventures;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.drools.games.adventures.AdventureFrame.JComboBoxChannel;
import org.drools.games.adventures.AdventureFrame.JTableChannel;
import org.drools.games.adventures.AdventureFrame.JTextAreaChannel;

public class TextAdventure {

    public static void main(String[] args) {              
        
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    GameEngine engine = new GameEngine();
                    engine.createGame();
                    
                    
                    TextAdventure.createFrame(engine, JFrame.EXIT_ON_CLOSE);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
        
    }
    
    public static void createFrame(GameEngine engine, int onClose) {
        UserSession session = new UserSession();
        
        AdventureFrame frame = new AdventureFrame(session, onClose);
        frame.setVisible( true );
                                                
        session.getChannels().put( "output", new JTextAreaChannel( frame.getOutputTextArea() ) );
        session.getChannels().put( "events",  new JTextAreaChannel( frame.getLocalEventsTextArea() ) );
        session.getChannels().put( "exits", new JTableChannel( frame.getExitsTable() ) );
        session.getChannels().put( "things", new JTableChannel( frame.getThingsTable()) );
        session.getChannels().put( "inventory", new JTableChannel( frame.getInventoryTable()) );
        session.getChannels().put( "characters", new JComboBoxChannel( frame.getCharacterSelectCombo() ) );
        
        frame.setGameEngine( engine );
        
        engine.ksession.insert( session );
        engine.ksession.fireAllRules();
    }
}
