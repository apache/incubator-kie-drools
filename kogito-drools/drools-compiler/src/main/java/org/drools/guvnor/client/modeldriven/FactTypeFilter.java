package org.drools.guvnor.client.modeldriven;

import java.io.Serializable;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public interface FactTypeFilter extends PortableObject {

    /**
     * Indicates if a fact should be filtered or not.
     * @param originalFact the fact.
     * @return if a fact should be filtered or not.
     */
    public boolean filter(String originalFact);
}
