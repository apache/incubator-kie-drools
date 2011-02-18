/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.conway.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.drools.examples.conway.AbstractRunConway;
import org.drools.examples.conway.CellGrid;
import org.drools.examples.conway.CellGridImpl;
import org.drools.examples.conway.ConwayApplicationProperties;
import org.drools.examples.conway.patterns.ConwayPattern;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class ConwayGUI extends JPanel {

	private static final long serialVersionUID = 510l;

	private final JButton   nextGenerationButton;
    private final JButton   startStopButton;
    private final JButton   clearButton;
    private final JComboBox patternSelector = new JComboBox();
    private final Timer     timer;
    private final CellGrid  grid;

    public ConwayGUI(final int executionControl) {
        super( new BorderLayout() );
        final String nextGenerationLabel = ConwayApplicationProperties.getProperty( "next.generation.label" );
        this.nextGenerationButton = new JButton( nextGenerationLabel );
        final String startLabel = ConwayApplicationProperties.getProperty( "start.label" );
        this.startStopButton = new JButton( startLabel );
        final String clearLabel = ConwayApplicationProperties.getProperty( "clear.label" );
        this.clearButton = new JButton( clearLabel );
        
        //this.grid = new CellGridAgendaGroup( 30, 30 );
        this.grid = new CellGridImpl( 30, 30, executionControl );
        
        final CellGridCanvas canvas = new CellGridCanvas( grid );
        final JPanel panel = new JPanel( new BorderLayout() );
        panel.add( BorderLayout.CENTER,
                   canvas );
        final Border etchedBorder = BorderFactory.createEtchedBorder( EtchedBorder.LOWERED );
        final Border outerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                         5,
                                                                         5,
                                                                         5 );
        final Border innerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                         5,
                                                                         5,
                                                                         5 );
        final Border border = BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder( outerBlankBorder,
                                                                                                      etchedBorder ),
                                                                  innerBlankBorder );
        panel.setBorder( border );
        add( BorderLayout.CENTER,
             panel );
        add( BorderLayout.EAST,
             createControlPanel() );
        this.nextGenerationButton.addActionListener( new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Worker.post( new Job() {
                    public Object run() {
                        grid.nextGeneration();
                        return null;
                    }
                } );
                canvas.repaint();
            }
        } );
        this.clearButton.addActionListener( new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Worker.post( new Job() {
                    public Object run() {
                        grid.killAll();
                        return null;
                    }
                } );
                canvas.repaint();
            }
        } );

        final ActionListener timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Worker.post( new Job() {
                    public Object run() {
                        if ( !grid.nextGeneration() ) {
                            stopTimer();
                        }
                        return null;
                    }
                } );
                canvas.repaint();
            }
        };
        this.timer = new Timer( 500,
                                timerAction );
        this.startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                if ( ConwayGUI.this.timer.isRunning() ) {
                    stopTimer();
                } else {
                    startTimer();
                }
            }
        } );

        populatePatternSelector();

        this.patternSelector.addActionListener( new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final ConwayPattern pattern = (ConwayPattern) ConwayGUI.this.patternSelector.getSelectedItem();
                if ( pattern != null ) {
                    grid.setPattern( pattern );
                    canvas.repaint();
                }
            }
        } );

        this.patternSelector.setSelectedIndex( -1 );
    }

    public void dispose() {
        this.grid.dispose();
    }
    private void populatePatternSelector() {
        final String patternClassNames = ConwayApplicationProperties.getProperty( "conway.pattern.classnames" );
        final StringTokenizer tokenizer = new StringTokenizer( patternClassNames );

        String className = null;
        while ( tokenizer.hasMoreTokens() ) {
            className = tokenizer.nextToken().trim();
            try {
                final Class<?> clazz = Class.forName( className );
                if ( ConwayPattern.class.isAssignableFrom( clazz ) ) {
                    this.patternSelector.addItem( clazz.newInstance() );
                } else {
                    System.err.println( "Invalid pattern class name: " + className );
                }
            } catch ( final Exception e ) {
                System.err.println( "An error occurred populating patterns: " );
                e.printStackTrace();
            }
        }
    }

    private void startTimer() {
        final String stopLabel = ConwayApplicationProperties.getProperty( "stop.label" );
        this.startStopButton.setText( stopLabel );
        this.nextGenerationButton.setEnabled( false );
        this.clearButton.setEnabled( false );
        this.patternSelector.setEnabled( false );
        this.timer.start();
    }

    private void stopTimer() {
        this.timer.stop();
        final String startLabel = ConwayApplicationProperties.getProperty( "start.label" );
        this.startStopButton.setText( startLabel );
        this.nextGenerationButton.setEnabled( true );
        this.clearButton.setEnabled( true );
        this.patternSelector.setEnabled( true );
    }

    private JPanel createControlPanel() {
        final FormLayout layout = new FormLayout( "pref, 3dlu, pref, 3dlu:grow",
                                                  "pref, 15dlu, pref, 15dlu, pref, 3dlu:grow, pref" );
        final PanelBuilder builder = new PanelBuilder( layout );
        final CellConstraints cc = new CellConstraints();

        final String title = ConwayApplicationProperties.getProperty( "app.title" );
        builder.addLabel( title,
                          cc.xywh( 1,
                                   1,
                                   layout.getColumnCount(),
                                   1 ) );

        final String info = ConwayApplicationProperties.getProperty( "app.description" );
        builder.addLabel( info,
                          cc.xywh( 1,
                                   3,
                                   layout.getColumnCount(),
                                   1 ) );

        final String patternLabel = ConwayApplicationProperties.getProperty( "pattern.label" );
        builder.addLabel( patternLabel,
                          cc.xy( 1,
                                 5 ) );

        builder.add( this.patternSelector,
                     cc.xy( 3,
                            5 ) );
        final JPanel buttonPanel = ButtonBarFactory.buildLeftAlignedBar( this.nextGenerationButton,
                                                                         this.startStopButton,
                                                                         this.clearButton );
        builder.add( buttonPanel,
                     cc.xywh( 1,
                              7,
                              layout.getColumnCount(),
                              1 ) );

        final Border etchedBorder = BorderFactory.createEtchedBorder( EtchedBorder.LOWERED );
        final Border outerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                         5,
                                                                         5,
                                                                         5 );
        final Border innerBlankBorder = BorderFactory.createEmptyBorder( 5,
                                                                         5,
                                                                         5,
                                                                         5 );
        final Border border = BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder( outerBlankBorder,
                                                                                                      etchedBorder ),
                                                                  innerBlankBorder );
        builder.setBorder( border );
        return builder.getPanel();
    }

    public static void main(final String[] args) {
        final ConwayGUI gui = new ConwayGUI( AbstractRunConway.RULEFLOWGROUP );
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
