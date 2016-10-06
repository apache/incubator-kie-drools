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
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class GameUI extends Canvas{
    private GameConfiguration conf;
    private JFrame   frame;
    private JPanel    panel;
    private BufferStrategy bufferStrategy;
    private Graphics2D graphics;

    KieSession ksession;

    public GameUI(KieSession ksession, GameConfiguration conf) {
        this.ksession = ksession;
        this.conf = conf;
    }

    /**
     * Initialize the contents of the frame.
     */
    public void init() {
        frame =  new JFrame("Drools Example");
        frame.setDefaultCloseOperation(conf.isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable( false );
        frame.setBackground(Color.BLACK);
        frame.getContentPane().setBackground(Color.BLACK);


        panel = (JPanel) frame.getContentPane(); 
        panel.setPreferredSize(new Dimension(conf.getWindowWidth(), conf.getWindowHeight()));
        panel.setLayout(null);

        setBounds(0, 0, conf.getWindowWidth(), conf.getWindowHeight());
        panel.add(this);
        setIgnoreRepaint(true);

        KeyListener klistener = new GameKeyListener( ksession.getEntryPoint( "KeyPressedStream" ), ksession.getEntryPoint( "KeyReleasedStream" ) );
        addKeyListener(klistener);

        frame.setLocationRelativeTo(null); // Center in screen
        frame.pack();
        frame.setResizable(false);
        frame.setVisible( true );

        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }


    public JPanel getCanvas() {
        return panel;
    }

    public Graphics getGraphics() {
        if ( graphics == null ) {
            graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
        }
        return graphics;
    }

    public  void disposeGraphics() {
        if ( graphics != null ) {
            graphics.dispose();
        }
        graphics = null;
    }
    
    public void repaint() {
        disposeGraphics();
        getBufferStrategy().show();
    }

    public static class GameKeyListener extends KeyAdapter {
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
            //System.out.println("pressed1" + e);
            this.keyPressedEntryPoint.insert( e );
        }

        public void keyReleased(KeyEvent e) {
            //System.out.println("released1" + e);
            this.keyReleasedEntryPoint.insert( e );
        }        
    }

}
