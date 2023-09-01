package org.drools.games.pong;

public class PongGame {
    private Player playerOne;
    private Player Playertwo;
    
    public PongGame(Player playerOne,
                    Player playertwo) {        
        this.playerOne = playerOne;
        Playertwo = playertwo;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayertwo() {
        return Playertwo;
    }

    public void setPlayertwo(Player playertwo) {
        Playertwo = playertwo;
    }  
}
