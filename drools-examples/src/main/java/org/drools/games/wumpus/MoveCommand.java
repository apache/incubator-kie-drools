package org.drools.games.wumpus;

public class MoveCommand implements Command {
    private Move move;

    public MoveCommand(Move move) {
        super();
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "MoveCommand [move=" + move + "]";
    }
    
}
