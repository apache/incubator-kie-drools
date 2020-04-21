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
    private final String namespace;
    private final String name;
    private final ExecutionIdSupplier execIdSupplier;

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name) {
        this.dmnRuntime = dmnRuntime;
        this.namespace = namespace;
        this.name = name;
        this.execIdSupplier = null;
    }

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name, ExecutionIdSupplier execIdSupplier) {
        this.dmnRuntime = dmnRuntime;
        this.namespace = namespace;
        this.name = name;
        this.execIdSupplier = execIdSupplier;
    }

    @Override
    public DMNContext newContext(Map<String, Object> variables) {
        return new org.kie.dmn.core.impl.DMNContextImpl(variables);
    }

    @Override
    public DMNResult evaluateAll(DMNContext context) {
        DMNModel dmnModel = dmnRuntime.getModel(namespace, name);
        if (dmnModel == null) {
            throw new IllegalArgumentException("DMN model '" + name + "' not found with namespace '" + namespace + "'");
        }
        return dmnRuntime.evaluateAll(dmnModel, inject(context));
    }

    @Override
    public DMNResult evaluateDecisionService(DMNContext context, String decisionServiceName) {
        DMNModel dmnModel = dmnRuntime.getModel(namespace, name);
        if (dmnModel == null) {
            throw new IllegalArgumentException("DMN model '" + name + "' not found with namespace '" + namespace + "'");
        }
        return dmnRuntime.evaluateDecisionService(dmnModel, inject(context), decisionServiceName);
    }

    private DMNContext inject(DMNContext context) {
        return execIdSupplier != null
                ? DecisionExecutionIdUtils.inject(context, execIdSupplier)
                : context;
    }

}
