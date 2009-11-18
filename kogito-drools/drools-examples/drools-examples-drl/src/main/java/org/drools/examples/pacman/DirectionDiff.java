package org.drools.examples.pacman;

public class DirectionDiff {
    private Character fromChar;
    private Character toChar;
    private int col;
    private int row;
    private int colDiff;
    private int rowDiff;
    
    public DirectionDiff(Character fromChar,
                         Character toChar,
                         int col,
                         int row,
                         int colDiff,
                         int rowDiff) {
        this.fromChar = fromChar;
        this.toChar = toChar;
        this.col = col;
        this.row = row;
        this.colDiff = colDiff;
        this.rowDiff = rowDiff;
    }

    public Character getFromChar() {
        return fromChar;
    }

    public Character getToChar() {
        return toChar;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getColDiff() {
        return colDiff;
    }

    public int getRowDiff() {
        return rowDiff;
    }

    public String toString() {
        return "from: " + fromChar + " to: " + toChar + " col: " + col + " row: " + row + " colDiff: " + colDiff + " rowDiff: " + rowDiff; 
    }
    
}
