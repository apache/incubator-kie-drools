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

package org.drools.games.wumpus.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.drools.games.GameFrame;
import org.drools.games.GamePanel;
import org.drools.games.wumpus.ClimbCommand;
import org.drools.games.wumpus.GrabCommand;
import org.drools.games.wumpus.Move;
import org.drools.games.wumpus.MoveCommand;
import org.drools.games.wumpus.Reset;
import org.drools.games.wumpus.Score;
import org.drools.games.wumpus.ShootCommand;
import org.drools.games.wumpus.WumpusWorldConfiguration;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class GameUI {

    private final WumpusWorldConfiguration wumpusWorldConfiguration;

    private GameFrame frame;

    private GameView gameView;

    private GamePanel cavePanel;

    private GamePanel sensorPanel;

    /**
     * @wbp.parser.entryPoint
     */
    public GameUI(KieSession ksession, WumpusWorldConfiguration wumpusWorldConfiguration) {
        this.wumpusWorldConfiguration = wumpusWorldConfiguration;
        this.gameView = new GameView();
        this.gameView.setKsession((StatefulKnowledgeSession) ksession);
        this.gameView.init(50, 50, 3, 20, 5, 5);
        initialize();
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public JFrame getParentJFrame() {
        return frame;
    }

    public GameView getGameView() {
        return gameView;
    }

    public GamePanel getCavePanel() {
        return cavePanel;
    }

    public GamePanel getSensorPanel() {
        return sensorPanel;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new GameFrame( "Wumpus World" );
        frame.getContentPane().setBackground( Color.WHITE );
        frame.setDefaultCloseOperation(wumpusWorldConfiguration.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new MigLayout("", "[540px:n][grow,fill]", "[30px,top][300px,top][100px,top][grow]"));
        frame.setSize( 926, 603 );
        frame.setLocationRelativeTo(null); // Center in screen
        
        JPanel scorePanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) scorePanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        scorePanel.setBackground(Color.WHITE);
        frame.getContentPane().add(scorePanel, "cell 0 0,grow");
        
        JLabel lblScore = new JLabel("Score");
        scorePanel.add(lblScore);
        
        final JTextField txtScore = new JTextField();
        gameView.getKsession().getChannels().put( "score", new Channel() {            
            public void send(Object object) {
                txtScore.setText( "" + ((Score ) object).getValue() );
            }
        } );
        
        txtScore.setEditable(false);
        scorePanel.add(txtScore);
        txtScore.setColumns(10);
        
        JScrollPane scrollPane = new JScrollPane();
        frame.getContentPane().add(scrollPane, "cell 1 0 1 4,grow");

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground( Color.WHITE );
        frame.getContentPane().add(actionPanel, "cell 0 1,grow");
        actionPanel.setLayout( new MigLayout("", "[200px,left][320px:n]", "[grow]") );

        JPanel controls = new JPanel();
        controls.setBackground( Color.WHITE );
        controls.setLayout( new MigLayout( "", "[grow,fill]", "[::100px,top][200px,top]" ) );
        controls.add( drawActionPanel(), "cell 0 0,alignx left,aligny top" );

        controls.add( drawMovePanel(), "cell 0 1,alignx left,growy" );

        actionPanel.add( controls, "cell 0 0,grow" );

        cavePanel = drawCave();
        actionPanel.add(cavePanel, "cell 1 0,grow" );

        sensorPanel = drawSensorPanel();

        frame.getContentPane().add(sensorPanel, "cell 0 2,grow");
        
        JPanel blank = new JPanel();
        blank.setBackground(Color.WHITE);
        frame.add(blank, "cell 0 3,grow");

        frame.setVisible( true );

        cavePanel.getBufferedImage();
        sensorPanel.getBufferedImage();

        repaint();

    }

    public void repaint() {
        cavePanel.disposeGraphics2D();
        sensorPanel.disposeGraphics2D();
        //frame.repaint();
        frame.waitForPaint();
    }


    public GamePanel drawSensorPanel() {
        GamePanel sensorPanel = new GamePanel("sensor", Color.WHITE ); //new SensorPanel( this );
        FlowLayout flowLayout = (FlowLayout) sensorPanel.getLayout();
        flowLayout.setVgap( 10 );
        sensorPanel.setBackground( Color.WHITE );
        return sensorPanel;
    }

    public GamePanel drawCave() {
        GamePanel cavelPanel = new GamePanel("cave", Color.WHITE );
        FlowLayout flowLayout = (FlowLayout) cavelPanel.getLayout();
        flowLayout.setVgap( 10 );
        cavelPanel.setBackground( Color.WHITE );
        return cavelPanel;
    }

    public JPanel drawActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground( Color.WHITE );
        actionPanel.setLayout( new GridLayout( 0, 2, 0, 0 ) );

        JButton restartButton = new JButton( "RESTART" );
        restartButton.setToolTipText( "Restart game" );
        restartButton.setBackground( Color.LIGHT_GRAY );
        restartButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Reset reset = new Reset();
                reset.setResetScore( true );
                gameView.getKsession().insert( reset );
                gameView.getKsession().getAgenda().getAgendaGroup("Reset").setFocus();
                gameView.getKsession().fireAllRules();
            }
        } );
        actionPanel.add( restartButton );

        JButton shootButton = new JButton( "SHOOT" );
        shootButton.setToolTipText( "Shoot Arrow" );
        shootButton.setBackground( Color.LIGHT_GRAY );
        shootButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameView.getKsession().insert( new ShootCommand() );
                gameView.getKsession().fireAllRules();
            }
        } );
        actionPanel.add( shootButton );        
        
        JButton grabButton = new JButton( "GRAB" );
        grabButton.setToolTipText( "Grab gold" );
        grabButton.setBackground( Color.LIGHT_GRAY );
        grabButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameView.getKsession().insert( new GrabCommand() );
                gameView.getKsession().fireAllRules();
            }
        } );        
        actionPanel.add( grabButton );
        
        JButton climbButton = new JButton( "CLIMB" );
        climbButton.setToolTipText( "Climb out of the cave" );
        climbButton.setBackground( Color.LIGHT_GRAY );
        climbButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameView.getKsession().insert( new ClimbCommand() );
                gameView.getKsession().fireAllRules();
            }
        } );        
        actionPanel.add( climbButton );        

        final JButton showCaveButton = new JButton( "HIDE" );
        showCaveButton.setToolTipText( "Hide/Show the cave" );
        showCaveButton.setBackground( Color.LIGHT_GRAY );
        showCaveButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if( cavePanel.isVisible() ) {
                    cavePanel.setVisible( false );
                    showCaveButton.setText( "SHOW" );
                } else {
                    cavePanel.setVisible( true );
                    showCaveButton.setText( "HIDE" );
                }                
            }
        } );
        actionPanel.add( showCaveButton );
        
        JButton cheatButton = new JButton( "CHEAT" );
        cheatButton.setToolTipText( "Reveal all squares" );
        cheatButton.setBackground( Color.LIGHT_GRAY );
        cheatButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                FactHandle fh = gameView.getKsession().getFactHandle( gameView );

                if ( gameView.isShowAllCells() ) {
                    gameView.setShowAllCells( false );
                } else {
                    gameView.setShowAllCells( true );
                }
                gameView.getKsession().update( fh, gameView );
                gameView.getKsession().fireAllRules(); 
            }
        } );
        actionPanel.add( cheatButton );

        return actionPanel;
    }

    public JPanel drawMovePanel() {
        JPanel movePanel = new JPanel();
        movePanel.setBackground( Color.WHITE );
        movePanel.setLayout( new GridLayout( 0, 3, 0, 0 ) );

        JPanel panel_2 = new JPanel();
        panel_2.setBackground( Color.WHITE );
        movePanel.add( panel_2 );

        JButton btnNewButton = new JButton( "" );
        btnNewButton.setForeground( Color.WHITE );
        btnNewButton.setBackground( Color.WHITE );
        btnNewButton.setIcon( new ImageIcon( getClass().getResource( "up.png" ) ) );
        btnNewButton.setToolTipText( "move forward" );
        btnNewButton.addMouseListener( new MoveButtonPressed( Move.MOVE_FORWARD ) );

        movePanel.add( btnNewButton );

        JPanel panel_7 = new JPanel();
        panel_7.setBackground( Color.WHITE );
        movePanel.add( panel_7 );

        JButton btnNewButton_3 = new JButton( "" );
        btnNewButton_3.setForeground( Color.WHITE );
        btnNewButton_3.setBackground( Color.WHITE );
        btnNewButton_3.setIcon( new ImageIcon( getClass().getResource( "rotate_left.png" ) ) );
        btnNewButton_3.addMouseListener( new MoveButtonPressed( Move.TURN_LEFT ) );
        btnNewButton_3.setToolTipText( "rotate left" );
        movePanel.add( btnNewButton_3 );

        JPanel panel_8 = new JPanel();
        panel_8.setBackground( Color.WHITE );
        movePanel.add( panel_8 );

        JButton btnNewButton_2 = new JButton( "" );
        btnNewButton_2.setForeground( Color.WHITE );
        btnNewButton_2.setBackground( Color.WHITE );
        btnNewButton_2.setIcon( new ImageIcon( getClass().getResource( "rotate_right.png" ) ) );
        btnNewButton_2.addMouseListener( new MoveButtonPressed( Move.TURN_RIGHT ) );
        btnNewButton_2.setToolTipText( "rotate right" );
        movePanel.add( btnNewButton_2 );

        JPanel panel_9 = new JPanel();
        panel_9.setBackground( Color.WHITE );
        movePanel.add( panel_9 );

        JButton btnNewButton_1 = new JButton( "" );
        btnNewButton_1.setForeground( Color.WHITE );
        btnNewButton_1.setBackground( Color.WHITE );
        btnNewButton_1.setIcon( new ImageIcon( getClass().getResource( "down.png" ) ) );
        btnNewButton_1.addMouseListener( new MoveButtonPressed( Move.MOVE_BACKWARD ) );
        btnNewButton_1.setToolTipText( "move backward" );
        movePanel.add( btnNewButton_1 );

        JPanel panel_10 = new JPanel();
        panel_10.setBackground( Color.WHITE );
        movePanel.add( panel_10 );

        return movePanel;
    }

    private final class MoveButtonPressed extends MouseAdapter {
        private Move move;

        public MoveButtonPressed(Move move) {
            this.move = move;
        }

        public void mousePressed(MouseEvent e) {
            gameView.getKsession().insert( new MoveCommand( move ) );

            gameView.getKsession().fireAllRules();
        }
    }

}
