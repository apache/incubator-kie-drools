package org.drools.games.invaders;


public class FPSTimer {
    private long time;
    private long lastTime;
    private int  frames;
    private int  lastFrames;
    private double correctionRatio;
    private int desiredFPS = 60;

    public FPSTimer() {
        time = System.currentTimeMillis();
        lastTime = time;
    }

    public void incFrame() {
        long currentTime = System.currentTimeMillis();

        if ( currentTime - time >= 1000 ) {
            // more than 1s
            correctionRatio = (double) frames / (double) desiredFPS;
            System.out.println( "fps :" + frames + "/s (" +  (currentTime - time) + ")" +  correctionRatio );
            frames = 0;
            time = currentTime;
            lastTime = currentTime;
            lastFrames = frames;
        } else {

            int interval = 50;

            int actualFrames = frames - lastFrames;
            int expectedFrames =  desiredFPS / (1000/interval);

            long timeDiff = currentTime - lastTime;
            if (timeDiff < interval ) {
                if (actualFrames >= expectedFrames) {
                    // done enough frames for this interval, so pause
                    try {
                        Thread.sleep((long) ((interval - timeDiff)*correctionRatio));
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Kaboom");
                    }
                    lastTime = currentTime;
                    lastFrames = frames;
                }
            } else {
                lastTime = currentTime;
                lastFrames = frames;
            }

        }
        frames++;
    }
}
