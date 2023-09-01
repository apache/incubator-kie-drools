package org.drools.games;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private BufferedImage backbuffer;
    private Graphics2D g2d;
    private String name;
    private Color color;

    public GamePanel(String name, Color color) {
        this.name = name;
        this.color = color;
    }


    public BufferedImage getBufferedImage() {
        if (backbuffer == null) {
            backbuffer = new BufferedImage(getWidth(), getHeight(),
                                           BufferedImage.TYPE_INT_RGB);
            Graphics2D g = getGraphics2D();
            g.setColor( color ); // background
            g.fillRect( 0, 0, getWidth(), getHeight() );
            disposeGraphics2D();
        }
        return backbuffer;
    }

    public Graphics2D getGraphics2D() {
        if ( g2d == null ) {
            g2d = (Graphics2D) backbuffer.getGraphics();
        }
        return g2d;
    }

    public void disposeGraphics2D() {
        if ( g2d != null ) {
            g2d.dispose();
            g2d = null;
        }
    }

    private long time;
    private int frameCount;
    public synchronized void paintComponent(Graphics g) {
        // track approx frames per second
        long currentTime = System.currentTimeMillis();
        if ( currentTime-time >= 10000 || time == 0) {
            System.out.println( "fps(" + name + ") :" + (frameCount/10) );
            frameCount = 0;
            time = currentTime;
        }
        frameCount++;

        // paint the buffered image to the graphics
        g.drawImage(backbuffer, 0, 0, this);
        Toolkit.getDefaultToolkit().sync();
    }
}
