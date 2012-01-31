package org.drools.examples.wumpus.view;

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

import org.drools.examples.wumpus.Cell;
import org.drools.examples.wumpus.Gold;
import org.drools.examples.wumpus.Hero;
import org.drools.examples.wumpus.Move;
import org.drools.examples.wumpus.MoveCommand;
import org.drools.examples.wumpus.Pit;
import org.drools.examples.wumpus.Reset;
import org.drools.examples.wumpus.ShootCommand;
import org.drools.examples.wumpus.Wumpus;

public class GameUI {

    private JFrame      frame;

    private GameView    gameView;

    private CavePanel   cavePanel;

    private SensorPanel sensorPanel;

//    public static final void main(String[] args) {
//        EventQueue.invokeLater( new Runnable() {
//            public void run() {
//                try {
//                    GameUI window = new GameUI();
//                    window.initialize();
//                } catch ( Exception e ) {
//                    e.printStackTrace();
//                }
//            }
//        } );
//    }
//
//    public GameUI() {
//        this.gameView = new GameView();
//        this.gameView.init( new Cell[5][5], new SensorsView(), new ArrayList<Pit>(), new Wumpus( 2, 1 ), new Gold( 3, 1 ), new Hero( 0, 0 ), 50, 50, 3, 20, 5, 5 );
//    }
//
//    public static final void run(final GameView gameView) {
//        GameUI window = new GameUI( gameView );
//        window.initialize();
//    }

    public GameUI() {
    }
    
    
    public GameUI(GameView gameView) {
        this.gameView = gameView;
        initialize();
    }
    
    public void setGameView(GameView gameView) {
        this.gameView = gameView;
        if ( frame == null ) {
            initialize();
        }
    }

    public GameView getGameView() {
        return gameView;
    } 
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame( "Display image" );
        frame.getContentPane().setBackground( Color.WHITE );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        //drawCave();
        //drawActionPanel();

        frame.setSize( 840, 602 );
        frame.getContentPane().setLayout( new MigLayout( "", "[grow]", "[300px:n,grow][100px:n,grow]" ) );

        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        frame.getContentPane().add( panel, "cell 0 0,grow" );
        panel.setLayout( new MigLayout( "", "[200px:200px:200px,left][grow]", "[grow]" ) );

        JPanel controls = new JPanel();
        controls.setBackground( Color.WHITE );
        controls.setLayout( new MigLayout( "", "[grow,fill]", "[::100px,top][200px,top]" ) );
        controls.add( drawActionPanel(), "cell 0 0,alignx left,aligny top" );

        controls.add( drawMovePanel(), "cell 0 1,alignx left,growy" );

        //        sensorPanel = drawSensorPanel();
        //        panel_2.add( sensorPanel, "cell 0 1,grow" );

        panel.add( controls, "cell 0 0,grow" );

        cavePanel = drawCave();
        panel.add( cavePanel, "cell 1 0,grow" );

        JPanel panel_1 = new JPanel();
        panel_1.setBackground( Color.WHITE );
        panel_1.setLayout( new MigLayout( "", "[grow]", "[150px:n,grow]" ) );
        sensorPanel = drawSensorPanel();
        panel_1.add( sensorPanel, "cell 0 0,grow" );

        frame.getContentPane().add( panel_1, "cell 0 1,grow" );
        frame.setVisible( true );
        //frame.pack();
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
        SensorPanel sensorPanel_1 = new SensorPanel( this );
        FlowLayout fl_sensorPanel_1 = (FlowLayout) sensorPanel_1.getLayout();
        fl_sensorPanel_1.setVgap( 10 );
        sensorPanel_1.setBackground( Color.WHITE );
        return sensorPanel_1;
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

