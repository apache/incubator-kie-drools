package org.kie.efesto.runtimemanager.api.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A generic <i>result</i> of evaluation
 */
public interface EfestoOutput<T> {

    /**
     * The unique, full identifier of a given model' resource
     * @return
     */
    ModelLocalUriId getModelLocalUriId();

    T getOutputData();
}
