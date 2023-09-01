package org.kie.efesto.runtimemanager.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import static org.kie.efesto.runtimemanager.core.service.RuntimeManagerUtils.getOptionalOutput;

public class RuntimeManagerImpl implements RuntimeManager {

    @Override
    public Collection<EfestoOutput> evaluateInput(EfestoRuntimeContext context, EfestoInput... toEvaluate) {
        if (toEvaluate.length == 1) { // minor optimization for the (most typical) case with 1 input
            return getOptionalOutput(context, toEvaluate[0]).map(Collections::singletonList).orElse(Collections.emptyList());
        }
        Collection<EfestoOutput> toReturn = new ArrayList<>();
        for (EfestoInput efestoInput : toEvaluate) {
            getOptionalOutput(context, efestoInput).ifPresent(toReturn::add);
        }
        return toReturn;
    }


}
