package org.kie.pmml.api.compilation;

import java.util.Set;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.pmml.api.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;

public interface PMMLCompilationContext extends EfestoCompilationContext<PMMLListener>,
                                                PMMLContext<PMMLListener> {

    Set<ModelLocalUriId> getModelLocalUriIdsForFile();
}
