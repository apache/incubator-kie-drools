package org.kie.efesto.runtimemanager.api.service;

import java.util.Collection;

import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;

public interface RuntimeManager {

    /**
     * Produce a <code>Collection&lt;EfestoOutput&gt;</code> from the given <code>EfestoInput</code>
     * @param context
     * @param toEvaluate
     * @return
     */
    Collection<EfestoOutput> evaluateInput(EfestoRuntimeContext context,
                                           EfestoInput... toEvaluate);
}
