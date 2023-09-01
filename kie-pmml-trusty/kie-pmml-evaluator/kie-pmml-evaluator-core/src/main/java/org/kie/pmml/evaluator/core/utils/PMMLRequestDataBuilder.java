package org.kie.pmml.evaluator.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;

public class PMMLRequestDataBuilder {

    private String correlationId;
    private String modelName;
    private List<ParameterInfo<?>> parameters;

    public PMMLRequestDataBuilder(String correlationId, String modelName) {
        this.correlationId = correlationId;
        this.modelName = modelName;
        parameters = new ArrayList<>();
    }

    public <T> PMMLRequestDataBuilder addParameter(String paramName, T value, Class<T> clazz) {
        parameters.add(new ParameterInfo<>(correlationId, paramName, clazz, value));
        return this;
    }

    public PMMLRequestData build() {
        PMMLRequestData data = new PMMLRequestData(correlationId, modelName);
        parameters.forEach(data::addRequestParam);
        return data;
    }
}
