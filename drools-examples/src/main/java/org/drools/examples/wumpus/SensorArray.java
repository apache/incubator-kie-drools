package org.drools.examples.wumpus;

public class SensorArray {
    private boolean    smellStench;
    private boolean    seeGlitter;    
    private boolean    feelBreeze;
    private boolean    feelBump;
    
    public boolean isSmellStench() {
        return smellStench;
    }
    
    public void setSmellStench(boolean smellStench) {
        this.smellStench = smellStench;
    }
    
    public boolean isSeeGlitter() {
        return seeGlitter;
    }
    
    public void setSeeGlitter(boolean seeGlitter) {
        this.seeGlitter = seeGlitter;
    }
    
    public boolean isFeelBreeze() {
        return feelBreeze;
    }
    
    public void setFeelBreeze(boolean feelBreeze) {
        this.feelBreeze = feelBreeze;
    }
    
    public boolean isFeelBump() {
        return feelBump;
    }
    
    public void setFeelBump(boolean feelBump) {
        this.feelBump = feelBump;
    }
    
    @Override
    public String toString() {
        return "SensorArray [smellStench=" + smellStench + ", seeGlitter=" + seeGlitter + ", feelBreeze=" + feelBreeze + ", feelBump=" + feelBump + "]";
    }
    

    
}