        JButton btnNewButton_4 = new JButton( "START" );
        btnNewButton_4.setBackground( Color.LIGHT_GRAY );
        btnNewButton_4.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameView.getKsession().insert( new Reset() );
                gameView.getKsession().fireAllRules();
                updateCave();
                updateSensors();
            }
        } );
        actionPanel.add( btnNewButton_4 );

        JButton btnNewButton_5 = new JButton( "SHOOT" );
        btnNewButton_5.setBackground( Color.LIGHT_GRAY );
        btnNewButton_5.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameView.getKsession().insert( new ShootCommand() );
                gameView.getKsession().fireAllRules();
                updateCave();
                updateSensors();
            }
        } );
        actionPanel.add( btnNewButton_5 );

        //        JButton btnNewButton_6 = new JButton( "GRAB" );
        //        btnNewButton_6.setBackground( Color.LIGHT_GRAY );
        //        actionPanel.add( btnNewButton_6 );

        //        JButton btnNewButton_7 = new JButton( "CLIMB" );
        //        btnNewButton_7.setBackground( Color.LIGHT_GRAY );
        //        actionPanel.add( btnNewButton_7 );

        JButton btnNewButton_8 = new JButton( "CAVE?" );
        btnNewButton_8.setBackground( Color.LIGHT_GRAY );
        btnNewButton_8.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ( gameView.isShowAllCells() ) {
                    gameView.setShowAllCells( false );
                } else {
                    gameView.setShowAllCells( true );
                }
                updateCave();
            }
        } );
        actionPanel.add( btnNewButton_8 );

        //        JButton btnNewButton_9 = new JButton( "WUMPUS?" );
        //        btnNewButton_9.setBackground( Color.LIGHT_GRAY );
        //        actionPanel.add( btnNewButton_9 );

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
            if ( gameView.isPitDeath() || gameView.isWumpusDeath() ) {
                gameView.getKlogger().close();
                gameView.setShowAllCells( true );
                updateCave();
                updateSensors();
                int answre = JOptionPane.showConfirmDialog( null,
                                                            "Play Again1?", "DEAD", JOptionPane.OK_OPTION );
                gameView.getWumpusWorld().setData( gameView );
                updateCave();
                updateSensors();
            } else if ( gameView.isGoldWin() ) {
                gameView.getKlogger().close();
                gameView.setShowAllCells( true );
                updateCave();
                updateSensors();
                int answre = JOptionPane.showConfirmDialog( null,
                                                            "Play Again1?", "WIN", JOptionPane.OK_OPTION );
                gameView.getWumpusWorld().setData( gameView );
                updateCave();
                updateSensors();
            }
            else {
                updateCave();
                updateSensors();
            }
        }
    }

    public static class SensorPanel extends JPanel {
        private GameUI gameUI;

        public SensorPanel(GameUI gameUI) {
            this.gameUI = gameUI;
        }

        public void paint(Graphics g) {
            super.paintComponent( g );
            GameView gameView = gameUI.getGameView();
            try {
                SensorsView sensor = gameView.getSensorsview();
                if ( sensor.isFeelBreeze() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "breeze.png" ) );
                    g.drawImage( image, 0, 0, 150, 150, this );
                }

                if ( sensor.isSmellStench() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "stench.png" ) );
                    g.drawImage( image, 153, 0, 150, 150, this );
                }

                if ( sensor.isSeeGlitter() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "glitter.png" ) );
                    g.drawImage( image, 306, 0, 150, 150, this );
                }

                if ( sensor.isFeelBump() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "bump.png" ) );
                    g.drawImage( image, 459, 0, 150, 150, this );
                }

                if ( sensor.isHearScream() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "scream.png" ) );
                    g.drawImage( image, 612, 0, 150, 150, this );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    public static class CavePanel extends JPanel {
        private GameUI gameUI;

        public CavePanel(GameUI gameUI) {
            this.gameUI = gameUI;
        }

        public void paint(Graphics g) {
            GameView gameView = gameUI.getGameView();
            super.paintComponent( g );
            try {
                int rowIndent = 20;
                int colIndent = 5;
                int rowPad = 0;
                for ( int row = 0; row < 5; row++ ) {
                    int colPad = 0;
                    for ( int col = 0; col < 5; col++ ) {
                        int x = (4 - row) * 50 - rowPad + rowIndent;
                        int y = col * 50 + colPad + colIndent;

                        BufferedImage image;
                        if ( !gameView.isShowAllCells() && (gameView.getCells() != null && gameView.getCells()[row][col].isHidden()) ) {
                            image = javax.imageio.ImageIO.read( getClass().getResource( "hidden_room.png" ) );
                        } else {
                            if ( row == gameView.getHero().getRow() && col == gameView.getHero().getCol() ) {
                                Hero hero = gameView.getHero();
                                switch ( hero.getDirection() ) {
                                    case UP :
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_up.png" ) );
                                        break;
                                    case DOWN :
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_down.png" ) );
                                        break;
                                    case LEFT :
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_left.png" ) );
                                        break;
                                    case RIGHT :
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_right.png" ) );
                                        break;
                                    default:
                                        throw new IllegalStateException( "invalid direction: " + hero.getDirection() );
                                }
                            } else {
                                boolean containsPit = gameView.getPits().contains( new Pit( row, col ) );
                                boolean containsAliveWumpus = false;
                                boolean containsDeadWumpus = false;
                                if ( row == gameView.getWumpus().getRow() && col == gameView.getWumpus().getCol() ) {
                                    if ( gameView.getWumpus().isAlive() ) {
                                        containsAliveWumpus = true;
                                    } else {
                                        containsDeadWumpus = true;
                                    }
                                }
                                boolean containsGold = (row == gameView.getGold().getRow() && col == gameView.getGold().getCol());

                                if ( !containsPit && !containsAliveWumpus && !containsDeadWumpus && !containsGold ) {
                                    image = javax.imageio.ImageIO.read( getClass().getResource( "empty_room.png" ) );
                                } else {

                                    String pit = "";
                                    String wumpus = "";
                                    String gold = "";

                                    if ( containsPit ) {
                                        pit = "pit";
                                    }

                                    if ( containsAliveWumpus ) {
                                        wumpus = "alive_wumpus";
                                    } else if ( containsDeadWumpus ) {
                                        wumpus = "dead_wumpus";
                                    }

                                    if ( containsGold ) {
                                        gold = "gold";
                                    }

                                    image = javax.imageio.ImageIO.read( getClass().getResource( pit + wumpus + gold + ".png" ) );
                                }
                            }
                        }
                        g.drawImage( image, y, x, 50, 50, this );
                        colPad = colPad + 3;
                    }
                    rowPad = rowPad + 3;
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }


}
