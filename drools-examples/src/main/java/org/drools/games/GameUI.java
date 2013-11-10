package org.drools.games;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameUI {
    private GameConfiguration conf;

    private JFrame     frame;
    private Canvas     canvas;

    KieSession ksession;

    public GameUI(KieSession ksession, GameConfiguration conf) {
        this.ksession = ksession;
        this.conf = conf;
    }

    public Graphics getGraphics() {
        return canvas.getBufferStrategy().getDrawGraphics();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void init() {
        frame = new JFrame();

        // must add before visible, and also request focus. As sometimes it would fail to attach. (mdp still seeing the problem, apparently a konwn swing bug)
        KeyListener klistener = new GameKeyListener( ksession.getEntryPoint( "KeyPressedStream" ), ksession.getEntryPoint( "KeyReleasedStream" ) );
        frame.addKeyListener( klistener );

        frame.setResizable(false);
        frame.setDefaultCloseOperation(conf.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

        frame.setSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));


        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        canvas.setSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));

        JPanel jp = new JPanel(  );
        jp.setBackground(Color.BLACK);
        jp.setPreferredSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));
        jp.add( canvas );

        frame.add(jp);
        frame.setVisible( true );
        frame.pack();
        canvas.createBufferStrategy(2);

        frame.setLocationRelativeTo(null); // Center in screen
        frame.requestFocus();
    }

    public static class GameKeyListener implements KeyListener {
        
        EntryPoint keyPressedEntryPoint;
        EntryPoint keyReleasedEntryPoint;

        public GameKeyListener(EntryPoint keyPressedEntryPoint,
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

    public synchronized void show() {
        canvas.getBufferStrategy().show();
        Toolkit.getDefaultToolkit().sync();
    }
}
