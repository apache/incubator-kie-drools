package org.kie.kogito.dmn;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.decision.DecisionExecutionIdUtils;
import org.kie.kogito.decision.DecisionModel;

public class DmnDecisionModel implements DecisionModel {

    private final DMNRuntime dmnRuntime;
    private final ExecutionIdSupplier execIdSupplier;
    private final DMNModel dmnModel;

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name) {
        this(dmnRuntime, namespace, name, null);
    }

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name, ExecutionIdSupplier execIdSupplier) {
        this.dmnRuntime = dmnRuntime;
        this.execIdSupplier = execIdSupplier;
        this.dmnModel = dmnRuntime.getModel(namespace, name);
        if (dmnModel == null) {
            throw new IllegalStateException("DMN model '" + name + "' not found with namespace '" + namespace + "' in the inherent DMNRuntime.");
        }
    }

    @Override
    public DMNContext newContext(Map<String, Object> variables) {
        return new org.kie.dmn.core.impl.DMNContextImpl(variables);
    }

    @Override
    public DMNResult evaluateAll(DMNContext context) {
        return dmnRuntime.evaluateAll(dmnModel, context);
    }

    @Override
    public DMNResult evaluateDecisionService(DMNContext context, String decisionServiceName) {
        return dmnRuntime.evaluateDecisionService(dmnModel, inject(context), decisionServiceName);
    }

    private DMNContext inject(DMNContext context) {
        return execIdSupplier != null
                ? DecisionExecutionIdUtils.inject(context, execIdSupplier)
                : context;
    }

    @Override
    public DMNModel getDMNModel() {
        return dmnModel;
    }
}
