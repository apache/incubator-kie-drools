package org.drools.examples.wumpus.view;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

import org.drools.examples.wumpus.Cell;
import org.drools.examples.wumpus.Gold;
import org.drools.examples.wumpus.Hero;
import org.drools.examples.wumpus.Move;
import org.drools.examples.wumpus.MoveCommand;
import org.drools.examples.wumpus.Pitt;
import org.drools.examples.wumpus.ShootCommand;
import org.drools.examples.wumpus.Wumpus;
import org.drools.examples.wumpus.WumpusApplicationWindow;
import org.drools.runtime.StatefulKnowledgeSession;

import net.miginfocom.swing.MigLayout;

public class GameUI {

    private JFrame                   frame;

    private GameView                 gameData;

    private CavePanel                cavePanel;

    private SensorPanel              sensorPanel;


    public static final void main(String[] args) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    GameUI window = new GameUI();
                    window.initialize();                      
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }



    public GameUI() {
        this.gameData = new GameView();
        this.gameData.init( new Cell[5][5], new SensorsView(), new ArrayList<Pitt>(), new Wumpus( 2, 1 ), new Gold( 3, 1 ), new Hero( 0, 0 ) );
    }
    
    public static final void run(final GameView gameData) {
        GameUI window = new GameUI(gameData);
        window.initialize();        
    }    

    public GameUI(GameView gameData) {
        this.gameData = gameData;
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
        frame.getContentPane().setLayout( new MigLayout("", "[grow]", "[300px:n,grow][100px:n,grow]") );

        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        frame.getContentPane().add( panel, "cell 0 0,grow" );
        panel.setLayout( new MigLayout("", "[200px:200px:200px,left][grow]", "[grow]") );

        JPanel controls = new JPanel();
        controls.setBackground( Color.WHITE );
        controls.setLayout( new MigLayout("", "[grow,fill]", "[::100px,top][200px,top]") );
        controls.add( drawActionPanel(), "cell 0 0,alignx left,aligny top" );

        controls.add( drawMovePanel(), "cell 0 1,alignx left,growy" );
        
//        sensorPanel = drawSensorPanel();
//        panel_2.add( sensorPanel, "cell 0 1,grow" );
        
        panel.add( controls, "cell 0 0,grow" );

        cavePanel = drawCave();
        panel.add( cavePanel, "cell 1 0,grow" );

        JPanel panel_1 = new JPanel();
        panel_1.setBackground( Color.WHITE );
        panel_1.setLayout(new MigLayout("", "[grow]", "[150px:n,grow]"));
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
        SensorPanel sensorPanel_1 = new SensorPanel( gameData );
        FlowLayout fl_sensorPanel_1 = (FlowLayout) sensorPanel_1.getLayout();
        fl_sensorPanel_1.setVgap( 10 );
        sensorPanel_1.setBackground( Color.WHITE );
        return sensorPanel_1;
    }

    public CavePanel drawCave() {
        CavePanel cavelPanel = new CavePanel( gameData );
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
                gameData.getWumpusWorld().setData( gameData );                            
                updateCave();
                updateSensors();
            }
        } );         
        actionPanel.add( btnNewButton_4 );

        JButton btnNewButton_5 = new JButton( "SHOOT" );
        btnNewButton_5.setBackground( Color.LIGHT_GRAY );
        btnNewButton_5.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameData.getKsession().insert( new ShootCommand() );
                gameData.getKsession().fireAllRules();
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
                if ( gameData.isShowAllCells() ) {
                    gameData.setShowAllCells( false );    
                } else {
                    gameData.setShowAllCells( true );
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
        btnNewButton.addMouseListener( new MoveButtonPressed(Move.MOVE_FORWARD) );
        
        movePanel.add( btnNewButton );

        JPanel panel_7 = new JPanel();
        panel_7.setBackground( Color.WHITE );
        movePanel.add( panel_7 );

        JButton btnNewButton_3 = new JButton( "" );
        btnNewButton_3.setForeground( Color.WHITE );
        btnNewButton_3.setBackground( Color.WHITE );
        btnNewButton_3.setIcon( new ImageIcon( getClass().getResource( "left.png" ) ) );
        btnNewButton_3.addMouseListener( new MoveButtonPressed(Move.TURN_LEFT) );
        movePanel.add( btnNewButton_3 );

        JPanel panel_8 = new JPanel();
        panel_8.setBackground( Color.WHITE );
        movePanel.add( panel_8 );

        JButton btnNewButton_2 = new JButton( "" );
        btnNewButton_2.setForeground( Color.WHITE );
        btnNewButton_2.setBackground( Color.WHITE );
        btnNewButton_2.setIcon( new ImageIcon( getClass().getResource( "right.png" ) ) );
        btnNewButton_2.addMouseListener( new MoveButtonPressed(Move.TURN_RIGHT) );
        movePanel.add( btnNewButton_2 );

        JPanel panel_9 = new JPanel();
        panel_9.setBackground( Color.WHITE );
        movePanel.add( panel_9 );

        JButton btnNewButton_1 = new JButton( "" );
        btnNewButton_1.setForeground( Color.WHITE );
        btnNewButton_1.setBackground( Color.WHITE );
        btnNewButton_1.setIcon( new ImageIcon( getClass().getResource( "down.png" ) ) );
        btnNewButton_1.addMouseListener(new MoveButtonPressed(Move.MOVE_BACKWARD));
        movePanel.add( btnNewButton_1 );

        JPanel panel_10 = new JPanel();
        panel_10.setBackground( Color.WHITE );
        movePanel.add( panel_10 );

        return movePanel;
    }

    private final class MoveButtonPressed extends MouseAdapter {
        private Move move;
        
        public MoveButtonPressed( Move move) {
            this.move = move;
        }
        
        public void mousePressed(MouseEvent e) {                                    
            gameData.getKsession().insert( new MoveCommand( move ) );
            
            gameData.getKsession().fireAllRules();
            if ( gameData.isPittDeath() || gameData.isWumpusDeath() ) {
                gameData.getKlogger().close();
                gameData.setShowAllCells( true );
                updateCave();
                updateSensors();
                int answre = JOptionPane.showConfirmDialog(null,
                                                           "Play Again1?", "DEAD", JOptionPane.OK_OPTION);
                gameData.getWumpusWorld().setData( gameData );                            
                updateCave();
                updateSensors();                    
            } else if ( gameData.isGoldWin() ) {
                gameData.getKlogger().close();
                gameData.setShowAllCells( true );
                updateCave();
                updateSensors();
                int answre = JOptionPane.showConfirmDialog(null,
                                                           "Play Again1?", "WIN", JOptionPane.OK_OPTION);
                gameData.getWumpusWorld().setData( gameData );                            
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
        private GameView gameData;

        public SensorPanel(GameView gameData) {
            this.gameData = gameData;
        }

        public void paint(Graphics g) {
            super.paintComponent( g );
            try {
                SensorsView sensor = gameData.getSensorsview();
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
        private GameView gameData;

        public CavePanel(GameView gameData) {
            this.gameData = gameData;
        }

        public void paint(Graphics g) {
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

                        BufferedImage image = null;
                        if ( !gameData.isShowAllCells() && ( gameData.getCells() != null && gameData.getCells()[row][col].isHidden() ) ) {
                            image = javax.imageio.ImageIO.read( getClass().getResource( "hidden_room.png" ) );
                        } else {                                                
                            if ( gameData.getPits().contains( new Pitt( row, col ) ) ) {
                                image = javax.imageio.ImageIO.read( getClass().getResource( "pitt.png" ) );
                            } else if ( row == gameData.getWumpus().getRow() && col == gameData.getWumpus().getCol() ) {
                                if ( gameData.getWumpus().isAlive() ) {
                                    image = javax.imageio.ImageIO.read( getClass().getResource( "alive_wumpus.png" ) );
                                } else {
                                    image = javax.imageio.ImageIO.read( getClass().getResource( "dead_wumpus.png" ) );
                                }
                            } else if ( row == gameData.getGold().getRow() && col == gameData.getGold().getCol() ) {
                                image = javax.imageio.ImageIO.read( getClass().getResource( "g.png" ) );
                            } else if ( row == gameData.getHero().getRow() && col == gameData.getHero().getCol() ) {
                                Hero hero = gameData.getHero();
                                switch( hero.getDirection() ) {
                                    case UP:
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_up.png" ) );
                                        break;
                                    case DOWN:
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_down.png" ) );
                                        break;                                        
                                    case LEFT:
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_left.png" ) );
                                        break;                                        
                                    case RIGHT:
                                        image = javax.imageio.ImageIO.read( getClass().getResource( "hero_right.png" ) );
                                        break;                                        
                                }
                            } else {
                                image = javax.imageio.ImageIO.read( getClass().getResource( "empty_room.png" ) );
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
