package org.kie.kogito.pmml.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.Application;

@Path("/$nameURL$")
public class PMMLRestResourceTemplate {

    Application application;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object pmml(java.util.Map<String, Object> variables) {
        org.kie.kogito.prediction.PredictionModel prediction = application.predictionModels().getPredictionModel("$modelName$");
        return prediction.evaluateAll(prediction.newContext(variables));
    }
}