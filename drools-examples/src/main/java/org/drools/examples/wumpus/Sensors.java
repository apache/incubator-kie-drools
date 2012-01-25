package org.drools.examples.wumpus;

public class Sensors {
    private boolean    smellStench;
    private boolean    feelBreeze;
    private boolean    seeGlitter;
    

    public boolean isSmellStench() {
        return smellStench;
    }

    public void setSmellStench(boolean smellStench) {
        this.smellStench = smellStench;
    }

    @Override
    public String toString() {
        return "Sensors [smellStench=" + smellStench + ", feelBreeze=" + feelBreeze + ", seeGlitter=" + seeGlitter + "]";
    }

    public boolean isFeelBreeze() {
        return feelBreeze;
    }

    public void setFeelBreeze(boolean feelBreeze) {
        this.feelBreeze = feelBreeze;
    }

    public boolean isSeeGlitter() {
        return seeGlitter;
    }

    public void setSeeGlitter(boolean seeGlitter) {
        this.seeGlitter = seeGlitter;
    }    
}
