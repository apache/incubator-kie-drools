package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;

public class AfterEvaluateAllEventImpl implements AfterEvaluateAllEvent {

    private String modelNamespace;
    private String modelName;
    private DMNResult result;

    public AfterEvaluateAllEventImpl(String modelNamespace, String modelName, DMNResult result) {
        this.modelNamespace = modelNamespace;
        this.modelName = modelName;
        this.result = result;
    }

    @Override
    public String getModelNamespace() {
        return modelNamespace;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public DMNResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "AfterEvaluateAllEventImpl{ modelNamespace=" + modelNamespace + ", modelName=" + modelName + "}";
    }

}
