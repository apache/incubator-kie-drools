package org.kie.dmn.kogito.quarkus.example;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.Application;

@Path("/$nameURL$")
public class DMNRestResourceTemplate {

    Application application;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public org.kie.kogito.dmn.rest.DMNResult dmn(java.util.Map<String, Object> variables) {
        org.kie.kogito.decision.DecisionModel decision = application.decisionModels().getDecisionModel("$modelNamespace$", "$modelName$");
        return new org.kie.kogito.dmn.rest.DMNResult(decision.evaluateAll(decision.newContext(variables)));
    }
}
