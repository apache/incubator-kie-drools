package org.drools.adventures;

import java.awt.EventQueue;

import org.drools.adventures.AdventureFrame.TextFieldChannel;

public class TextAdventure {
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    AdventureFrame frame = new AdventureFrame();
                    frame.setVisible( true );
                    
                    GameEngine engine = new GameEngine();
                    engine.createGame( new TextFieldChannel(
                                       frame.getOutputTextArea() ), new TextFieldChannel( frame.getLocalEventsTextArea() ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
        
    }
}
