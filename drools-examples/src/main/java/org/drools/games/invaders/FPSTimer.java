package org.drools.games.invaders;


public class FPSTimer {
    private long lastTime;
    private long frameDiff;
    private long statsTime;
    private long frames = 0;

    public FPSTimer(long frameDiff) {
        this.frameDiff = frameDiff;
        lastTime = System.currentTimeMillis();
        statsTime = System.currentTimeMillis();
    }

    public void incFrame() {
        if (System.currentTimeMillis()-statsTime > 1000) {
            System.out.println( "fps :" + frames + "/s (" +  (System.currentTimeMillis() - statsTime) + ")" );
            frames = 0;
            statsTime = System.currentTimeMillis();
        }
        while (System.currentTimeMillis()-lastTime<frameDiff) {
            // do nothing.
        }
        frames++;
        lastTime = System.currentTimeMillis();
    }
}   
