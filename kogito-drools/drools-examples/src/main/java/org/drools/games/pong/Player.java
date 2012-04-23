package org.drools.games.pong;

import org.drools.definition.type.PropertyReactive;

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
