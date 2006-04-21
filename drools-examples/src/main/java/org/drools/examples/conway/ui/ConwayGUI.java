package org.drools.examples.conway.ui;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import foxtrot.Job;
import foxtrot.Worker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.drools.examples.conway.CellGrid;
import org.drools.examples.conway.ConwayApplicationProperties;
import org.drools.examples.conway.patterns.ConwayPattern;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class ConwayGUI extends JPanel
{
    private final JButton   nextGenerationButton;
    private final JButton   startStopButton;
    private final JButton   clearButton;
    private final JComboBox patternSelector = new JComboBox( );
    private final Timer     timer;

    public ConwayGUI()
    {
        super( new BorderLayout( ) );
        final String nextGenerationLabel = ConwayApplicationProperties.getProperty( "next.generation.label" );
        nextGenerationButton = new JButton( nextGenerationLabel );
        final String startLabel = ConwayApplicationProperties.getProperty( "start.label" );
        startStopButton = new JButton( startLabel );
        final String clearLabel = ConwayApplicationProperties.getProperty( "clear.label" );
        clearButton = new JButton( clearLabel );
        final CellGrid grid = new CellGrid( 30,
                                            30 );
        final CellGridCanvas canvas = new CellGridCanvas( grid );
        JPanel panel = new JPanel( new BorderLayout( ) );
        panel.add( BorderLayout.CENTER,
                   canvas );
        Border etchedBorder = BorderFactory.createEtchedBorder( EtchedBorder.LOWERED );
        Border outerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                   5,
                                                                   5,
                                                                   5 );
        Border innerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                   5,
                                                                   5,
                                                                   5 );
        Border border = BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder( outerBlankBorder,
                                                                                                etchedBorder ),
                                                            innerBlankBorder );
        panel.setBorder( border );
        add( BorderLayout.CENTER,
             panel );
        add( BorderLayout.EAST,
             createControlPanel( ) );
        nextGenerationButton.addActionListener( new ActionListener( ) {
            public void actionPerformed(ActionEvent e)
            {
                Worker.post( new Job( ) {
                    public Object run()
                    {
                        grid.nextGeneration( );
                        return null;
                    }
                } );
                canvas.repaint( );
            }
        } );
        clearButton.addActionListener( new ActionListener( ) {
            public void actionPerformed(ActionEvent e)
            {
                Worker.post( new Job( ) {
                    public Object run()
                    {
                        grid.killAll( );
                        return null;
                    }
                } );
                canvas.repaint( );
            }
        } );

        ActionListener timerAction = new ActionListener( ) {
            public void actionPerformed(ActionEvent ae)
            {
                Worker.post( new Job( ) {
                    public Object run()
                    {
                        if ( !grid.nextGeneration( ) )
                        {
                            stopTimer( );
                        }
                        return null;
                    }
                } );
                canvas.repaint( );
            }
        };
        timer = new Timer( 500,
                           timerAction );
        startStopButton.addActionListener( new ActionListener( ) {
            public void actionPerformed(ActionEvent e)
            {
                if ( timer.isRunning( ) )
                {
                    stopTimer( );
                }
                else
                {
                    startTimer( );
                }
            }
        } );

        populatePatternSelector( );

        patternSelector.addActionListener( new ActionListener( ) {
            public void actionPerformed(ActionEvent e)
            {
                ConwayPattern pattern = (ConwayPattern) patternSelector.getSelectedItem( );
                if ( pattern != null )
                {
                    grid.setPattern( pattern );
                    canvas.repaint( );
                }
            }
        } );

        patternSelector.setSelectedIndex( -1 );
    }

    private void populatePatternSelector()
    {
        String patternClassNames = ConwayApplicationProperties.getProperty( "conway.pattern.classnames" );
        StringTokenizer tokenizer = new StringTokenizer( patternClassNames );

        String className = null;
        while ( tokenizer.hasMoreTokens( ) )
        {
            className = tokenizer.nextToken( ).trim( );
            try
            {
                Class clazz = Class.forName( className );
                if ( ConwayPattern.class.isAssignableFrom( clazz ) )
                {
                    patternSelector.addItem( clazz.newInstance( ) );
                }
                else
                {
                    System.err.println( "Invalid pattern class name: " + className );
                }
            }
            catch ( Exception e )
            {
                System.err.println( "An error occurred populating patterns: " );
                e.printStackTrace( );
            }
        }
    }

    private void startTimer()
    {
        final String stopLabel = ConwayApplicationProperties.getProperty( "stop.label" );
        startStopButton.setText( stopLabel );
        nextGenerationButton.setEnabled( false );
        clearButton.setEnabled( false );
        patternSelector.setEnabled( false );
        timer.start( );
    }

    private void stopTimer()
    {
        timer.stop( );
        final String startLabel = ConwayApplicationProperties.getProperty( "start.label" );
        startStopButton.setText( startLabel );
        nextGenerationButton.setEnabled( true );
        clearButton.setEnabled( true );
        patternSelector.setEnabled( true );
    }

    private JPanel createControlPanel()
    {
        FormLayout layout = new FormLayout( "pref, 3dlu, pref, 3dlu:grow",
                                            "pref, 15dlu, pref, 15dlu, pref, 3dlu:grow, pref" );
        PanelBuilder builder = new PanelBuilder( layout );
        CellConstraints cc = new CellConstraints( );

        String title = ConwayApplicationProperties.getProperty( "app.title" );
        builder.addLabel( title,
                          cc.xywh( 1,
                                   1,
                                   layout.getColumnCount( ),
                                   1 ) );

        String info = ConwayApplicationProperties.getProperty( "app.description" );
        builder.addLabel( info,
                          cc.xywh( 1,
                                   3,
                                   layout.getColumnCount( ),
                                   1 ) );

        final String patternLabel = ConwayApplicationProperties.getProperty( "pattern.label" );
        builder.addLabel( patternLabel,
                          cc.xy( 1,
                                 5 ) );

        builder.add( patternSelector,
                     cc.xy( 3,
                            5 ) );
        JPanel buttonPanel = ButtonBarFactory.buildLeftAlignedBar( nextGenerationButton,
                                                                   startStopButton,
                                                                   clearButton );
        builder.add( buttonPanel,
                     cc.xywh( 1,
                              7,
                              layout.getColumnCount( ),
                              1 ) );

        Border etchedBorder = BorderFactory.createEtchedBorder( EtchedBorder.LOWERED );
        Border outerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                   5,
                                                                   5,
                                                                   5 );
        Border innerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                   5,
                                                                   5,
                                                                   5 );
        Border border = BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder( outerBlankBorder,
                                                                                                etchedBorder ),
                                                            innerBlankBorder );
        builder.setBorder( border );
        return builder.getPanel( );
    }

    public static void main(String[] args)
    {
//        if ( args.length != 1 )
//        {
//            System.out.println( "Usage: " + ConwayGUI.class.getName( ) + " [drl file]" );
//            return;
//        }
//        System.out.println( "Using drl: " + args[0] );
//
//        System.setProperty( "conway.drl.file",
//                            args[0] );

        final String appTitle = ConwayApplicationProperties.getProperty( "app.title" );
        JFrame f = new JFrame( appTitle );
        f.setResizable( false );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.getContentPane( ).add( BorderLayout.CENTER,
                                 new ConwayGUI( ) );
        f.pack( );
        f.setVisible( true );
    }
}
