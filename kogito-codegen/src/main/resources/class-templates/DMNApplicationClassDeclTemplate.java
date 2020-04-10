
public class DecisionModels implements org.kie.kogito.decision.DecisionModels {

    private final static org.kie.dmn.api.core.DMNRuntime dmnRuntime = org.kie.kogito.dmn.DMNKogito.createGenericDMNRuntime();

    public void init(org.kie.kogito.Application app) {
        app.config().decision().decisionEventListeners().listeners().forEach(dmnRuntime::addListener);
    }

    public org.kie.kogito.decision.DecisionModel getDecisionModel(java.lang.String namespace, java.lang.String name) {
        return new org.kie.kogito.dmn.DmnDecisionModel(dmnRuntime, namespace, name);
    }

}
