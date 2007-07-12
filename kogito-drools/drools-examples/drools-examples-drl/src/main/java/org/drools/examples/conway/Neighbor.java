package org.drools.examples.conway;

public class Neighbor {
    private Cell cell;
    private Cell neighbor;
    
    public Neighbor(Cell cell, Cell neighbor) {
        this.cell = cell;
        this.neighbor = neighbor;
    }

    public Cell getCell() {
        return cell;
    }

    public Cell getNeighbor() {
        return neighbor;
    }       
    
    public String toString() {
        return "cell '"+ this.cell + "' neighbour '" + this.neighbor + "'"; 
    }
    
}
