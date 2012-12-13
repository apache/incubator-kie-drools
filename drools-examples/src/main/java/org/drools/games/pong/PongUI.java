package org.drools.games.pong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.rule.FactHandle;
import org.kie.runtime.rule.SessionEntryPoint;

public class PongUI {
    private PongConfiguration pconf;

    private JFrame     frame;

    private TablePanel tablePanel;

    private boolean    ready;
    
    StatefulKnowledgeSession ksession;

    /**
     * @wbp.parser.entryPoint
     */
    public PongUI() {
        this(new PongConfiguration());
        init( null );
    }
    public PongUI(PongConfiguration pconf) {
        this.pconf = pconf;
    }

    
    
    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }

    public void setKsession(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    public PongConfiguration getPconf() {
        return pconf;
    }

    public void setPconf(PongConfiguration pconf) {
        this.pconf = pconf;
    }

    public TablePanel getTablePanel() {
        return tablePanel;
    }

    public void setTablePanel(TablePanel tablePanel) {
        this.tablePanel = tablePanel;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Initialize the contents of the frame.
     */
    public void init(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
        
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(pconf.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout( new BorderLayout() );

        tablePanel = new TablePanel( this );
        tablePanel.setPreferredSize( new Dimension(pconf.getTableWidth(), pconf.getTableHeight()) );
        tablePanel.setBackground( Color.BLACK );
        frame.getContentPane().add( tablePanel );
        tablePanel.setLayout( new BorderLayout( ) );
        frame.pack();
        frame.setLocationRelativeTo(null); // Center in screen

        frame.setVisible( true );
        
        KeyListener klistener = new PongKeyListener( ksession.getWorkingMemoryEntryPoint( "KeyPressedStream" ), ksession.getWorkingMemoryEntryPoint( "KeyReleasedStream" ) );
        frame.addKeyListener( klistener );
        
        updateTable();
    }

    public static class PongKeyListener implements KeyListener {
        
        SessionEntryPoint keyPressedEntryPoint;
        SessionEntryPoint keyReleasedEntryPoint;

        public PongKeyListener(SessionEntryPoint keyPressedEntryPoint,
                               SessionEntryPoint keyReleasedEntryPoint) {           
            this.keyPressedEntryPoint = keyPressedEntryPoint;
            this.keyReleasedEntryPoint = keyReleasedEntryPoint;
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            this.keyPressedEntryPoint.insert( e );
        }

        public void keyReleased(KeyEvent e) {
            this.keyReleasedEntryPoint.insert( e );
        }        
    }    
    
    public synchronized void updateTable() {
        tablePanel.repaint();
        tablePanel.revalidate();
    }

    public static class TablePanel extends JPanel {
        private PongUI        pongUI;

        private BufferedImage bi;
        private Graphics      tableG;

        public TablePanel(PongUI pongUI) {
            setOpaque( true );
            this.pongUI = pongUI;
        }

        public Graphics getTableG() {
            if ( bi == null ) { // prepare BufferdImage
                PongConfiguration pconf = pongUI.getPconf();
                
                bi = new BufferedImage( pconf.getTableWidth() + 200, pconf.getTableHeight(),
                                        BufferedImage.TYPE_INT_RGB );
                tableG = bi.createGraphics();
                tableG.setColor( Color.BLACK ); // background
                tableG.fillRect( 0, 0,  pconf.getTableWidth() + 200, pconf.getTableHeight() ); // +200 hidden for double buffer space

                StatefulKnowledgeSession ksession =  pongUI.getKsession();
                FactHandle fh = ksession.getFactHandle( pongUI );                
                pongUI.setReady( true );
                ksession.update( fh, pongUI );
            }
            return tableG;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if ( pongUI != null ) {
                getTableG();
    
                PongConfiguration pconf = pongUI.getPconf();
                
                g.drawImage( bi, 0, 0, pconf.getTableWidth(), pconf.getTableHeight(),
                             0, 0, pconf.getTableWidth(), pconf.getTableHeight(),
                             null );
            }
        }
    }
}
