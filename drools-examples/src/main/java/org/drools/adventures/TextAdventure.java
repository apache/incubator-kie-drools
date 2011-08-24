package org.drools.adventures;

import java.awt.EventQueue;

import org.drools.adventures.AdventureFrame.JTextAreaChannel;
import org.drools.adventures.AdventureFrame.JTableChannel;
import org.drools.adventures.AdventureFrame.JComboBoxChannel;

public class TextAdventure {
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    UserSession session = new UserSession();
                    
                    AdventureFrame frame = new AdventureFrame(session);
                    frame.setVisible( true );
                                                            
                    session.getChannels().put( "output", new JTextAreaChannel( frame.getOutputTextArea() ) );
                    session.getChannels().put( "events",  new JTextAreaChannel( frame.getLocalEventsTextArea() ) );
                    session.getChannels().put( "exits", new JTableChannel( frame.getExitsTable() ) );
                    session.getChannels().put( "things", new JTableChannel( frame.getThingsTable()) );
                    session.getChannels().put( "inventory", new JTableChannel( frame.getInventoryTable()) );
                    session.getChannels().put( "characters", new JComboBoxChannel( frame.getCharacterSelectCombo() ) );
                    
                    GameEngine engine = new GameEngine();
                    engine.createGame();
                    frame.setGameEngine( engine );
                    
                    engine.ksession.insert( session );
                    engine.ksession.fireAllRules();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
        
    }
}
