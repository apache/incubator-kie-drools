package org.kie.drl.engine.runtime.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoOutput;

/**
 * Generic <code>EfestoOutput</code> specific for DRL engines, to be subclassed depending on the actual implementation needs
 * @param <T>
 */
public class EfestoOutputDrl<T> extends AbstractEfestoOutput<T> {

    public EfestoOutputDrl(ModelLocalUriId modelLocalUriId, T outputData) {
        super(modelLocalUriId, outputData);
    }

}
