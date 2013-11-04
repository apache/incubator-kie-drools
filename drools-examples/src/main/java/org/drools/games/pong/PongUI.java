package org.drools.games.pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.EntryPoint;

public class PongUI {
    private PongConfiguration pconf;

    private JFrame     frame;
    private Canvas     canvas;

    private int x;
    private int y;
//    private BufferedImage areaCopy;
//    private Graphics areaCopyG;

//    private TablePanel tablePanel;
//
//    private boolean    ready;
    
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

    public Graphics getGraphics() {
        return canvas.getBufferStrategy().getDrawGraphics();
    }


    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }

    public void setKsession(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    /**
     * Initialize the contents of the frame.
     */
    public void init(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
        
        frame = new JFrame();

        // must add before visible, and also request focus. As sometimes it would fail to attach. (mdp still seeing the problem, apparently a konwn swing bug)
        KeyListener klistener = new PongKeyListener( ksession.getEntryPoint( "KeyPressedStream" ), ksession.getEntryPoint( "KeyReleasedStream" ) );
        frame.addKeyListener( klistener );

        frame.setResizable(false);
        frame.setDefaultCloseOperation(pconf.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

        frame.setSize(new Dimension(pconf.getTableWidth(), pconf.getTableHeight()));


        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        canvas.setSize(new Dimension(pconf.getTableWidth() + 100, pconf.getTableHeight()));

        // Use ScrollPanel for offscreen drawing area, to preserve background
        JScrollPane jsp = new JScrollPane( canvas );
        jsp.setPreferredSize(new Dimension(pconf.getTableWidth(), pconf.getTableHeight()));
        jsp.add( canvas );

        frame.add(jsp);
        frame.setVisible( true );
        frame.pack();
        canvas.createBufferStrategy(2);

        frame.setLocationRelativeTo(null); // Center in screen
        frame.requestFocus();
    }

    public static class PongKeyListener implements KeyListener {
        
        EntryPoint keyPressedEntryPoint;
        EntryPoint keyReleasedEntryPoint;

        public PongKeyListener(EntryPoint keyPressedEntryPoint,
                               EntryPoint keyReleasedEntryPoint) {           
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
        canvas.getBufferStrategy().show();
        Toolkit.getDefaultToolkit().sync();
    }


    public void restoreBallBackground(Ball ball) {
        Graphics g = getGraphics(); //ui.getTablePanel().getTableG();
        int copyX = 0 - ( pconf.getTableWidth() - ball.getX()  +2);
        int copyY = ball.getY()-2;
        g.copyArea( pconf.getTableWidth(), 0,
                    ball.getWidth()+4, ball.getWidth()+4,
                    copyX,copyY );
    }

    public void drawBall(Ball ball) {
        Graphics g = getGraphics();

        // ball must preserve the area it's being drawn over, such as the net
        int copyX = pconf.getTableWidth() - ball.getX() + 2;
        int copyY = 0 - (ball.getY()-2);

        g.copyArea( ball.getX()-2, ball.getY()-2,
                    ball.getWidth()+4, ball.getWidth()+4,
                    copyX, copyY );

        g.setColor( Color.WHITE ); // background
        g.fillOval( ball.getX(), ball.getY(), ball.getWidth(), ball.getWidth() );
    }

    public void restoreBatBackground(Bat bat) {
        Graphics g = getGraphics();

        g.setColor( Color.BLACK ); // background
        g.fillRect( bat.getX(), bat.getY(), bat.getWidth(), bat.getHeight() );
    }

    public void drawBat(Bat bat) {
        Graphics g = getGraphics();

        g.setColor( Color.WHITE ); // background
        g.fillRect( bat.getX(), bat.getY(), bat.getWidth(), bat.getHeight() );
    }

    public void drawScore(Player p, int x, PongConfiguration pconf) {
        Graphics g = getGraphics(); //ui.getTablePanel().getTableG();
        int y = (pconf.getPadding() + pconf.getSideLineWidth() + 50);

        g.setColor( Color.BLACK ); // background
        g.fillRect( x, y-50, 80, 60 );

        FontRenderContext frc = ((Graphics2D)g).getFontRenderContext();
        Font f = new Font("Monospaced",Font.BOLD, 70);
        String s = "" + p.getScore();
        TextLayout tl = new TextLayout(s, f, frc);
        g.setColor( Color.WHITE );
        tl.draw(((Graphics2D)g), x, y );
    }
}
