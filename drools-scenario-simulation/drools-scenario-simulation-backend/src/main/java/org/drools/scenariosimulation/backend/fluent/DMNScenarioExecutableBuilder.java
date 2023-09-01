package org.drools.scenariosimulation.backend.fluent;

import org.drools.scenariosimulation.backend.util.DMNSimulationUtils;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

public class DMNScenarioExecutableBuilder {

    public static final String DEFAULT_APPLICATION = "defaultApplication";
    public static final String DMN_RESULT = "dmnResult";
    public static final String DMN_MODEL = "dmnModel";

    private final DMNRuntime dmnRuntime;
    private final DMNContext dmnContext;
    private DMNModel dmnModel;

    private DMNScenarioExecutableBuilder(KieContainer kieContainer, String applicationName) {
        dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);
        dmnContext = dmnRuntime.newContext();
    }

    private DMNScenarioExecutableBuilder(KieContainer kieContainer) {
        this(kieContainer, DEFAULT_APPLICATION);
    }

    public static DMNScenarioExecutableBuilder createBuilder(KieContainer kieContainer, String applicationName) {
        return new DMNScenarioExecutableBuilder(kieContainer, applicationName);
    }

    public static DMNScenarioExecutableBuilder createBuilder(KieContainer kieContainer) {
        return new DMNScenarioExecutableBuilder(kieContainer);
    }

    public void setActiveModel(String path) {
        dmnModel = DMNSimulationUtils.extractDMNModel(dmnRuntime, path);
    }

    public void setValue(String key, Object value) {
        dmnContext.set(key, value);
    }

    public RequestContext run() {
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        RequestContext requestContext = ExecutableRunner.create().createContext();
        requestContext.setResult(dmnResult);
        requestContext.setOutput(DMN_MODEL, dmnModel);
        requestContext.setOutput(DMN_RESULT, dmnResult);
        return requestContext;
    }
}
