package org.kie.pmml.api.runtime;

import org.kie.efesto.common.api.listener.EfestoListener;
import org.kie.pmml.api.models.PMMLStep;

public interface PMMLListener extends EfestoListener {



    /**
     * Method invoked when a <code>PMMLStep</code> is executed
     * @param step
     */
    void stepExecuted(PMMLStep step);

}
