package org.drools.examples.conway;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.drools.examples.conway.ui.ConwayGUI;

public class AbstractRunConway {
    public static final int AGENDAGROUP = 0;
    public static final int RULEFLOWGROUP = 1;
    
    public static void start(final int executionControl) {
        final ConwayGUI gui = new ConwayGUI(executionControl);
        final String appTitle = ConwayApplicationProperties.getProperty( "app.title" );
        final JFrame f = new JFrame( appTitle );
        f.setResizable( false );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.getContentPane().add( BorderLayout.CENTER,
                                gui );

        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                gui.dispose();
            }
        } );
        f.pack();
        f.setVisible( true );
    }
}
