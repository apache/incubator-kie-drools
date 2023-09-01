package org.drools.examples.sudoku;

/**
 * Abstract class for "numbered" cell groups: rows and columns.
 */
public abstract class CellFile extends CellGroup {
    
    private int number;
    
    /**
     * Constructor.
     * 
     * @param number thw row or column number.
     */
    protected CellFile(int number) {
        super();
        this.number = number;
    }

    /**
     * Retrieves the row or column number.
     * @return an int value
     */
    public int getNumber() {
        return number;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < getCells().size(); i++) {
            String cStr = getCells().get( i ).toString();
            sb.append(del).append(cStr);
            del = ", ";
        }
        return sb.toString();
    }
}
