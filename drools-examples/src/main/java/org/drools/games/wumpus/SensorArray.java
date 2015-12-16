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

package org.drools.games.wumpus;

public class SensorArray {
    private boolean    smellStench;
    private boolean    seeGlitter;    
    private boolean    feelBreeze;
    private boolean    feelBump;
    private boolean    hearScream;
    
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

    public boolean isHearScream() {
        return hearScream;
    }

    public void setHearScream(boolean hearScream) {
        this.hearScream = hearScream;
    }

    @Override
    public String toString() {
        return "SensorArray [smellStench=" + smellStench + ", seeGlitter=" + seeGlitter + ", feelBreeze=" + feelBreeze + ", feelBump=" + feelBump + ", hearScream=" + hearScream + "]";
    }
    


    
}
