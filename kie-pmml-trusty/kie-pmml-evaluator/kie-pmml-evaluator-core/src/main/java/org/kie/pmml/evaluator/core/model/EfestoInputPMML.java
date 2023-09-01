package org.kie.pmml.evaluator.core.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

public class EfestoInputPMML extends BaseEfestoInput<PMMLRuntimeContext> {

    public EfestoInputPMML(ModelLocalUriId modelLocalUriId, PMMLRuntimeContext inputData) {
        super(modelLocalUriId, inputData);
    }
}
