package org.drools.examples.conway.patterns;

/**
 * Represents a simple glider
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see ConwayPattern
 * @see org.drools.examples.conway.CellGrid
 */
public class SimpleGlider
    implements
    ConwayPattern
{

    private boolean[][] grid = {{false, true, false}, {true, false, false}, {true, true, true}};

    /**
     * This method should return a 2 dimensional array of boolean that represent
     * a conway grid, with <code>true</code> values in the positions where
     * cells are alive
     * 
     * @return array representing a conway grid
     */
    public boolean[][] getPattern()
    {
        return grid;
    }

    /**
     * @return the name of this pattern
     */
    public String getPatternName()
    {
        return "Simple Glider";
    }

    public String toString()
    {
        return getPatternName( );
    }
}
