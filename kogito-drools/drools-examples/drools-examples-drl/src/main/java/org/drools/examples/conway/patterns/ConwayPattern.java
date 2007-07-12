package org.drools.examples.conway.patterns;

import java.io.Serializable;

/**
 * A <code>ConwayPattern</code> describes the state of a conway grid.
 * <code>ConwayPattern</code> objects are useful for persisting grid states
 * for recall later.
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public interface ConwayPattern
    extends
    Serializable {

    /**
     * This method should return a 2 dimensional array of boolean that represent
     * a conway grid, with <code>true</code> values in the positions where
     * cells are alive
     * 
     * @return array representing a conway grid
     */
    public boolean[][] getPattern();

    /**
     * @return the name of this pattern
     */
    public String getPatternName();
}
