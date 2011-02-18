package org.drools;

public class Neighbor {
    
    private Cell cell;
    private Cell neighbor;
    public Neighbor(Cell cell,
                    Cell neighbor) {
        super();
        this.cell = cell;
        this.neighbor = neighbor;
    }
    public Cell getCell() {
        return cell;
    }
    public void setCell(Cell cell) {
        this.cell = cell;
    }
    public Cell getNeighbor() {
        return neighbor;
    }
    public void setNeighbor(Cell neighbor) {
        this.neighbor = neighbor;
    }
    
    @Override
    public String toString() {
        return "[" + cell + " <=> " + neighbor +"]";
    }
    
}
