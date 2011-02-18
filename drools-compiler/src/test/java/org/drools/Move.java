package org.drools;

public class Move {
    private int first;
    private int second;
    
    
    public Move(int first,
                int second) {
        super();
        this.first = first;
        this.second = second;
    }
    
    public int getFirst() {
        return first;
    }
    public void setFirst(int first) {
        this.first = first;
    }
    public int getSecond() {
        return second;
    }
    public void setSecond(int second) {
        this.second = second;
    }
    
    public String toString() {
        return "Move("+first+","+second+")";
    }

}
