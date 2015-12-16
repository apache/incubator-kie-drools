/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
    private GameFrame   frame;
    private MyJPanel    panel;

    KieSession ksession;

    public GameUI(KieSession ksession, GameConfiguration conf) {
        this.ksession = ksession;
        this.conf = conf;
    }

    /**
     * Initialize the contents of the frame.
     */
    public void init() {
        frame = new GameFrame();
        frame.setDefaultCloseOperation(conf.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable( false );
        frame.setBackground(Color.BLACK);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));

        panel = new MyJPanel("", Color.BLACK);
        frame.add( panel );
        panel.init();
        panel.getBufferedImage();

        frame.setLocationRelativeTo(null); // Center in screen
        frame.pack();
        frame.setVisible( true );
    }


    public JPanel getCanvas() {
        return panel;
    }

    public Graphics getGraphics() {
        return panel.getGraphics2D();
    }

    public void repaint() {
        panel.disposeGraphics2D();
        frame.waitForPaint();
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

    public class MyJPanel extends GamePanel {

        public MyJPanel(String name, Color color) {
            super(name, color);
        }

        public void init() {
            KeyListener klistener = new GameKeyListener( ksession.getEntryPoint( "KeyPressedStream" ), ksession.getEntryPoint( "KeyReleasedStream" ) );
            addKeyListener(klistener);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    requestFocus();
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

            setPreferredSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));
            setSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));
            setBackground(Color.BLACK);
            setDoubleBuffered(true);


            setFocusable(true);
            requestFocus();
        }

    }
}
