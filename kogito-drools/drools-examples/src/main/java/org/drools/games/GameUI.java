package org.drools.games;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameUI {
    private GameConfiguration conf;

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

    /**
     * Initialize the contents of the frame.
     */
    public void init() {
        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        canvas.setSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));

        KeyListener klistener = new GameKeyListener( ksession.getEntryPoint( "KeyPressedStream" ), ksession.getEntryPoint( "KeyReleasedStream" ) );
        canvas.addKeyListener(klistener);

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                canvas.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(conf.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));
        frame.setBackground(Color.BLACK);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center in screen

        canvas.createBufferStrategy(2);
        canvas.requestFocus();
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
