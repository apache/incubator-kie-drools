package org.kie.pmml.evaluator.core.model;

import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoOutput;

public class EfestoOutputPMML extends AbstractEfestoOutput<PMML4Result> {

    public EfestoOutputPMML(ModelLocalUriId modelLocalUriId, PMML4Result outputData) {
        super(modelLocalUriId, outputData);
    }
}
