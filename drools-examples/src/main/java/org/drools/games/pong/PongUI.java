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

    public void drawGame(Ball ball, Bat bat1, Bat bat2, Player p1, Player p2) {
        clearMovingBall(ball);
        clearBat(bat1);
        clearBat(bat2);

        drawScore( p1, 100 );
        drawScore( p2, pconf.getTableWidth()-120 );
        drawTable();

        drawBall(ball);
        drawBat(bat1);
        drawBat(bat2);
        repaint();
    }

    public void clearBall(Ball ball) {
        Graphics g = getGraphics();
        g.setColor( Color.BLACK ); // background
        g.clearRect(ball.getX(), ball.getY(), ball.getWidth(), ball.getWidth());
    }

    public void clearMovingBall(Ball ball) {
        Graphics g = getGraphics();
        g.setColor( Color.BLACK ); // background
        g.clearRect(ball.getX()-(ball.getDx()*ball.getSpeed()), ball.getY()-(ball.getDy()*ball.getSpeed()), ball.getWidth(), ball.getWidth());
    }

    public void clearBat(Bat bat) {
        Graphics g = getGraphics();
        g.setColor( Color.BLACK ); // background
        g.clearRect(bat.getX(), bat.getY()-bat.getDy(), bat.getWidth(), bat.getHeight());
    }

    public void drawTable() {
        Graphics tableG = getGraphics(); //ui.getTablePanel().getTableG();
        tableG.setColor( Color.WHITE ); // background

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
