package org.drools.examples.wumpus;

import java.awt.EventQueue;

import javax.management.RuntimeErrorException;
import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.FlowLayout;

public class WumpusApplicationWindow {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    WumpusApplicationWindow window = new WumpusApplicationWindow();
                    window.frame.setVisible( true );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * Create the application.
     * @throws IOException 
     */
    public WumpusApplicationWindow() throws IOException {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     * @throws IOException 
     */
    private void initialize() throws IOException {
        frame = new JFrame();
        frame.getContentPane().setBackground( Color.WHITE );
        frame.setBounds( 100, 100, 733, 646 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        frame.getContentPane().setLayout( gridBagLayout );

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground( Color.WHITE );
        GridBagConstraints gbc_actionPanel = new GridBagConstraints();
        gbc_actionPanel.gridheight = 8;
        gbc_actionPanel.gridwidth = 5;
        gbc_actionPanel.insets = new Insets( 0, 0, 5, 5 );
        gbc_actionPanel.fill = GridBagConstraints.BOTH;
        gbc_actionPanel.gridx = 0;
        gbc_actionPanel.gridy = 0;
        frame.getContentPane().add( actionPanel, gbc_actionPanel );
        actionPanel.setLayout( new GridLayout( 0, 2, 0, 0 ) );

        JButton btnNewButton_4 = new JButton( "START" );
        btnNewButton_4.setBackground( Color.LIGHT_GRAY );
        btnNewButton_4.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        } );
        actionPanel.add( btnNewButton_4 );

        JButton btnNewButton_5 = new JButton( "SHOOT" );
        btnNewButton_5.setBackground( Color.LIGHT_GRAY );
        actionPanel.add( btnNewButton_5 );

        JButton btnNewButton_6 = new JButton( "GRAB" );
        btnNewButton_6.setBackground( Color.LIGHT_GRAY );
        actionPanel.add( btnNewButton_6 );

        JButton btnNewButton_7 = new JButton( "CLIMB" );
        btnNewButton_7.setBackground( Color.LIGHT_GRAY );
        actionPanel.add( btnNewButton_7 );

        JButton btnNewButton_8 = new JButton( "PITT?" );
        btnNewButton_8.setBackground( Color.LIGHT_GRAY );
        actionPanel.add( btnNewButton_8 );

        JButton btnNewButton_9 = new JButton( "WUMPUS?" );
        btnNewButton_9.setBackground( Color.LIGHT_GRAY );
        actionPanel.add( btnNewButton_9 );

        JPanel dividerPanel = new JPanel();
        dividerPanel.setBackground( Color.WHITE );
        GridBagConstraints gbc_dividerPanel = new GridBagConstraints();
        gbc_dividerPanel.gridheight = 11;
        gbc_dividerPanel.insets = new Insets( 0, 0, 5, 5 );
        gbc_dividerPanel.fill = GridBagConstraints.BOTH;
        gbc_dividerPanel.gridx = 5;
        gbc_dividerPanel.gridy = 0;
        frame.getContentPane().add( dividerPanel, gbc_dividerPanel );

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridheight = 20;
        gbc_panel.gridwidth = 2;
        gbc_panel.insets = new Insets( 0, 0, 0, 5 );
        gbc_panel.gridx = 18;
        gbc_panel.gridy = 1;
        frame.getContentPane().add( panel, gbc_panel );

        JPanel movePanel = new JPanel();
        movePanel.setBackground( Color.WHITE );
        GridBagConstraints gbc_movePanel = new GridBagConstraints();
        gbc_movePanel.gridwidth = 5;
        gbc_movePanel.gridheight = 4;
        gbc_movePanel.insets = new Insets( 0, 0, 5, 5 );
        gbc_movePanel.fill = GridBagConstraints.BOTH;
        gbc_movePanel.gridx = 0;
        gbc_movePanel.gridy = 8;
        frame.getContentPane().add( movePanel, gbc_movePanel );
        movePanel.setLayout( new GridLayout( 0, 3, 0, 0 ) );

        JPanel panel_2 = new JPanel();
        panel_2.setBackground( Color.WHITE );
        movePanel.add( panel_2 );

        JButton btnNewButton = new JButton( "" );
        btnNewButton.setForeground( Color.WHITE );
        btnNewButton.setBackground( Color.WHITE );
        btnNewButton.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/up.png" ) ) );
        movePanel.add( btnNewButton );

        JPanel panel_7 = new JPanel();
        panel_7.setBackground( Color.WHITE );
        movePanel.add( panel_7 );

        JButton btnNewButton_3 = new JButton( "" );
        btnNewButton_3.setForeground( Color.WHITE );
        btnNewButton_3.setBackground( Color.WHITE );
        btnNewButton_3.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/left.png" ) ) );
        movePanel.add( btnNewButton_3 );

        JPanel panel_8 = new JPanel();
        panel_8.setBackground( Color.WHITE );
        movePanel.add( panel_8 );

        JButton btnNewButton_2 = new JButton( "" );
        btnNewButton_2.setForeground( Color.WHITE );
        btnNewButton_2.setBackground( Color.WHITE );
        btnNewButton_2.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/right.png" ) ) );
        movePanel.add( btnNewButton_2 );

        JPanel panel_9 = new JPanel();
        panel_9.setBackground( Color.WHITE );
        movePanel.add( panel_9 );

        JButton btnNewButton_1 = new JButton( "" );
        btnNewButton_1.setForeground( Color.WHITE );
        btnNewButton_1.setBackground( Color.WHITE );
        btnNewButton_1.setIcon( new ImageIcon( WumpusApplicationWindow.class.getResource( "/org/drools/examples/wumpus/down.png" ) ) );
        movePanel.add( btnNewButton_1 );

        JPanel panel_10 = new JPanel();
        panel_10.setBackground( Color.WHITE );
        movePanel.add( panel_10 );

        JPanel panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.gridwidth = 22;
        gbc_panel_1.gridheight = 8;
        gbc_panel_1.insets = new Insets( 0, 0, 5, 5 );
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 12;
        frame.getContentPane().add( panel_1, gbc_panel_1 );
        panel_1.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

        JTextArea textArea = new JTextArea();
        textArea.setColumns( 80 );
        textArea.setRows( 10 );
        panel_1.add( textArea );

        JPanel cavePanel = new JPanel();
        cavePanel.setBackground( Color.WHITE );
        //        GridBagConstraints gbc_cavePanel = new GridBagConstraints();
        //        gbc_cavePanel.insets = new Insets(0, 0, 5, 5);
        //        gbc_cavePanel.gridheight = 12;
        //        gbc_cavePanel.gridwidth = 16;
        //        gbc_cavePanel.fill = GridBagConstraints.BOTH;
        //        gbc_cavePanel.gridx = 6;
        //        gbc_cavePanel.gridy = 0;
        GridBagConstraints gbc_cavePanel = new GridBagConstraints();
        gbc_cavePanel.fill = GridBagConstraints.BOTH;
        gbc_cavePanel.gridwidth = 16;
        gbc_cavePanel.gridheight = 8;
        gbc_cavePanel.gridy = 4;
        gbc_cavePanel.gridx = 7;
        frame.getContentPane().add( cavePanel, gbc_cavePanel );
        //        cavePanel.setLayout(new GridLayout(0, 5));

        ImageBackgroundPanel imagePanel = new ImageBackgroundPanel();
        imagePanel.setSize( 500, 500 );
        cavePanel.add( imagePanel );
        //      imagePanel.set
        //      cavePanel.add(imagePanel);
        //      JButton btn1 = new JButton("X");
        //      cavePanel.add(btn1);

        //        for ( int i = 0; i < 25; i++ ) {
        //            System.out.println(getClass().getResource( "pitt.png" ) );
        //            BufferedImage image = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //            ImageBackgroundPanel imagePanel = new ImageBackgroundPanel(image);
        //            imagePanel.setSize( 100, 100 );
        ////            imagePanel.set
        ////            cavePanel.add(imagePanel);
        ////            JButton btn1 = new JButton("X");
        ////            cavePanel.add(btn1);
        //        }

        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image0 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel0 = new ImageBackgroundPanel(image0);
        //        imagePanel0.setSize( 150, 150 );
        //        cavePanel.add(imagePanel0);
        //        
        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image1 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel1 = new ImageBackgroundPanel(image1);
        //        imagePanel1.setSize( 150, 150 );
        //        cavePanel.add(imagePanel1);
        //        
        //        
        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image2 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel2 = new ImageBackgroundPanel(image2);
        //        imagePanel2.setSize( 150, 150 );
        //        cavePanel.add(imagePanel2);
        //        
        //        
        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image3 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel3 = new ImageBackgroundPanel(image3);
        //        imagePanel3.setSize( 150, 150 );
        //        cavePanel.add(imagePanel3);
        //        
        //        
        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image4 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel4 = new ImageBackgroundPanel(image4);
        //        imagePanel4.setSize( 150, 150 );
        //        cavePanel.add(imagePanel4);
        //        
        //        
        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image5 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel5 = new ImageBackgroundPanel(image5);
        //        imagePanel5.setSize( 150, 150 );
        //        cavePanel.add(imagePanel5);

        //        System.out.println(getClass().getResource( "pitt.png" ) );
        //        BufferedImage image6 = javax.imageio.ImageIO.read(getClass().getResource( "pitt.png" ));
        //        ImageBackgroundPanel imagePanel6 = new ImageBackgroundPanel(image6);
        //        imagePanel6.setSize( 150, 150 ); 
        //        cavePanel.add(imagePanel6);

        //        //JPanel panel_2 = new JPanel();
        //        panel.add(imagePanel);        
        //        
        //        JPanel panel_3 = new JPanel();
        //        cavePanel.add(panel_3);
        //        
        //        JPanel panel_4 = new JPanel();
        //        cavePanel.add(panel_4);
        //        
        //        JPanel panel_5 = new JPanel();
        //        cavePanel.add(panel_5);
        //        
        //        JPanel panel_6 = new JPanel();
        //        cavePanel.add(panel_6);
        //        
        //        JPanel panel_12 = new JPanel();
        //        cavePanel.add(panel_12);        
    }

    public class ImageBackgroundPanel extends JPanel {
        //BufferedImage image;

        ImageBackgroundPanel() {
            //this.image = image;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent( g );
            try {
                for ( int i = 0; i < 5; i++ ) {
                    for ( int j = 0; j < 5; j++ ) {
                        int row = i * 50;
                        int col = j * 50;
                        BufferedImage image = javax.imageio.ImageIO.read( getClass().getResource( "pitt.png" ) );
                        g.drawImage( image, row, col, 50, 50, this );
                    }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }

        }
    }

}
