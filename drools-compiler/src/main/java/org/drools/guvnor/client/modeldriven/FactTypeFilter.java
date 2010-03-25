package org.drools.guvnor.client.modeldriven;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public interface FactTypeFilter {

    /**
     * Indicates if a fact should be filtered or not.
     * @param originalFact the fact.
     * @return if a fact should be filtered or not.
     */
    public boolean filter(String originalFact);
}
