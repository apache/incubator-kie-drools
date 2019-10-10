
public class DecisionModels implements org.kie.kogito.decision.DecisionModels {

    static org.kie.dmn.api.core.DMNRuntime dmnRuntime = org.kie.kogito.dmn.DMNKogito.createGenericDMNRuntime();

    public org.kie.kogito.decision.DecisionModel getDecisionModel(java.lang.String namespace, java.lang.String name) {
        return new org.kie.kogito.decision.DecisionModel() {

            @Override
            public org.kie.dmn.api.core.DMNContext newContext(java.util.Map<String, Object> variables) {
                return new org.kie.dmn.core.impl.DMNContextImpl(variables);
            }

            @Override
            public org.kie.dmn.api.core.DMNResult evaluateAll(org.kie.dmn.api.core.DMNContext context) {
                return dmnRuntime.evaluateAll(dmnRuntime.getModel(namespace, name), context);
            }
            
            @Override
            public org.kie.dmn.api.core.DMNResult evaluateDecisionService(org.kie.dmn.api.core.DMNContext context, java.lang.String decisionServiceName) {
                return dmnRuntime.evaluateDecisionService(dmnRuntime.getModel(namespace, name), context, decisionServiceName);
            }
        };
    }
}
