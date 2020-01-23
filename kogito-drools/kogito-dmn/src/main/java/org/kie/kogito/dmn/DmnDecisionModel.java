package org.kie.kogito.dmn;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;

public class DmnDecisionModel implements DecisionModel {
    private final DMNRuntime dmnRuntime;
    private final String namespace;
    private final String name;

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name) {
        this.dmnRuntime = dmnRuntime;
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public DMNContext newContext(Map<String, Object> variables) {
        return new org.kie.dmn.core.impl.DMNContextImpl(variables);
    }

    @Override
    public DMNResult evaluateAll(DMNContext context) {
        return dmnRuntime.evaluateAll(dmnRuntime.getModel(namespace, name), context);
    }

    @Override
    public DMNResult evaluateDecisionService(DMNContext context, String decisionServiceName) {
        return dmnRuntime.evaluateDecisionService(dmnRuntime.getModel(namespace, name), context, decisionServiceName);
    }
}
