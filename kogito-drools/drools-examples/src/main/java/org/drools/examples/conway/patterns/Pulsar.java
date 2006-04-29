package org.drools.examples.conway.patterns;

/**
 * The Pulsar <p/>
 * 
 * @see ConwayPattern
 * @see org.drools.examples.conway.CellGrid
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class Pulsar
    implements
    ConwayPattern {

    private boolean[][] grid = {{false, true, false}, {true, true, true}, {true, false, true}, {true, true, true}, {false, true, false}};

    /**
     * This method should return a 2 dimensional array of boolean that represent
     * a conway grid, with <code>true</code> values in the positions where
     * cells are alive
     * 
     * @return array representing a conway grid
     */
    public boolean[][] getPattern() {
        return grid;
    }

    /**
     * @return the name of this pattern
     */
    public String getPatternName() {
        return "Pulsar";
    }

    public String toString() {
        return getPatternName();
    }
}
