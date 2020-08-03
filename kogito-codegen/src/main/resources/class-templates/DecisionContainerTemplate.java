import java.util.function.Function;

import org.kie.api.runtime.KieRuntimeFactory;

public class DecisionModels implements org.kie.kogito.decision.DecisionModels {

    private final static java.util.function.Function<java.lang.String, org.kie.api.runtime.KieRuntimeFactory> kieRuntimeFactoryFunction = PredictionModels.kieRuntimeFactoryFunction;
    private final static org.kie.dmn.api.core.DMNRuntime dmnRuntime = org.kie.kogito.dmn.DMNKogito.createGenericDMNRuntime(kieRuntimeFactoryFunction);
    private final static org.kie.kogito.ExecutionIdSupplier execIdSupplier = null;

    public DecisionModels(org.kie.kogito.Application app) {
        app.config().decision().decisionEventListeners().listeners().forEach(dmnRuntime::addListener);
    }

    public org.kie.kogito.decision.DecisionModel getDecisionModel(java.lang.String namespace, java.lang.String name) {
        return new org.kie.kogito.dmn.DmnDecisionModel(dmnRuntime, namespace, name, execIdSupplier);
    }

}
