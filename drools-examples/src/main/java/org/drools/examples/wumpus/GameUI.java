package org.drools.examples.wumpus;

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

import org.drools.runtime.StatefulKnowledgeSession;

import net.miginfocom.swing.MigLayout;

public class GameUI {

    private JFrame                   frame;

    private GameData                 gameData;

    private CavePanel                cavePanel;

    private SensorPanel              sensorPanel;

    public static final void main(String[] args) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    GameUI window = new GameUI();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    public static final void run(final GameData gameData) {
        GameUI window = new GameUI(gameData);
        window.initialize();        
    }

    public GameUI() {
        this.gameData = new GameData();
        this.gameData.init( new Cell[5][5], new Sensors(), new ArrayList<Pitt>(), new Wumpus( 2, 1 ), new Gold( 3, 1 ), new Hero( 0, 0 ) );
    }

    public GameUI(GameData gameData) {
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

        frame.setSize( 700, 500 );
        frame.getContentPane().setLayout( new MigLayout( "", "[grow]", "[:100px:300px,grow][::300px]" ) );

        JPanel panel = new JPanel();
        panel.setBackground( Color.WHITE );
        frame.getContentPane().add( panel, "cell 0 0,grow" );
        panel.setLayout( new MigLayout( "", "[200px:200px:200px,left][400px:400px:400px][]", "[grow]" ) );

        JPanel panel_2 = new JPanel();
        panel_2.setBackground( Color.WHITE );
        panel_2.setLayout( new MigLayout( "", "[grow,fill]", "[grow][60px:n,grow][grow]" ) );
        panel_2.add( drawActionPanel(), "cell 0 0,alignx left,aligny top" );

        panel_2.add( drawMovePanel(), "cell 0 2,alignx left,growy" );
        sensorPanel = drawSensorPanel();
        panel_2.add( sensorPanel, "cell 0 1,grow" );
        panel.add( panel_2, "cell 0 0,grow" );

        cavePanel = drawCave();
        panel.add( cavePanel, "cell 1 0 2 1,grow" );

        JPanel panel_1 = new JPanel();
        panel_1.setBackground( Color.WHITE );
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

//        JButton btnNewButton_5 = new JButton( "SHOOT" );
//        btnNewButton_5.setBackground( Color.LIGHT_GRAY );
//        actionPanel.add( btnNewButton_5 );

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
        btnNewButton.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/up.png" ) ) );
        btnNewButton.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameData.getKsession().insert( new MoveCommand( Move.UP ) );
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
        } );
        
        movePanel.add( btnNewButton );

        JPanel panel_7 = new JPanel();
        panel_7.setBackground( Color.WHITE );
        movePanel.add( panel_7 );

        JButton btnNewButton_3 = new JButton( "" );
        btnNewButton_3.setForeground( Color.WHITE );
        btnNewButton_3.setBackground( Color.WHITE );
        btnNewButton_3.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/left.png" ) ) );
        btnNewButton_3.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameData.getKsession().insert( new MoveCommand( Move.LEFT ) );
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
        } );
        movePanel.add( btnNewButton_3 );

        JPanel panel_8 = new JPanel();
        panel_8.setBackground( Color.WHITE );
        movePanel.add( panel_8 );

        JButton btnNewButton_2 = new JButton( "" );
        btnNewButton_2.setForeground( Color.WHITE );
        btnNewButton_2.setBackground( Color.WHITE );
        btnNewButton_2.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/right.png" ) ) );
        btnNewButton_2.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameData.getKsession().insert( new MoveCommand( Move.RIGHT ) );
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
        } );
        movePanel.add( btnNewButton_2 );

        JPanel panel_9 = new JPanel();
        panel_9.setBackground( Color.WHITE );
        movePanel.add( panel_9 );

        JButton btnNewButton_1 = new JButton( "" );
        btnNewButton_1.setForeground( Color.WHITE );
        btnNewButton_1.setBackground( Color.WHITE );
        btnNewButton_1.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/down.png" ) ) );
        btnNewButton_1.addMouseListener( new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                gameData.getKsession().insert( new MoveCommand( Move.DOWN ) );
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
        } );
        movePanel.add( btnNewButton_1 );

        JPanel panel_10 = new JPanel();
        panel_10.setBackground( Color.WHITE );
        movePanel.add( panel_10 );

        return movePanel;
    }

    public static class SensorPanel extends JPanel {
        private GameData gameData;

        public SensorPanel(GameData gameData) {
            this.gameData = gameData;
        }

        public void paint(Graphics g) {
            super.paintComponent( g );
            try {
                Sensors sensor = gameData.getSensors();
                if ( sensor.isFeelBreeze() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "breeze.png" ) );
                    g.drawImage( image, 0, 0, 50, 50, this );
                }

                if ( sensor.isSmellStench() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "stench.png" ) );
                    g.drawImage( image, 53, 0, 50, 50, this );
                }

                if ( sensor.isSeeGlitter() ) {
                    BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "glitter.png" ) );
                    g.drawImage( image, 106, 0, 50, 50, this );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    public static class CavePanel extends JPanel {
        private GameData gameData;

        public CavePanel(GameData gameData) {
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
                                image = javax.imageio.ImageIO.read( getClass().getResource( "wumpus.png" ) );
                            } else if ( row == gameData.getGold().getRow() && col == gameData.getGold().getCol() ) {
                                image = javax.imageio.ImageIO.read( getClass().getResource( "g.png" ) );
                            } else if ( row == gameData.getHero().getRow() && col == gameData.getHero().getCol() ) {
                                image = javax.imageio.ImageIO.read( getClass().getResource( "hero.png" ) );
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
