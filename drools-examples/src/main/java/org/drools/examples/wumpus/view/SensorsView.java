package org.drools.examples.wumpus.view;

public class SensorsView {
    private boolean    smellStench;
    private boolean    feelBreeze;
    private boolean    seeGlitter;
    private boolean    feelBump;
    private boolean    hearScream;
    

    public boolean isSmellStench() {
        return smellStench;
    }

    public void setSmellStench(boolean smellStench) {
        this.smellStench = smellStench;
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

    public boolean isFeelBump() {
        return feelBump;
    }

    public void setFeelBump(boolean feelBump) {
        this.feelBump = feelBump;
    }

    public boolean isHearScream() {
        return hearScream;
    }

    public void setHearScream(boolean hearScream) {
        this.hearScream = hearScream;
    }

    @Override
    public String toString() {
        return "SensorsView [smellStench=" + smellStench + ", feelBreeze=" + feelBreeze + ", seeGlitter=" + seeGlitter + ", feelBump=" + feelBump + ", hearScream=" + hearScream + "]";
    }   
        
}
