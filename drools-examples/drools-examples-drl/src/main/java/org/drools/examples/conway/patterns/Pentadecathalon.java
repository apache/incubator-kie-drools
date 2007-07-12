package org.drools.examples.conway.patterns;

/**
 * The Pentadecathalon <p/>
 * 
 * @see ConwayPattern
 * @see org.drools.examples.conway.CellGrid
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class Pentadecathalon
    implements
    ConwayPattern {

    private final boolean[][] grid = {{true, true, true, true, true, true, true, true, true}};

    /**
     * This method should return a 2 dimensional array of boolean that represent
     * a conway grid, with <code>true</code> values in the positions where
     * cells are alive
     * 
     * @return array representing a conway grid
     */
    public boolean[][] getPattern() {
        return this.grid;
    }

    /**
     * @return the name of this pattern
     */
    public String getPatternName() {
        return "Pentadecathalon";
    }

    public String toString() {
        return getPatternName();
    }
}
