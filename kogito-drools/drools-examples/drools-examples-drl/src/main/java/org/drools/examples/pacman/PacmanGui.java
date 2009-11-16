package org.drools.examples.pacman;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.drools.runtime.ExitPoint;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class PacmanGui extends JFrame
    implements
    KeyListener,
    ActionListener {
    JTextArea           displayArea;
    static final String newline = System.getProperty( "line.separator" );
    WorkingMemoryEntryPoint keyListenerEntryPoint;

    public static void init(final StatefulKnowledgeSession ksession) {
        try {
            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch ( UnsupportedLookAndFeelException ex ) {
            ex.printStackTrace();
        } catch ( IllegalAccessException ex ) {
            ex.printStackTrace();
        } catch ( InstantiationException ex ) {
            ex.printStackTrace();
        } catch ( ClassNotFoundException ex ) {
            ex.printStackTrace();
        }
        UIManager.put( "swing.boldMetal",
                       Boolean.FALSE );

        //Schedule a job for event dispatch thread:
        //creating and showing this application's GUI.
        try {
            javax.swing.SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    createAndShowGUI(ksession);
                }
            } );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private static void createAndShowGUI(StatefulKnowledgeSession ksession) {
        //Create and set up the window.
        PacmanGui frame = new PacmanGui( "KeyEventDemo", ksession );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        //Set up the content pane.
        frame.addComponentsToPane();

        //Display the window.
        frame.pack();
        frame.setVisible( true );
    }
    
    public void appendText(final String string) {
        javax.swing.SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                displayArea.append( string );
                displayArea.setCaretPosition(displayArea.getDocument().getLength());
            }
        } );                
    }
        
    private void addComponentsToPane() {
        JButton button = new JButton( "Clear" );
        button.addActionListener( this );

        displayArea = new JTextArea();
        displayArea.setEditable( false );
        JScrollPane scrollPane = new JScrollPane( displayArea );
        scrollPane.setPreferredSize( new Dimension( 600,
                                                    600 ) );
        displayArea.addKeyListener( this );

        getContentPane().add( scrollPane,
                              BorderLayout.CENTER );
        getContentPane().add( button,
                              BorderLayout.PAGE_END );
    }

    public PacmanGui(String name, StatefulKnowledgeSession ksession) {
        super( name );        
        this.keyListenerEntryPoint = ksession.getWorkingMemoryEntryPoint( "KeyListener" );
        ksession.registerExitPoint( "ConsoleExitPoint", new ConsoleExitPoint( this ) );
    }
    
    public static class ConsoleExitPoint implements ExitPoint {        
        private PacmanGui gui;
        
        public ConsoleExitPoint(PacmanGui gui) {
            this.gui = gui;
        }

        public void insert(final Object arg) {
            gui.appendText( (String) arg );
        }
        
    }    

    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    public void keyPressed(KeyEvent e) {
        // do nothing
    }

    public void keyReleased(KeyEvent e) {
//        switch ( e.getKeyCode() ) {
//            case 38 : { // UP
//             break;   
//            }
//            case 40 : { // DOWN
//                break;
//            }
//            case 37 : { // LEFt
//                break;
//            }
//            case 39 : { // RIGHT
//                break;
//            }
//            default: {
//                
//            }
//        }
        //System.out.println( e );
        this.keyListenerEntryPoint.insert( e );
    }

    public void actionPerformed(ActionEvent e) {
        displayArea.setText( "" );
    }

}
