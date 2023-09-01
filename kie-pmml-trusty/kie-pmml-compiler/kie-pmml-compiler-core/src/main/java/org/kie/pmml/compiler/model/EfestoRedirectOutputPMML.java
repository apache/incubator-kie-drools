package org.kie.pmml.compiler.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoRedirectOutput;

public class EfestoRedirectOutputPMML extends EfestoRedirectOutput<String> {

    public EfestoRedirectOutputPMML(ModelLocalUriId modelLocalUriId, String modelFile) {
        super(modelLocalUriId, "drl", modelFile);
    }
}
