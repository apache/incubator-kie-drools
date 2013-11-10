package org.drools.games.numberguess;

public class Game {
    private int biggest;
    private int smallest;
    private int guessCount;

    public void begin() {
        this.guessCount = 0;
        this.biggest = 0;
        this.smallest = 100;
    }

    public void incrementGuessCount() {
        guessCount++;
    }

    public int getBiggest() {
        return this.biggest;
    }

    public int getSmallest() {
        return this.smallest;
    }

    public int getGuessCount() {
        return this.guessCount;
    }

    public void setGuessCount(int guessCount) {
        this.guessCount = guessCount;
    }

    public void setBiggest(int biggest) {
        this.biggest = biggest;
    }

    public void setSmallest(int smallest) {
        this.smallest = smallest;
    }

    @Override
    public String toString() {
        return "Game{" +
               "biggest=" + biggest +
               ", smallest=" + smallest +
               ", guessCount=" + guessCount +
               '}';
    }
}
