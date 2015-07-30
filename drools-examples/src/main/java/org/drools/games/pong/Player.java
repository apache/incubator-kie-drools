/*
 * Copyright 2015 JBoss Inc
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

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Player {
    private PlayerId id;
    private int      score;
    private Bat      bat;
    
    public Player(PlayerId id,
                  Bat bat) {
        this.id = id;
        this.bat = bat;
        this.bat.setPlayer( this );
    }

    public PlayerId getId() {
        return id;
    }

    public void setId(PlayerId id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Bat getBat() {
        return bat;
    }

    public void setBat(Bat bat) {
        this.bat = bat;
    }  
    
}
