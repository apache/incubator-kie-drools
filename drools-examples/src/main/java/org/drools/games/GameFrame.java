package org.drools.games;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final Object redrawLock = new Object();

    public GameFrame() throws HeadlessException {
    }

    public GameFrame(String title) throws HeadlessException {
        super(title);
    }

    @Override
    public void paint(Graphics g) {
        // this will iterate the children, calling paintComponent
        super.paint(g);
        Toolkit.getDefaultToolkit().sync();
        resume(); // all the children are redrawn, so resume
    }

    public void waitForPaint() {
        try {
            synchronized (redrawLock) {
                repaint();
                redrawLock.wait();
            }
        } catch (InterruptedException e) {
        }
    }

    private void resume() {
        synchronized (redrawLock) {
            redrawLock.notify();
        }
    }
}
