package org.kie.kogito.pmml.rest;

import java.util.Map;

import org.kie.kogito.Application;
import org.kie.kogito.prediction.PredictionModel;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/$nameURL$")
public class PMMLRestResourceTemplate {

    Application application;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object pmml(@RequestBody(required = false) Map<String, Object> variables) {
        PredictionModel prediction = application.predictionModels().getPredictionModel("$modelName$");
        return prediction.evaluateAll(prediction.newContext(variables));
    }
}