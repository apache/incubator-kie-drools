package org.drools.games.wumpus.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.drools.games.wumpus.Cell;
import org.drools.games.wumpus.ClimbCommand;
import org.drools.games.wumpus.Gold;
import org.drools.games.wumpus.GrabCommand;
import org.drools.games.wumpus.Hero;
import org.drools.games.wumpus.Move;
import org.drools.games.wumpus.MoveCommand;
import org.drools.games.wumpus.Pit;
import org.drools.games.wumpus.Reset;
import org.drools.games.wumpus.Score;
import org.drools.games.wumpus.ShootCommand;
import org.drools.games.wumpus.Wumpus;
import org.drools.games.wumpus.WumpusWorldConfiguration;
import org.drools.runtime.Channel;
import org.drools.runtime.rule.FactHandle;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GameUI {

    private final WumpusWorldConfiguration wumpusWorldConfiguration;

    private JFrame      frame;

    private GameView    gameView;

    private CavePanel   cavePanel;

    private SensorPanel sensorPanel;

    public GameUI(WumpusWorldConfiguration wumpusWorldConfiguration) {
        this.wumpusWorldConfiguration = wumpusWorldConfiguration;
    }

    /**
     * @wbp.parser.entryPoint
     */
    public GameUI(WumpusWorldConfiguration wumpusWorldConfiguration, GameView gameView) {
        this(wumpusWorldConfiguration);
        this.gameView = gameView;
        if (this.gameView == null ) {
                this.gameView = new GameView();
                this.gameView.init( 50, 50, 3, 20, 5, 5 );
        }
        initialize();
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
        if ( frame == null ) {
            initialize();
        }
    }
    
    public JFrame getParentJFrame() {
        return frame;
    }

    public GameView getGameView() {
        return gameView;
    }

    public CavePanel getCavePanel() {
        return cavePanel;
    }

    public SensorPanel getSensorPanel() {
        return sensorPanel;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame( "Wumpus World" );
        frame.getContentPane().setBackground( Color.WHITE );
        frame.setDefaultCloseOperation(wumpusWorldConfiguration.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

        frame.setSize( 926, 603 );
        frame.getContentPane().setLayout( new MigLayout("", "[540px:n][grow,fill]", "[30px,top][300px,top][100px,top][grow]") );
        
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
        frame.getContentPane().add( actionPanel, "cell 0 1,grow" );
        actionPanel.setLayout( new MigLayout("", "[200px,left][320px:n]", "[grow]") );

        JPanel controls = new JPanel();
        controls.setBackground( Color.WHITE );
        controls.setLayout( new MigLayout( "", "[grow,fill]", "[::100px,top][200px,top]" ) );
        controls.add( drawActionPanel(), "cell 0 0,alignx left,aligny top" );

        controls.add( drawMovePanel(), "cell 0 1,alignx left,growy" );

        actionPanel.add( controls, "cell 0 0,grow" );

        cavePanel = drawCave();
        actionPanel.add( cavePanel, "cell 1 0,grow" );

        sensorPanel = drawSensorPanel();
        
        frame.getContentPane().add( sensorPanel, "cell 0 2,grow" );
        
        JPanel blank = new JPanel();
        blank.setBackground(Color.WHITE);
        frame.getContentPane().add(blank, "cell 0 3,grow");

        frame.setLocationRelativeTo(null); // Center in screen
        frame.setVisible( true );
        
        updateCave();
        updateSensors();
    }

    public synchronized void updateCave() {
        cavePanel.repaint();
        cavePanel.revalidate();
    }

    public synchronized void updateSensors() {
        sensorPanel.repaint();
        sensorPanel.revalidate();
    }

    public SensorPanel drawSensorPanel() {
        SensorPanel sensorPanel = new SensorPanel( this );
        FlowLayout flowLayout = (FlowLayout) sensorPanel.getLayout();
        flowLayout.setVgap( 10 );
        sensorPanel.setBackground( Color.WHITE );
        return sensorPanel;
    }

    public CavePanel drawCave() {
        CavePanel cavelPanel = new CavePanel( this );
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

    public static class SensorPanel extends JPanel {
        private GameUI        gameUI;

        private BufferedImage bi;
        private Graphics      sensorG;

        public SensorPanel(GameUI gameUI) {
            setOpaque( true );
            this.gameUI = gameUI;
        }
        
        public Graphics getSensorG() {
            return sensorG;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if ( bi == null ) { // prepare BufferdImage
                bi = new BufferedImage( getWidth(), getHeight(),
                                        BufferedImage.TYPE_INT_RGB );
                sensorG = bi.createGraphics();
                sensorG.setColor( Color.WHITE ); // background
                sensorG.fillRect( 0, 0, getWidth(), getHeight() );
                if ( gameUI != null ) {
                    // we need this to trigger the ksession drawing, otherwise the engine doesn't know it's ready                    
                    gameUI.getGameView().getKsession().update( gameUI.getGameView().getKsession().getFactHandle( gameUI ), gameUI );
                    gameUI.getGameView().getKsession().fireAllRules();
                }
            }

            g.drawImage( bi, 0, 0, null );
        }
    }

    public static class CavePanel extends JPanel {
        private GameUI        gameUI;

        private BufferedImage bi;
        private Graphics      caveG;

        public CavePanel(GameUI gameUI) {
            setOpaque( true );
            this.gameUI = gameUI;
        }
        
        public Graphics getCaveG() {
            return caveG;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if ( bi == null ) { // prepare BufferdImage
                bi = new BufferedImage( getWidth(), getHeight(),
                                        BufferedImage.TYPE_INT_RGB );
                caveG = bi.createGraphics();
                caveG.setColor( Color.WHITE ); // background
                caveG.fillRect( 0, 0, getWidth(), getHeight() );
                if ( gameUI != null ) {                
                    // we need this to trigger the ksession drawing, otherwise the engine doesn't know it's ready                    
                    gameUI.getGameView().getKsession().update( gameUI.getGameView().getKsession().getFactHandle( gameUI ), gameUI );
                    gameUI.getGameView().getKsession().fireAllRules();
                }
            }
            
            g.drawImage( bi, 0, 0, null );
        }
    }

}
