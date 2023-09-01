package org.kie.drl.engine.runtime.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;

/**
 * Generic <code>EfestoInput</code> specific for DRL engines, to be subclassed depending on the actual implementation needs
 * @param <T>
 */
public abstract class EfestoInputDrl<T> extends BaseEfestoInput<T> {

    // TODO {mfusco} Define a generic (instead of "String") that could reasonably contain any given "input" for rule execution
    protected EfestoInputDrl(ModelLocalUriId modelLocalUriId, T inputData) {
        super(modelLocalUriId, inputData);
    }


}
