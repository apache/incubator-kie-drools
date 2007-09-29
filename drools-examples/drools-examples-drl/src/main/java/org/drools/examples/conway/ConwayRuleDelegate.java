package org.drools.examples.conway;

import org.drools.StatefulSession;

public interface ConwayRuleDelegate {

    public abstract StatefulSession getSession();

    public abstract void init();

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#nextGeneration()
     */
    public abstract boolean nextGeneration();

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#killAll()
     */
    public abstract void killAll();

}