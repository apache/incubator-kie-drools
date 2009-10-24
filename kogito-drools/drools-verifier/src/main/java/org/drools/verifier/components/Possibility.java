package org.drools.verifier.components;

import java.util.Set;

import org.drools.verifier.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public interface Possibility
    extends
    Cause {

    public Set<Cause> getItems();

    public int getAmountOfItems();
}
