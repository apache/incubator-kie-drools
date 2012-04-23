package org.drools.games.pong;

public class Collision {
    
    private CollisionType type;
    
    private Player        player;

    public Collision(Player player,
                     CollisionType type) {
        this.player = player;        
        this.type = type;
    }

    public CollisionType getType() {
        return type;
    }

    public void setType(CollisionType type) {
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Collision other = (Collision) obj;
        if ( player == null ) {
            if ( other.player != null ) return false;
        } else if ( !player.equals( other.player ) ) return false;
        if ( type != other.type ) return false;
        return true;
    }
    
}
