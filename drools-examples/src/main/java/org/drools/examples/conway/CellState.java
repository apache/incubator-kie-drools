package org.drools.examples.conway;

/**
 * <code>CellState</code> enumerates all of the valid states that a Cell may
 * be in.
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see Cell
 * @see CellGrid
 */
public class CellState
{

    public static final CellState LIVE = new CellState("LIVE");
    public static final CellState DEAD = new CellState("DEAD");

    private final String name;

    private CellState(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return "CellState: " + name;
    }
}
