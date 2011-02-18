/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.broker.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JComponent;

import org.drools.examples.broker.model.StockTick;

import com.jgoodies.looks.FontSets;

/**
 * A simple component to show the incoming stock ticks
 * 
 * @author etirelli
 */
public class ScrollingBanner extends JComponent
    implements
    Runnable {

    private static final long serialVersionUID = 510l;
    private static final long SPACE = 10;

    private Queue<StockTick>  ticks;
    private volatile boolean  shutdown         = false;
    private AtomicInteger     headOffset       = new AtomicInteger( 0 );
    private StockTick         headTick         = null;

    public ScrollingBanner() {
        super();
        this.ticks = new ConcurrentLinkedQueue<StockTick>();
        this.setBackground( Color.black );
        this.setForeground( Color.GREEN );
        this.setFont( FontSets.getLogicalFontSet().getTitleFont() );
    }

    public void run() {
        shutdown = false;
        while ( !shutdown ) {
            repaint();
            try {
                Thread.sleep( 50 );
            } catch ( InterruptedException e ) {
                shutdown = true;
            }
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    public void addTick(StockTick tick) {
        ticks.add( tick );
    }

    public void paint(Graphics g) {
        final Dimension dim = this.getSize();
        final int y = ((int) (dim.height - g.getFontMetrics().getHeight()) / 2) + g.getFontMetrics().getHeight() -2 ;
        // erase previous
        g.setColor( Color.BLACK );
        g.fillRect( 0,
                    0,
                    dim.width,
                    dim.height );
        // redraw
        int width = 10;
        if ( headTick == null ) {
            headTick = ticks.poll();
            headOffset.set( 0 );
        }
        if ( headTick != null ) {
            String toDraw = headTick.toString().substring( headOffset.get() );
            width += SPACE + drawString( g,
                                      y,
                                      width,
                                      headTick,
                                      toDraw );
        }
        for ( StockTick tick : ticks ) {
            String toDraw = tick.toString();
            width += SPACE + drawString( g,
                                      y,
                                      width,
                                      tick,
                                      toDraw );
            if ( width > dim.width ) {
                if( headOffset.addAndGet(2) >= headTick.toString().length() ) {
                    headTick = null;
                }
                break;
            }
        }
    }

    private int drawString(Graphics g,
                           final int y,
                           int width,
                           StockTick tick,
                           String toDraw) {
        int size = g.getFontMetrics().stringWidth( toDraw );
        if ( tick.getDelta() < 0 ) {
            g.setColor( Color.red );
        } else {
            g.setColor( Color.green );
        }
        g.drawString( toDraw,
                      width,
                      y );
        return size;
    }
}
