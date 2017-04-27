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

package org.drools.games.pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.drools.games.GameConfiguration;
import org.drools.games.GameUI;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.EntryPoint;

public class PongUI extends GameUI {
    private PongConfiguration pconf;

    public PongUI(KieSession ksession, GameConfiguration conf) {
        super(ksession, conf);
        this.pconf = (PongConfiguration) conf;
    }
    
    @Override
    public void init() {
        super.init();
        registerWindowListenerOnFrame(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                getKieSession().halt();
            }
        });
    }

    public void drawGame(Ball ball, Bat bat1, Bat bat2, Player p1, Player p2) {
        Graphics tableG = getGraphics(); //ui.getTablePanel().getTableG();
        tableG.setColor( Color.BLACK ); // background
        tableG.fillRect(0,0, getWidth(), getHeight() );

        tableG.setColor( Color.WHITE ); // background

        drawScore( p1, 100 );
        drawScore( p2, pconf.getTableWidth()-120 );
        drawTable();

        drawBall(ball);
        drawBat(bat1);
        drawBat(bat2);
        repaint();
    }

    public void drawTable() {
        Graphics tableG = getGraphics(); //ui.getTablePanel().getTableG();

        int padding = pconf.getPadding();
        int tableWidth = pconf.getTableWidth();
        int tableHeight = pconf.getTableHeight();
        int sideLineWidth = pconf.getSideLineWidth();

        tableG.fillRect( padding, padding,
                         tableWidth-(padding*2), sideLineWidth );
        tableG.fillRect( padding, tableHeight-padding-sideLineWidth,
                         tableWidth-(padding*2), sideLineWidth );
        // draw dash line net
        int netWidth = pconf.getNetWidth();
        int gap = pconf.getNetGap();
        int dash = pconf.getNetDash();
        int x = (tableWidth/2) - (netWidth/2);
        for (int i = 0; i < tableHeight; i = i + dash + gap) {
            tableG.fillRect( (int) x, i, netWidth, dash );
        }
    }

    public void drawBall(Ball ball) {
        Graphics g = getGraphics();
        g.setColor( Color.WHITE ); // background
        g.fillOval( ball.getX(), ball.getY(), ball.getWidth(), ball.getWidth() );
    }

    public void drawBat(Bat bat) {
        Graphics g = getGraphics();
        g.setColor( Color.WHITE ); // background
        g.fillRect( bat.getX(), bat.getY(), bat.getWidth(), bat.getHeight() );
    }

    public void drawScore(Player p, int x) {
        Graphics g = getGraphics(); //ui.getTablePanel().getTableG();
        int y = (pconf.boundedTop()+ 60);

        g.setColor( Color.BLACK ); // background
        g.fillRect( x, y-60, 90, 90 );

        FontRenderContext frc = ((Graphics2D)g).getFontRenderContext();
        Font f = new Font("Monospaced",Font.BOLD, 70);
        String s = "" + p.getScore();
        TextLayout tl = new TextLayout(s, f, frc);
        g.setColor( Color.WHITE );
        tl.draw(((Graphics2D)g), x, y );
    }
}
