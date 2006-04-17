package org.drools.spi;

import java.io.Serializable;

/**
 * Interface used to expose generic information on Rete nodes outside of he package. It is used
 * for exposing information events.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public interface ReteooNode
    extends
    Serializable {

    /**
     * Returns the unique id that represents the node in the Rete network
     * @return
     *      unique int value
     */
    public int getId();
}
